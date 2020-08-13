package com.berrontech.dsensor.dataserver.runner;

import com.berrontech.dsensor.dataserver.upgrade.DatabaseUpgrader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    private final DatabaseUpgrader databaseUpgrader;

    public DatabaseUpgradeRunner(DatabaseUpgrader databaseUpgrader) {
        this.databaseUpgrader = databaseUpgrader;
    }

    /**
     * 运行数据库初始化
     * 运行数据库自动更新
     *
     * @param args command args
     */
    @Override
    public void run(ApplicationArguments args) {
        databaseUpgrader.checkForInit();
        databaseUpgrader.checkForUpgrade();
    }
}
