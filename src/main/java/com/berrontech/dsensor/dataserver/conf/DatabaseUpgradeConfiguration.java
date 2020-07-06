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
     * 数据库版本配置文件位置
     */
    private String dbVersionFilePath = "/sdcard/scada_wsa/db/db_version.json";
}
