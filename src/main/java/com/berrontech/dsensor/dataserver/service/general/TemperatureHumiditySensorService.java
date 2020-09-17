package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;

import java.util.Collection;

/**
 * Create By Levent8421
 * Create Time: 2020/9/17 17:31
 * Class Name: TemperatureHumiditySensorService
 * Author: Levent8421
 * Description:
 * 温湿度传感器相关业务行为定义
 *
 * @author Levent8421
 */
public interface TemperatureHumiditySensorService extends AbstractService<TemperatureHumiditySensor> {
    /**
     * 创建或更新温湿度传感器元数据
     *
     * @param sensors 扫描到的传感器
     */
    void createOrUpdateSensors(Collection<MemoryTemperatureHumiditySensor> sensors);
}
