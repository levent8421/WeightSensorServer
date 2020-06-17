package com.berrontech.dsensor.dataserver.common.entity;

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
public abstract class AbstractDevice485 extends AbstractEntity {
    private Integer connectionId;
    private DeviceConnection connection;
    private Integer address;
    private String sn;
    private String state;
}
