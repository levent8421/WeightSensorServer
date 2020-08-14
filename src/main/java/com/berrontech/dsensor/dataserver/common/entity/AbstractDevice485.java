package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;

/**
 * Create By Levent8421
 * Create Time: 2020/6/15 13:33
 * Class Name: AbstractDevice485
 * Author: Levent8421
 * Description:
 * 485 设备实体类
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractDevice485 extends AbstractEntity {
    public static final int STATE_ONLINE = 0x01;
    public static final String STATE_ONLINE_STR = "online";
    public static final int STATE_OFFLINE = 0x02;
    public static final String STATE_OFFLINE_STR = "offline";
    public static final int STATE_DISABLE = 0x03;
    public static final String STATE_DISABLE_STR = "disable";
    public static final int STATE_OVERLOAD = 0x04;
    public static final String STATE_OVERLOAD_STR = "overload";
    public static final int STATE_UNDER_LOAD = 0x05;
    public static final String STATE_UNDER_LOAD_STR = "underload";

    public static String getStateString(Integer state) {
        if (state == null) {
            return "null_state";
        }
        switch (state) {
            case STATE_ONLINE:
                return STATE_ONLINE_STR;
            case STATE_OFFLINE:
                return STATE_OFFLINE_STR;
            case STATE_DISABLE:
                return STATE_DISABLE_STR;
            case STATE_OVERLOAD:
                return STATE_OVERLOAD_STR;
            case STATE_UNDER_LOAD:
                return STATE_UNDER_LOAD_STR;
            default:
                return String.valueOf(state);
        }
    }

    public static int getState(String state) {
        switch (state) {
            case STATE_ONLINE_STR:
                return STATE_ONLINE;
            case STATE_OFFLINE_STR:
                return STATE_OFFLINE;
            case STATE_DISABLE_STR:
                return STATE_DISABLE;
            case STATE_OVERLOAD_STR:
                return STATE_OVERLOAD;
            case STATE_UNDER_LOAD_STR:
                return STATE_UNDER_LOAD;
            default:
                return -1;
        }
    }

    /**
     * 连接ID
     */
    @Column(name = "connection_id", length = 10, nullable = false)
    private Integer connectionId;
    /**
     * 连接对象
     */
    private DeviceConnection connection;
    /**
     * 485物理地址
     */
    @Column(name = "address", length = 10, nullable = false)
    private Integer address;
    /**
     * 设备SN
     */
    @Column(name = "device_sn", length = 50, nullable = false)
    private String deviceSn;
    /**
     * 设备状态
     */
    @Column(name = "state", length = 2, nullable = false)
    private Integer state;
}
