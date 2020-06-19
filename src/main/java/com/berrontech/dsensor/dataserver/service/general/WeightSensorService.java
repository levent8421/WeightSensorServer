package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;

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
}
