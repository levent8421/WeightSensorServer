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
    private String state;
}
