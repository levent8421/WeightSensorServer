package com.berrontech.dsensor.dataserver.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/7/3 16:47
 * Class Name: SerialConfiguration
 * Author: Levent8421
 * Description:
 * 窗口配置类
 *
 * @author Levent8421
 */
@Data
@Component
@ConfigurationProperties(prefix = "serial")
public class SerialConfiguration {
    /**
     * 本地库位置
     */
    private String libName = "lib_serialport";
}
