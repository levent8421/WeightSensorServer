package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Create By Levent8421
 * Create Time: 2020/9/24 14:03
 * Class Name: TempHumidityLog
 * Author: Levent8421
 * Description:
 * 温湿度日志
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_temp_humidity_log")
public class TempHumidityLog extends AbstractEntity {
    /**
     * 传感器ID
     */
    @Column(name = "sensor_id", length = 10, nullable = false)
    private Integer sensorId;
    /**
     * 湿度
     */
    @Column(name = "humidity", length = 6, nullable = false)
    private Double humidity;
    /**
     * 湿度状态
     */
    @Column(name = "humidity_state", length = 2, nullable = false)
    private Integer humidityState;
    /**
     * 最大湿度
     */
    @Column(name = "max_humidity", length = 6, nullable = false)
    private Double maxHumidity;
    /**
     * 最小湿度
     */
    @Column(name = "min_humidity", length = 6, nullable = false)
    private Double minHumidity;
    /**
     * 温度
     */
    @Column(name = "temperature", length = 6, nullable = false)
    private Double temperature;
    /**
     * 温度状态
     */
    @Column(name = "temperature_state", length = 2, nullable = false)
    private Integer temperatureState;
    /**
     * 最大温度
     */
    @Column(name = "max_temperature", length = 6, nullable = false)
    private Double maxTemperature;
    /**
     * 最小温度
     */
    @Column(name = "min_temperature", length = 6, nullable = false)
    private Double minTemperature;
}
