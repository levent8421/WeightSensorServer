package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;

import java.util.Collection;
import java.util.List;

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
}
