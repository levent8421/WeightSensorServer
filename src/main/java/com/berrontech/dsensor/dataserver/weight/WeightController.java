package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;

import java.io.IOException;
import java.util.Collection;

/**
 * Create By Levent8421
 * Create Time: 2020/6/21 16:43
 * Class Name: WeightController
 * Author: Levent8421
 * Description:
 * 称重控制器
 *
 * @author Levent8421
 */
public interface WeightController {
    /**
     * 开始扫描所有连接下的传感器设备
     *
     * @param connections 要扫描的连接
     * @throws IOException 开始扫描失败时抛出
     */
    void startScan(Collection<DeviceConnection> connections) throws IOException;

    /**
     * 扫描指定连接下指定数量的传感器
     *
     * @param connection     连接
     * @param countOfSensors 传感器数量
     * @throws IOException 开启扫描失败时抛出
     */
    void startScan(DeviceConnection connection, int countOfSensors) throws IOException;

    /**
     * 更新货道SKU
     *
     * @param slotNo 货道号
     * @param sku    SKU信息
     */
    void setSku(String slotNo, MemorySku sku);

    /**
     * 元数据改变通知
     */
    void onMetaDataChanged();

    /**
     * 停止所有读取操作
     */
    void shutdown();

    /**
     * 设置逻辑货道号
     *
     * @param slotId 货道ID
     * @param slotNo 货道号
     */
    void updateSlotNo(Integer slotId, String slotNo);

    /**
     * 货道状态被改变时调用
     *
     * @param slotNo 货道号
     * @param state  状态 参考 AbstractDevice485.STATE_***
     */
    void onSlotStateChanged(String slotNo, int state);

    /**
     * 全部清零
     */
    void doZeroAll();

    /**
     * 清零指定货道
     *
     * @param slotNo 货道号
     */
    void doZero(String slotNo);
}
