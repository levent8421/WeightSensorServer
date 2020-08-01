package com.berrontech.dsensor.dataserver.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/7/27 19:41
 * Class Name: LogConfiguration
 * Author: Levent8421
 * Description:
 * 日志相关配置
 *
 * @author Levent8421
 */
@Data
@Component
@ConfigurationProperties("log-conf")
public class LogConfiguration {
    /**
     * 日志文件存放位置
     */
    private String logFileDir = "/mnt/hd0/scada_wsa/log";
}
