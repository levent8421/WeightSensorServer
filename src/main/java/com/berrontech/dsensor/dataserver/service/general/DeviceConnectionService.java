package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 18:33
 * Class Name: DeviceConnectionService
 * Author: Levent8421
 * Description:
 * 设备连接相关业务行为定义
 *
 * @author Levent8421
 */
public interface DeviceConnectionService extends AbstractService<DeviceConnection> {
    /**
     * 新增连接
     *
     * @param param param
     * @return Connection
     */
    DeviceConnection createConnection(DeviceConnection param);

    /**
     * 刷新全部串口连接的usb ID，并获取连接列表
     *
     * @return connections
     */
    List<DeviceConnection> refreshConnectionUsbIdAndGet();

    /**
     * 刷新设备USB ID
     *
     * @param connection connection
     * @return connection
     */
    DeviceConnection refreshUsbId(DeviceConnection connection);
}
