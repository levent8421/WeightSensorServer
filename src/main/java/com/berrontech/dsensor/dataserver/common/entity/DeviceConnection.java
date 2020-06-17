package com.berrontech.dsensor.dataserver.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Create By Levent8421
 * Create Time: 2020/6/15 13:09
 * Class Name: DeviceConnection
 * Author: Levent8421
 * Description:
 * 设备连接实体类
 *
 * @author Levent8421
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_device_connection")
public class DeviceConnection extends AbstractEntity {
    /**
     * 串口连接
     */
    public static final int TYPE_SERIAL = 0x01;
    /**
     * 网络连接
     */
    public static final int TYPE_NET = 0x02;
    /**
     * 连接类型
     */
    @Column(name = "type", length = 2, nullable = false)
    private Integer type;
    /**
     * 连接目标
     */
    @Column(name = "target", nullable = false)
    private String target;
}

