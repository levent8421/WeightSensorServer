package com.berrontech.dsensor.dataserver.weight.holder;

import lombok.Data;

import java.io.Serializable;

/**
 * Create By Levent8421
 * Create Time: 2020/9/16 13:55
 * Class Name: MemoryTemperatureHumidityData
 * Author: Levent8421
 * Description:
 * 内存保持： 温湿度数据
 *
 * @author Levent8421
 */
@Data
public class MemoryTemperatureHumidityData implements Serializable {
    /**
     * 温度
     */
    private Double temperature;
    /**
     * 湿度
     */
    private Double humidity;
    /**
     * 温度状态
     */
    private Integer temperatureState;
    /**
     * 湿度状态
     */
    private Integer humidityState;
}
