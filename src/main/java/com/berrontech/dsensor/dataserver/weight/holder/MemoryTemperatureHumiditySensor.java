package com.berrontech.dsensor.dataserver.weight.holder;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import lombok.Data;

import java.io.Serializable;

/**
 * Create By Levent8421
 * Create Time: 2020/9/16 13:51
 * Class Name: MemoryTemperatureHumiditySensor
 * Author: Levent8421
 * Description:
 * 内存保持 ： 温湿度传感器
 *
 * @author Levent8421
 */
@Data
public class MemoryTemperatureHumiditySensor implements Serializable {
    /**
     * Row ID
     */
    private Integer id;
    /**
     * 传感器标号
     */
    private String no;
    /**
     * 485地址
     */
    private Integer address;
    /**
     * 传感器状态
     */
    private Integer state;
    /**
     * 连接ID
     */
    private Integer connectionId;
    /**
     * 连接对象
     */
    private DeviceConnection connection;
    /**
     * 是否存在电子标签
     */
    private Boolean hasElabel;
    /**
     * 传感器数据
     */
    private MemoryTemperatureHumidityData data;
}
