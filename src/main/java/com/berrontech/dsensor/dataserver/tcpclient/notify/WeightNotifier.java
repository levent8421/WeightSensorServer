package com.berrontech.dsensor.dataserver.tcpclient.notify;

import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;

import java.util.Collection;

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
     * @param slot slot readonly
     */
    void countChange(MemorySlot slot);

    /**
     * 货道状态该表时调用
     *
     * @param slot  货道 readonly
     * @param state 状态
     */
    void deviceStateChanged(MemorySlot slot, int state);

    /**
     * 通知扫描到的所有重力传感器
     *
     * @param sensors 传感器 readonly
     */
    void notifySensorList(Collection<MemoryWeightSensor> sensors);
}
