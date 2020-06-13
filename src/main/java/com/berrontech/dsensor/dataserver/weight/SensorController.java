package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.io.PackageReadConnection;

import java.io.IOException;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 10:06
 * Class Name: SensorController
 * Author: Levent8421
 * Description:
 * Weight Sensor Controller
 *
 * @author Levent8421
 */
public interface SensorController extends PackageReadConnection {
    /**
     * 发送数据包
     *
     * @param packet package
     * @throws IOException e
     */
    void send(DataPacket packet) throws IOException;
}
