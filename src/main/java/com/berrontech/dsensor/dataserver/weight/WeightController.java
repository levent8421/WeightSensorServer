package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;

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
     */
    void startScan();

    /**
     * 更新货道SKU
     *
     * @param slotNo 货道号
     * @param sku    SKU信息
     */
    void setSku(String slotNo, MemorySku sku);

    /**
     * 添加连接
     *
     * @param connection connection
     */
    void addConnection(DeviceConnection connection);

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
}
