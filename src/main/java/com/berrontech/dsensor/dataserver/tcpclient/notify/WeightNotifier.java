package com.berrontech.dsensor.dataserver.tcpclient.notify;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;

import java.util.Collection;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/22 11:24
 * Class Name: WeightNotifier
 * Author: Levent8421
 * Description:
 * 重量相关数据通知组件
 *
 * @author Levent8421
 */
public interface WeightNotifier {
    /**
     * 发送心跳通知
     */
    void heartbeat();

    /**
     * 物料数量改变后调用
     *
     * @param slots 发生数据改变的货道 readonly
     */
    void countChange(Collection<MemorySlot> slots);

    /**
     * 货道状态该表时调用
     *
     * @param slots 发生状态改变的货道列表 readonly
     * @param state 状态
     */
    void deviceStateChanged(Collection<MemorySlot> slots, int state);

    /**
     * 通知货道列表数据
     *
     * @param slots 货道列表 readonly
     */
    void notifySlotList(Collection<Slot> slots);

    /**
     * 通知传感器扫描结束
     *
     * @param sensors sensors
     */
    void notifyScanDone(Collection<MemoryWeightSensor> sensors);

    /**
     * 传感器状态发生改变时调用
     *
     * @param sensors 发生状态改变的传感器
     */
    void sensorStateChanged(Collection<MemoryWeightSensor> sensors);

    /**
     * 状态变化通知
     */
    void checkForStateChangedNotify();

    /**
     * 重量变化通知
     */
    void checkForWeightChangedNotify();

    /**
     * 通知温湿度传感器扫描结束
     *
     * @param sensors 传感器
     */
    void notifyTemperatureHumidityScanDone(Collection<MemoryTemperatureHumiditySensor> sensors);

    /**
     * 通知温湿度传感器状态变化
     *
     * @param sensors 传感器列表
     */
    void notifyTemperatureHumiditySensorStateChanged(Collection<MemoryTemperatureHumiditySensor> sensors);

    /**
     * 通知货道状态发生变化
     *
     * @param slots memory slot collection
     */
    void notifySlotStateChanged(Collection<MemorySlot> slots);

    /**
     * 货道合并通知
     *
     * @param slots 货道列表
     */
    void notifySlotMerged(List<Slot> slots);

    /**
     * 货道拆分通知
     *
     * @param slots 货道列表
     */
    void notifySlotUnmerged(List<Slot> slots);
}
