package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.weight.dto.DeviceDetails;
import com.berrontech.dsensor.dataserver.weight.dto.SensorPackageCounter;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareResource;
import com.berrontech.dsensor.dataserver.weight.firmware.UpgradeFirmwareListener;
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
     * @param connection 连接
     * @throws IOException 开启扫描失败时抛出
     */
    void startScan(DeviceConnection connection) throws IOException;

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

    /**
     * 查看是否正在扫描
     *
     * @return scanning?
     */
    boolean isScanning();

    /**
     * 货道高亮（闪烁）提示
     *
     * @param duration 高亮时间
     * @param slotNo   货道号
     */
    void highlight(String slotNo, long duration);

    /**
     * 批量提示
     *
     * @param duration 高亮时间
     * @param slots    slot number list
     */
    void highlight(Collection<String> slots, long duration);

    /**
     * 设置补偿状态
     *
     * @param enable 状态
     */
    void setAllCompensationStatus(boolean enable);

    /**
     * 查询指定连接的指定地址传感器的数据包计数器
     *
     * @param connectionId 连接ID
     * @param address      485地址
     * @return 包计数器
     */
    SensorPackageCounter getPackageCounter(Integer connectionId, int address);

    /**
     * 清空包计数器
     */
    void cleanPackageCounter();

    /**
     * 获取传感器详细信息
     *
     * @param connectionId 连接ID
     * @param address      物理地址
     * @return details
     */
    DeviceDetails getSensorDetails(Integer connectionId, Integer address);

    /**
     * 烧写固件
     *
     * @param connectionId 连接ID
     * @param address      物理地址
     * @param resource     固件资源
     * @param listener     监听器
     */
    void upgradeFirmware(Integer connectionId, Integer address, FirmwareResource resource, UpgradeFirmwareListener listener);
}
