package com.berrontech.dsensor.dataserver.upgrade;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.util.NumberUtils;
import com.berrontech.dsensor.dataserver.common.util.VersionUtils;
import com.berrontech.dsensor.dataserver.conf.DatabaseUpgradeConfiguration;
import com.berrontech.dsensor.dataserver.repository.mapper.DatabaseMetaDataMapper;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/7/7 19:42
 * Class Name: MysqlDatabaseUpgrader
 * Author: Levent8421
 * Description:
 * MySQL 数据库更新组件实现
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class MysqlDatabaseUpgrader implements DatabaseUpgrader, ApplicationContextAware {
    /**
     * 查询所有表名
     */
    private static final String TABLE_NAME_APPLICATION_CONFIG = "t_application_config";
    /**
     * 数据库更新脚本模板
     */
    private static final String DATABASE_UPGRADE_SCRIPT_FILE_NAME_TEMPLATE = "%s/upgrade_scada_wsa_%s.sql";
    /**
     * 更新脚本格式相关配置
     */
    private static final boolean CONTINUE_ON_ERROR = false;
    private static final boolean IGNORE_FAILED_DROPS = false;
    private static final String COMMENT_PREFIX = "#";
    private static final String STATEMENT_SEPARATOR = ";";
    private static final String BLOCK_COMMEND_DELIMITER_START = "/*";
    private static final String BLOCK_COMMEND_DELIMITER_END = "*/";

    private final DatabaseUpgradeConfiguration databaseUpgradeConfiguration;
    private final ApplicationConfigService applicationConfigService;
    private final SqlSessionFactory sqlSessionFactory;
    private ApplicationContext applicationContext;
    private final DatabaseMetaDataMapper databaseMetaDataMapper;

    public MysqlDatabaseUpgrader(DatabaseUpgradeConfiguration databaseUpgradeConfiguration,
                                 ApplicationConfigService applicationConfigService,
                                 SqlSessionFactory sqlSessionFactory,
                                 DatabaseMetaDataMapper databaseMetaDataMapper) {
        this.databaseUpgradeConfiguration = databaseUpgradeConfiguration;
        this.applicationConfigService = applicationConfigService;
        this.sqlSessionFactory = sqlSessionFactory;
        this.databaseMetaDataMapper = databaseMetaDataMapper;
    }

    @Override
    public void checkForUpgrade() {
        final int targetDbVersion = databaseUpgradeConfiguration.getTargetDbVersion();
        log.debug("Application startup success, check for database Update with targetVersion[{}]", targetDbVersion);
        doDatabaseUpgrade(targetDbVersion);
    }

    @Override
    public void checkForInit() {
        log.debug("Check for database init!");
        final List<String> tables = databaseMetaDataMapper.showTables();
        log.debug("Database INTI: tables=[{}]", tables);
        final long matchedTables = tables.stream().filter(TABLE_NAME_APPLICATION_CONFIG::equalsIgnoreCase).count();
        if (matchedTables <= 0) {
            doDatabaseInit();
        } else {
            log.info("Skip Database init!");
        }
    }

    private void doDatabaseInit() {
        resetDatabase();
    }

    /**
     * 执行数据库更新
     *
     * @param targetVersion database targetVersion
     */
    private void doDatabaseUpgrade(int targetVersion) {
        final ApplicationConfig versionConfig = applicationConfigService.getConfig(ApplicationConfig.DB_VERSION);
        final String currentVersion = versionConfig.getValue();
        try {
            if (VersionUtils.compareDatabaseVersion(String.valueOf(targetVersion), currentVersion) > 0) {
                final int from = NumberUtils.parseInt(currentVersion, 1);
                final List<EncodedResource> resources = lookupSqlScripts(from, targetVersion);
                runUpgradeScript(resources);
            } else {
                log.info("Application database targetVersion=[{}], currentVersion=[{}], no upgrade required!",
                        targetVersion, currentVersion);
            }
        } catch (Exception e) {
            log.error("Error On Upgrade Database! current=[{}], target=[{}]", currentVersion, targetVersion, e);
        }
    }

    /**
     * 查找数据库更新脚本
     *
     * @param from   开始版本
     * @param target 目标版本
     * @return Script File List
     */
    private List<EncodedResource> lookupSqlScripts(int from, int target) {
        final String updateScriptPath = databaseUpgradeConfiguration.getScriptPath();
        final List<EncodedResource> resources = new ArrayList<>();
        for (int version = from + 1; version <= target; version++) {
            final String scriptResourceName = String.format(DATABASE_UPGRADE_SCRIPT_FILE_NAME_TEMPLATE, updateScriptPath, version);
            final Resource resource = applicationContext.getResource(scriptResourceName);
            if (resource == null) {
                throw new InternalServerErrorException(String.format("Can not find resource [%s], target=[%s], current=[%s]", scriptResourceName, target, from));
            }
            log.info("Find database upgrade resource: [{}]", scriptResourceName);
            resources.add(new EncodedResource(resource));
        }
        return resources;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void runUpgradeScript(List<EncodedResource> resources) {
        for (EncodedResource resource : resources) {
            runScript(resource);
        }
    }

    @Override
    public void resetDatabase() {
        final String dbResetScriptName = databaseUpgradeConfiguration.getDbInitSqlFile();
        final String dbScriptPath = databaseUpgradeConfiguration.getScriptPath();
        final String resetScriptResourceName = String.format("%s/%s", dbScriptPath, dbResetScriptName);
        final Resource resource = applicationContext.getResource(resetScriptResourceName);
        if (resource == null) {
            throw new InternalServerErrorException("Can not find database init script: " + dbResetScriptName);
        }
        log.info("Reset database with script: [{}]", resetScriptResourceName);
        final EncodedResource sqlResource = new EncodedResource(resource);
        runScript(sqlResource);
    }

    private void runScript(EncodedResource resource) {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final Connection connection = session.getConnection();
            ScriptUtils.executeSqlScript(connection, resource,
                    CONTINUE_ON_ERROR, IGNORE_FAILED_DROPS, COMMENT_PREFIX, STATEMENT_SEPARATOR,
                    BLOCK_COMMEND_DELIMITER_START, BLOCK_COMMEND_DELIMITER_END);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
