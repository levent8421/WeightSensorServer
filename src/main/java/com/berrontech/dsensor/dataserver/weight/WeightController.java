package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightDataRecord;
import com.berrontech.dsensor.dataserver.weight.dto.DeviceDetails;
import com.berrontech.dsensor.dataserver.weight.dto.DeviceState;
import com.berrontech.dsensor.dataserver.weight.dto.SensorPackageCounter;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareResource;
import com.berrontech.dsensor.dataserver.weight.firmware.UpgradeFirmwareListener;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.scan.SensorScanListener;

import java.io.IOException;
import java.math.BigDecimal;
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
     * 扫描单连接
     *
     * @param connection 连接
     * @param listener   监听器
     * @throws IOException IOE
     */
    void startScan(DeviceConnection connection, SensorScanListener listener) throws IOException;

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
     * 烧写传感器固件
     *
     * @param connectionId 连接ID
     * @param address      物理地址
     * @param resource     固件资源
     * @param listener     监听器
     */
    void upgradeFirmware(Integer connectionId, Integer address, FirmwareResource resource, UpgradeFirmwareListener listener);

    /**
     * 烧写电子标签固件
     *
     * @param connectionId 连接ID
     * @param address      物理地址
     * @param resource     固件资源
     * @param listener     监听器
     */
    void upgradeElabelFirmware(Integer connectionId, Integer address, FirmwareResource resource, UpgradeFirmwareListener listener);

    /**
     * 取消升级
     *
     * @param connectionId 连接ID
     * @param address      物理地址
     */
    void cancelUpgrade(Integer connectionId, Integer address);

    /**
     * 开始扫描温湿度传感器
     *
     * @param connection 连接
     * @throws IOException error
     */
    void startScanTemperatureHumiditySensors(DeviceConnection connection) throws IOException;

    /**
     * 获取设备状态
     *
     * @param connectionId 连接ID
     * @param address      设备地址
     * @return 设备状态对象
     */
    DeviceState getDeviceState(Integer connectionId, Integer address);

    /**
     * 对指定连接下指定SN的传感器编址
     *
     * @param connectionId 连接ID
     * @param sn           sn
     * @param address      地址
     * @return 是否成功
     */
    boolean setSensorAddressForSn(Integer connectionId, String sn, Integer address);

    /**
     * 对指定连接下指定SN的电子标签编址
     *
     * @param connectionId 连接ID
     * @param sn           sn
     * @param address      地址
     * @return 是否成功
     */
    boolean setElabelAddressForSn(Integer connectionId, String sn, Integer address);

    /**
     * 重新为电子标签分配SN
     *
     * @param connectionId 连接ID
     * @param address      地址
     * @return SN
     * @throws SnBuildException any error
     */
    String rebuildSnForElabel(Integer connectionId, Integer address) throws SnBuildException;

    /**
     * 重新为传感器分配SN
     *
     * @param connectionId 连接ID
     * @param address      地址
     * @return SN
     * @throws SnBuildException any Error
     */
    String rebuildSnForSensor(Integer connectionId, Integer address) throws SnBuildException;

    /**
     * 标定温度传感器
     *
     * @param connectionId       连接ID
     * @param address            物理地址
     * @param currentTemperature 当前温度
     * @throws CalibrationException 标定异常
     */
    void calibrateTemperatureSensor(Integer connectionId, Integer address, BigDecimal currentTemperature)
            throws CalibrationException;

    /**
     * 获取传感器记录信息
     *
     * @param connectionId connectionId
     * @param address      address
     * @return record
     */
    WeightDataRecord getSensorRecord(Integer connectionId, Integer address);

    /**
     * 零点标定
     *
     * @param connectionId 连接ID
     * @param address      传感器地址
     * @throws CalibrationException 标定错误
     */
    void calibrateWeightSensorZero(Integer connectionId, Integer address) throws CalibrationException;

    /**
     * SPAN标定
     *
     * @param connectionId 连接ID
     * @param address      传感器地址
     * @param span         砝码重量
     * @param unitCode     单位代码 @see WeightUnit
     * @throws CalibrationException 标定错误
     */
    void calibrateWeightSensorSpan(Integer connectionId, Integer address, BigDecimal span, int unitCode) throws CalibrationException;

    /**
     * 去皮
     *
     * @param slotNo   货道号
     * @param tare     皮重：
     *                 若传入皮重未null，则使用当前重量作为皮重
     *                 若当前无重量（设备离线或未准备就绪）则抛出异常
     * @param unitCode 单位代码 @see WeightUnit
     * @return 去皮后的皮重，不允许返回空值
     * @throws TareException 去皮错误
     */
    BigDecimal doTare(String slotNo, BigDecimal tare, int unitCode) throws TareException;
}
