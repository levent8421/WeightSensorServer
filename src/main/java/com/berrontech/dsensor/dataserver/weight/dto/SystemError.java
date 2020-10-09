package com.berrontech.dsensor.dataserver.weight.dto;

import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
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

    public static SystemError of(MemorySlot slot, MemoryWeightSensor sensor, int type, String message) {
        final SystemError error = new SystemError();
        error.setType(type);
        error.setSensorState(sensor.getState());
        error.setSensorAddress(sensor.getAddress485());
        error.setSlotState(slot.getState());
        error.setSlotNo(slot.getSlotNo());
        error.setMessage(message);
        return error;
    }

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
