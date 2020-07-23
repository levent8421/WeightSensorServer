package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 10:58
 * Class Name: WeightSensorMapper
 * Author: Levent8421
 * Description:
 * 重力传感器相关数据库访问组件
 *
 * @author Levent8421
 */
@Repository
public interface WeightSensorMapper extends AbstractMapper<WeightSensor> {
    /**
     * 更新传感器状态
     *
     * @param id    id
     * @param state state
     * @return rows
     */
    int updateState(@Param("id") Integer id, @Param("state") int state);

    /**
     * 更新传感器零点
     *
     * @param id            sensor id
     * @param zeroReference zero
     * @return rows
     */
    int updateZeroReference(@Param("id") Integer id, @Param("zeroReference") Double zeroReference);

    /**
     * 设置配置字符串
     *
     * @param id        sensor id
     * @param configStr configuration string
     * @return rows
     */
    int updateConfigStr(@Param("id") Integer id, @Param("configStr") String configStr);

    /**
     * 批量更新某个货道传感器的电子标签状态
     *
     * @param slotId    slot ID
     * @param hasElable has ELable
     * @return rows
     */
    int updateHasElableBySlotId(@Param("slotId") Integer slotId, @Param("hasElable") Boolean hasElable);

    /**
     * Select All Sensors Fetch Slot
     *
     * @return Sensors
     */
    List<WeightSensor> selectAllWithSlot();

    /**
     * Update [HasELabel] by id
     *
     * @param id        id
     * @param hasElabel ELabel
     * @return rows
     */
    int updateHasElable(@Param("id") Integer id, @Param("hasElabel") Boolean hasElabel);

    /**
     * Select Sensors By 485 Address
     *
     * @param address address
     * @return Sensor List
     */
    List<WeightSensor> selectByAddress(@Param("address") int address);

    /**
     * Update slotId by ids
     *
     * @param ids    sensorIds
     * @param slotId slotsId
     * @return rows
     */
    int updateSlotIdByIds(@Param("ids") Set<Integer> ids, @Param("slotId") Integer slotId);

    /**
     * Update sensor slot id by slot id
     *
     * @param slotId    slot id
     * @param newSlotId target slot id
     * @return rows
     */
    int updateSlotIdBySlotId(@Param("slotId") Integer slotId, @Param("newSlotId") int newSlotId);

    /**
     * Find A Primary Sensor(A sensor with min address) by slot id
     *
     * @param slotId SlotId
     * @return WeightSensor
     */
    WeightSensor selectPrimarySensorBySlotId(@Param("slotId") int slotId);

    /**
     * Dump all sensor meta data from database
     *
     * @return sensor list with all inner objects
     */
    List<WeightSensor> dumpAll();
}
