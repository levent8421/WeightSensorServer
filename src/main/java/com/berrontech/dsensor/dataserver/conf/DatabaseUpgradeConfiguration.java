package com.berrontech.dsensor.dataserver.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/7/4 18:18
 * Class Name: DatabaseUpgradeConfiguration
 * Author: Levent8421
 * Description:
 * 数据库更行配置
 *
 * @author Levent8421
 */
@ConfigurationProperties(prefix = "db-upgrade")
@Component
@Data
public class DatabaseUpgradeConfiguration {
    /**
     * 数据库更新脚本位置
     */
    private String scriptPath = "classpath:/db_script";
    /**
     * 数据库初始化脚本
     */
    private String dbInitSqlFile = "db_init.sql";
    /**
     * Target db version
     */
    private int targetDbVersion = 11;
}
