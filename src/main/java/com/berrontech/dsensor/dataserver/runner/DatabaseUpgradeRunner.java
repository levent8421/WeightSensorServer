package com.berrontech.dsensor.dataserver.runner;

import com.alibaba.fastjson.JSON;
import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.common.io.IOUtils;
import com.berrontech.dsensor.dataserver.common.util.ParamChecker;
import com.berrontech.dsensor.dataserver.common.util.VersionUtils;
import com.berrontech.dsensor.dataserver.common.vo.DatabaseVersionConfig;
import com.berrontech.dsensor.dataserver.conf.DatabaseUpgradeConfiguration;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Create By Levent8421
 * Create Time: 2020/7/4 18:23
 * Class Name: DatabaseUpgradeRunner
 * Author: Levent8421
 * Description:
 * 数据库升级任务
 *
 * @author Levent8421
 */
@Component
@Slf4j
@Order(1)
public class DatabaseUpgradeRunner implements ApplicationRunner {
    private final DatabaseUpgradeConfiguration databaseUpgradeConfiguration;
    private final ApplicationConfigService applicationConfigService;

    public DatabaseUpgradeRunner(DatabaseUpgradeConfiguration databaseUpgradeConfiguration,
                                 ApplicationConfigService applicationConfigService) {
        this.databaseUpgradeConfiguration = databaseUpgradeConfiguration;
        this.applicationConfigService = applicationConfigService;
    }

    @Override
    public void run(ApplicationArguments args) {
        final String dbVersionConfigFile = databaseUpgradeConfiguration.getDbVersionFilePath();
        log.debug("Application Database Upgrade with config file [{}]!", dbVersionConfigFile);
        checkForUpgrade(dbVersionConfigFile);
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
            if (VersionUtils.compare(targetVersion, currentVersion) > 0) {
                // Require A Database Upgrade
                final String sqlScriptPath = config.getSqlScriptPath();
                log.info("Upgrade database from [{}] to [{}] with script [{}]", currentVersion, targetVersion, sqlScriptPath);
                ParamChecker.notEmpty(sqlScriptPath, InternalServerErrorException.class, "SqlScriptPath Not config!");
                runSqlScript(sqlScriptPath);
            }
        } catch (Exception e) {
            log.error("Error On Upgrade Database! current=[{}], target=[{}]", currentVersion, targetVersion, e);
        }
    }

    /**
     * 执行数据库更新脚本
     *
     * @param sqlScriptPath sql script path
     */
    private void runSqlScript(String sqlScriptPath) {
        log.info("Running Database upgrade script[{}] ......", sqlScriptPath);
        log.info("Run Database Upgrade script done!");
    }
}
