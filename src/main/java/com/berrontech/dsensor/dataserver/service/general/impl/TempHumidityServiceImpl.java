package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.TempHumidityLog;
import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.repository.mapper.TempHumidityLogMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.TempHumidityLogService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumidityData;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/24 14:28
 * Class Name: TempHumidityServiceImpl
 * Author: Levent8421
 * Description:
 * 温湿度日志相关业务行为实现
 *
 * @author Levent8421
 */
@Service
@Slf4j
public class TempHumidityServiceImpl extends AbstractServiceImpl<TempHumidityLog> implements TempHumidityLogService {
    private static final int LOG_RETENTION_HOURS = 48;
    private final TempHumidityLogMapper tempHumidityLogMapper;

    public TempHumidityServiceImpl(TempHumidityLogMapper tempHumidityLogMapper) {
        super(tempHumidityLogMapper);
        this.tempHumidityLogMapper = tempHumidityLogMapper;
    }

    @Override
    public List<TempHumidityLog> findBySensor(Integer sensorId) {
        final TempHumidityLog query = new TempHumidityLog();
        query.setSensorId(sensorId);
        query.setDeleted(false);
        return findByQuery(query);
    }

    @Override
    public void cleanLog() {
        final int rows = tempHumidityLogMapper.cleanLog(LOG_RETENTION_HOURS);
        log.info("Clean temperatureHumidityLog, rows=[{}]", rows);
    }

    @Override
    public void log(MemoryTemperatureHumiditySensor sensor, MemoryTemperatureHumidityData data) {
        final TempHumidityLog tempHumidityLog = new TempHumidityLog();
        tempHumidityLog.setSensorId(sensor.getId());
        tempHumidityLog.setMinHumidity(sensor.getMinHumidity());
        tempHumidityLog.setMaxHumidity(sensor.getMaxHumidity());
        tempHumidityLog.setMinTemperature(sensor.getMinTemperature());
        tempHumidityLog.setMaxTemperature(sensor.getMaxTemperature());

        final Double temperature = data.getTemperature();
        final Double humidity = data.getHumidity();
        tempHumidityLog.setTemperature(temperature == null ? 0 : temperature);
        tempHumidityLog.setHumidity(humidity == null ? 0 : humidity);
        final Integer humidityState = data.getHumidityState();
        final Integer temperatureState = data.getTemperatureState();
        tempHumidityLog.setHumidityState(humidityState == null ? 0 : humidityState);
        tempHumidityLog.setTemperatureState(temperatureState == null ? 0 : temperatureState);

        final TempHumidityLog saveRes = save(tempHumidityLog);
        log.debug("Save log [{}],temp=[{}], humidity=[{}]", saveRes.getId(), saveRes.getTemperature(), saveRes.getHumidity());
    }
}
