package com.berrontech.dsensor.dataserver.service.general;

import com.berrontech.dsensor.dataserver.common.entity.TempHumidityLog;
import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.service.basic.AbstractService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumidityData;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/24 14:27
 * Class Name: TempHumidityLogService
 * Author: Levent8421
 * Description:
 * 温湿度日志相关业务组件（定义）
 *
 * @author Levent8421
 */
public interface TempHumidityLogService extends AbstractService<TempHumidityLog> {
    /**
     * 获取传感器的温湿度日志
     *
     * @param sensorId 传感器ID
     * @return logs
     */
    List<TempHumidityLog> findBySensor(Integer sensorId);

    /**
     * 清空日志
     */
    void cleanLog();

    /**
     * 记录日志
     *
     * @param sensor 传感器
     * @param data   数据
     */
    void log(MemoryTemperatureHumiditySensor sensor, MemoryTemperatureHumidityData data);
}
