package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Create By Levent8421
 * Create Time: 2020/9/17 17:04
 * Class Name: TemperatureHumiditySensor
 * Author: Levent8421
 * Description:
 * 温湿度传感器元数据实体类
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_temperature_humidity_sensor")
public class TemperatureHumiditySensor extends AbstractDevice485 {
    /**
     * 标号
     */
    @Column(name = "no", length = 100, nullable = false)
    private String no;
    /**
     * 温度上限
     */
    @Column(name = "max_temperature", nullable = false)
    private Double maxTemperature;
    /**
     * 温度下限
     */
    @Column(name = "min_temperature", nullable = false)
    private Double minTemperature;
    /**
     * 湿度上限
     */
    @Column(name = "max_humidity", nullable = false)
    private Double maxHumidity;
    /**
     * 湿度下限
     */
    @Column(name = "min_humidity", nullable = false)
    private Double minHumidity;
}
