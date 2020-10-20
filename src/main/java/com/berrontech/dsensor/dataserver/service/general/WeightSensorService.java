package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 11:03
 * Class Name: WeightSensorService
 * Author: Levent8421
 * Description:
 * 中i传感器相关业务行为定义
 *
 * @author Levent8421
 */
public interface WeightSensorService extends AbstractService<WeightSensor> {
    /**
     * Delete Sensor By Connection Id
     *
     * @param connectionId connection Id
     */
    void deleteByConnection(Integer connectionId);

    /**
     * Find Sensor By Connection
     *
     * @param connectionId connection Id
     * @return Sensor List
     */
    List<WeightSensor> findByConnection(Integer connectionId);

    /**
     * 当该地址的传感器存在时更新传感器数据 否则创建新的传感器记录
     *
     * @param sensors 传感器列表
     * @return 保存结果
     */
    List<WeightSensor> createOrUpdateSensor(Collection<MemoryWeightSensor> sensors);

    /**
     * 更新传感器状态
     *
     * @param id    id
     * @param state state
     */
    void updateState(Integer id, int state);

    /**
     * Find Sensor By Slot Id
     *
     * @param slotId slotId
     * @return Sensors
     */
    List<WeightSensor> findBySlot(Integer slotId);

    /**
     * 设置零点参考值
     *
     * @param id            id
     * @param zeroReference 零点
     */
    void setZeroReference(Integer id, Double zeroReference);

    /**
     * 设置配置字符串
     *
     * @param id        id
     * @param configStr configuration string
     */
    void setConfigStr(Integer id, String configStr);

    /**
     * 批量设置某个货道传感器的电子标签状态
     *
     * @param slotId    slot id
     * @param hasElable has E-Label
     */
    void setElabelStateBySlotId(Integer slotId, Boolean hasElable);

    /**
     * List All Sensors with slot
     *
     * @return Sensors
     */
    List<WeightSensor> listWithSlot();

    /**
     * 设置传感器是否有电子标签
     *
     * @param id        id
     * @param hasElable 是否有电子标签
     */
    void updateElableState(Integer id, Boolean hasElable);

    /**
     * Find Weight Sensor By 485 Address
     *
     * @param address address
     * @return Sensor List
     */
    List<WeightSensor> findByAddress(int address);

    /**
     * 设置传感器货道为指定货道
     *
     * @param sensorIds sensor ids
     * @param slotId    slot id
     */
    void setSensorsSlotTo(Set<Integer> sensorIds, Integer slotId);

    /**
     * Find Primary Sensor By SlotId
     *
     * @param slotId slotId
     * @return WeightSensor
     */
    WeightSensor findPrimarySensor(int slotId);

    /**
     * Dump All WeightSensor Metadata from database
     *
     * @return sensor list with all inner objects
     */
    List<WeightSensor> dumpAll();

    /**
     * 重置传感器绑定的货道ID
     *
     * @param slotIds 货道ID
     * @return 重置的传感器数量
     */
    int resetSlotIdBySlotIds(List<Integer> slotIds);

    /**
     * 跟新设备序列号，非null时更新，为null时保持原数据
     *
     * @param id       设备ID
     * @param sensorSn 传感器SN
     * @param elabelSn 电子标签SN
     */
    void updateSn(Integer id, String sensorSn, String elabelSn);
}
