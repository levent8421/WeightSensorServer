package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
}
