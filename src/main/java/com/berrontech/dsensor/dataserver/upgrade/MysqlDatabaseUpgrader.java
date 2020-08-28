package com.berrontech.dsensor.dataserver.upgrade;

import com.alibaba.fastjson.JSON;
import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.io.IOUtils;
import com.berrontech.dsensor.dataserver.common.util.NumberUtils;
import com.berrontech.dsensor.dataserver.common.util.ParamChecker;
import com.berrontech.dsensor.dataserver.common.util.VersionUtils;
import com.berrontech.dsensor.dataserver.common.vo.DatabaseVersionConfig;
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

import java.io.File;
import java.io.IOException;
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
     * 数据库初始化脚本
     */
    private static final String DATABASE_INIT_SCRIPT_RESOURCE_NAME = "classpath:/db_script/db_init.sql";
    /**
     * 数据库更新脚本模板
     */
    private static final String DATABASE_UPGRADE_SCRIPT_FILE_NAME_TEMPLATE = "upgrade_scada_wsa_%s.sql";
    /**
     * 脚本文件资源名模板
     */
    private static final String SCRIPT_RESOURCE_NAME_TEMPLATE = "file:%s";
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
        final String dbVersionConfigFile = databaseUpgradeConfiguration.getDbVersionFilePath();
        log.debug("Application database upgrade with config file [{}]!", dbVersionConfigFile);
        checkForUpgrade(dbVersionConfigFile);
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
     * 检查更新
     *
     * @param configFilePath 数据库版本配置文件
     */
    private void checkForUpgrade(String configFilePath) {
        final File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            log.info("Can not found the dbVersion config file!");
            return;
        }
        if (!configFile.isFile()) {
            log.error("DbVersion Config file path [{}] is not a file!", configFilePath);
            return;
        }
        final String configJson;
        try {
            configJson = IOUtils.readAsString(configFile);
        } catch (IOException e) {
            log.error("Error on read DbVersion config file!", e);
            return;
        }
        final DatabaseVersionConfig databaseVersionConfig;
        try {
            databaseVersionConfig = JSON.parseObject(configJson, DatabaseVersionConfig.class);
            ParamChecker.notNull(databaseVersionConfig, InternalServerErrorException.class,
                    "Parse a null result DbVersion Config json!");
            ParamChecker.notEmpty(databaseVersionConfig.getTargetDbVersion(), InternalServerErrorException.class,
                    "No TargetDatabaseVersion in config file!");
        } catch (Exception e) {
            log.error("Error on parse BbVersion config json.", e);
            return;
        }
        doDatabaseUpgrade(databaseVersionConfig);
    }

    /**
     * 执行数据库更新
     *
     * @param config version config
     */
    private void doDatabaseUpgrade(DatabaseVersionConfig config) {
        final ApplicationConfig versionConfig = applicationConfigService.getConfig(ApplicationConfig.DB_VERSION);
        final String currentVersion = versionConfig.getValue();
        final String targetVersion = config.getTargetDbVersion();
        try {
            if (VersionUtils.compareDatabaseVersion(targetVersion, currentVersion) > 0) {
                final int from = NumberUtils.parseInt(currentVersion, 1);
                final int target = NumberUtils.parseInt(targetVersion, 1);
                final List<File> scriptList = lookupSqlScripts(from, target);
                runUpgradeScript(scriptList);
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
    private List<File> lookupSqlScripts(int from, int target) {
        final File path = new File(databaseUpgradeConfiguration.getScriptPath());
        if (!path.exists() || !path.isDirectory()) {
            throw new InternalServerErrorException("Database upgrade script path ["
                    + path.getAbsolutePath()
                    + "] could not be found!");
        }
        final List<File> files = new ArrayList<>();
        for (int version = from + 1; version <= target; version++) {
            final String scriptFileName = String.format(DATABASE_UPGRADE_SCRIPT_FILE_NAME_TEMPLATE, version);
            final File scriptFile = new File(path, scriptFileName);
            if (!scriptFile.exists() || !scriptFile.isFile()) {
                throw new InternalServerErrorException("Database upgrade script file["
                        + scriptFileName
                        + "] could not be found!");
            }
            files.add(scriptFile);
        }
        return files;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void runUpgradeScript(List<File> scriptFiles) {
        for (File scriptFile : scriptFiles) {
            runScript(scriptFile);
        }
    }

    @Override
    public void resetDatabase() {
        final Resource resource = applicationContext.getResource(DATABASE_INIT_SCRIPT_RESOURCE_NAME);
        final EncodedResource sqlResource = new EncodedResource(resource);
        log.info("Database init with script: [{}]", DATABASE_INIT_SCRIPT_RESOURCE_NAME);
        runScript(sqlResource);
    }

    /**
     * 执行数据库更新脚本文件
     *
     * @param scriptFile 脚本文件
     */
    private void runScript(File scriptFile) {
        final String filePath = scriptFile.getAbsolutePath();
        log.info("Running database upgrade script [{}]", filePath);
        final String resourceName = String.format(SCRIPT_RESOURCE_NAME_TEMPLATE, filePath);
        final EncodedResource resource = new EncodedResource(applicationContext.getResource(resourceName));
        runScript(resource);
        log.info("Run done database upgrade script [{}]", filePath);
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
