package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;

import java.util.Collection;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 12:15
 * Class Name: SlotService
 * Author: Levent8421
 * Description:
 * 货位相关业务行为定义
 *
 * @author Levent8421
 */
public interface SlotService extends AbstractService<Slot> {
    /**
     * 通过逻辑货道号更新SKU信息
     *
     * @param slot 包含SKU信息和SlotNo的Slot对象
     */
    void updateSkuInfoBySlotNo(Slot slot);

    /**
     * 更新逻辑货道号
     *
     * @param id     id
     * @param slotNo slotNO
     */
    void updateSlotNo(Integer id, String slotNo);

    /**
     * 通过SKU号或SKU名称查找数据
     *
     * @param skuNo   SKU号
     * @param skuName SKU名称
     * @return 货道列表
     */
    List<Slot> searchBySku(String skuNo, String skuName);

    /**
     * 通过扫描到的传感器创建或更新货道数据
     *
     * @param sensors             传感器集合
     * @param weightSensorService 重力传感器相关业务组件
     * @return 货道列表
     */
    List<Slot> createOrUpdateSlotsBySensor(Collection<WeightSensor> sensors, WeightSensorService weightSensorService);

    /**
     * 更新货道状态
     *
     * @param id    id
     * @param state state
     */
    void updateState(Integer id, int state);

    /**
     * 设置货道是否有电子标签
     *
     * @param id        id
     * @param hasElabel 电子标签
     */
    void setElabelState(Integer id, Boolean hasElabel);

    /**
     * Find Slots By SkuNo
     *
     * @param skuNo skuNo
     * @return slot list
     */
    List<Slot> findBySku(String skuNo);

    /**
     * Find slot by address(original address)
     *
     * @param address address
     * @return slot
     */
    Slot findByAddress(int address);

    /**
     * Delete Slot By Address List
     *
     * @param addressList address List
     */
    void deleteByAddressList(List<Integer> addressList);

    /**
     * find all slots with inner sensor
     *
     * @return slots
     */
    List<Slot> allWithSensors();

    /**
     * Find slot by ids
     *
     * @param slotIds ids
     * @return Slots
     */
    List<Slot> findByIds(List<Integer> slotIds);

    /**
     * 合并糊货道
     *
     * @param slots               货道列表
     * @param weightSensorService 传感器业务组件
     * @return 合并传感器数量
     */
    int mergeSlots(List<Slot> slots, WeightSensorService weightSensorService);

    /**
     * Find slot by addr list
     *
     * @param addrList addrList
     * @return slots
     */
    List<Slot> findByAddressList(List<Integer> addrList);
}
