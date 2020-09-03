package com.berrontech.dsensor.dataserver.weight.dto;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/9/2 10:05
 * Class Name: SystemError
 * Author: Levent8421
 * Description:
 * 描述一个系统错误
 *
 * @author Levent8421
 */
@Data
public class SystemError {
    /**
     * 类型： 传感器错误
     */
    public static final int TYPE_SENSOR_ERROR = 0x01;
    /**
     * 类型： 电子标签错误
     */
    public static final int TYPE_ELABEL_ERROR = 0x02;
    /**
     * 错误类型
     */
    private Integer type;
    /**
     * 异常传感器地址
     */
    private Integer sensorAddress;
    /**
     * 异常传感器状态
     */
    private Integer sensorState;
    /**
     * 异常货道号
     */
    private String slotNo;
    /**
     * 异常货道状态
     */
    private Integer slotState;
    /**
     * 错误信息
     */
    private String message;
}
