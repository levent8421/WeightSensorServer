package com.berrontech.dsensor.dataserver.weight.holder;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
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
    public static final int STATE_ONLINE = WeightSensor.STATE_ONLINE;
    /**
     * 状态 离线
     */
    public static final int STATE_OFFLINE = WeightSensor.STATE_OFFLINE;
    /**
     * 状态 禁用
     */
    public static final int STATE_DISABLE = WeightSensor.STATE_DISABLE;

    public static MemoryWeightSensor of(WeightSensor sensor) {
        final MemoryWeightSensor mws = new MemoryWeightSensor();
        mws.setId(sensor.getId());
        mws.setAddress485(sensor.getAddress());
        mws.setDeviceSn(sensor.getDeviceSn());
        mws.setState(STATE_OFFLINE);
        mws.setConnectionId(sensor.getConnectionId());
        mws.setSlotId(sensor.getSlotId());
        mws.setZeroReference(sensor.getZeroReference());
        mws.setConfigStr(sensor.getConfigStr());
        mws.setHasElable(sensor.getHasElabel());
        return mws;
    }

    private Integer id;
    private Integer slotId;
    private Double zeroReference;
    private String configStr;
    private int address485;
    private Integer connectionId;
    private DeviceConnection connection;
    private String deviceSn;
    private int state;
    private MemoryWeightData weightData;
    private Boolean hasElable;
}
