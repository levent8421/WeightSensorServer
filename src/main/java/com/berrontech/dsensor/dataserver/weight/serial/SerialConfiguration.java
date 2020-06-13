package com.berrontech.dsensor.dataserver.weight.serial;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 18:44
 * Class Name: SerialConfiguration
 * Author: Levent8421
 * Description:
 * 串口配置
 *
 * @author Levent8421
 */
@Data
@ConfigurationProperties(prefix = "serial")
@Component
public class SerialConfiguration {
    private int baudRate;
}
