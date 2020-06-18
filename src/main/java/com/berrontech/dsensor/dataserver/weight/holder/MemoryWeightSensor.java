package com.berrontech.dsensor.dataserver.weight.holder;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:01
 * Class Name: MemoryWeightSensor
 * Author: Levent8421
 * Description:
 * 存在于内存中的Weight Sensor类
 *
 * @author Levent8421
 */
@Data
public class MemoryWeightSensor {
    /**
     * 状态 在线 正常
     */
    public static final int STATE_ONLINE = 0x01;
    /**
     * 状态 离线
     */
    public static final int STATE_OFFLINE = 0x02;
    /**
     * 状态 禁用
     */
    public static final int STATE_DISABLE = 0x03;
    /**
     * 状态 超载
     */
    public static final int STATE_OVERLOAD = 0x04;
    /**
     * 状态 欠载
     */
    public static final int STATE_UNDERLOAD = 0x05;

    private int address485;
    private DeviceConnection connection;
    private String deviceSn;
    private int state;

}
