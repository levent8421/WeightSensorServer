package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.AbstractDevice485;
import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.repository.mapper.TemperatureHumiditySensorMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.TemperatureHumiditySensorService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/9/17 17:32
 * Class Name: TemperatureHumiditySensorServiceImpl
 * Author: Levent8421
 * Description:
 * 温湿度传感器相关业务行为实现
 *
 * @author Levent8421
 */
@Slf4j
@Service
public class TemperatureHumiditySensorServiceImpl extends AbstractServiceImpl<TemperatureHumiditySensor> implements TemperatureHumiditySensorService {
    private final TemperatureHumiditySensorMapper temperatureHumiditySensorMapper;

    public TemperatureHumiditySensorServiceImpl(TemperatureHumiditySensorMapper temperatureHumiditySensorMapper) {
        super(temperatureHumiditySensorMapper);
        this.temperatureHumiditySensorMapper = temperatureHumiditySensorMapper;
    }

    @Override
    public void createOrUpdateSensors(Collection<MemoryTemperatureHumiditySensor> sensors) {
        final List<TemperatureHumiditySensor> resultList = new ArrayList<>();
        for (MemoryTemperatureHumiditySensor sensor : sensors) {
            TemperatureHumiditySensor sensorMetaData = findBySn(sensor.getSn());
            if (sensorMetaData == null) {
                sensorMetaData = createDefaultSensor(sensor);
            } else {
                sensorMetaData.setAddress(sensor.getAddress());
                sensorMetaData.setDeviceSn(sensor.getSn());
                sensorMetaData.setConnectionId(sensor.getConnectionId());
                sensorMetaData.setState(AbstractDevice485.STATE_ONLINE);
                updateById(sensorMetaData);
            }
            resultList.add(sensorMetaData);
        }
        log.debug("Create Or Update [{}] sensor(s)!", resultList.size());
    }

    private TemperatureHumiditySensor createDefaultSensor(MemoryTemperatureHumiditySensor sensor) {
        final TemperatureHumiditySensor sensorMetaData = new TemperatureHumiditySensor();
        sensorMetaData.setState(AbstractDevice485.STATE_ONLINE);
        sensorMetaData.setAddress(sensor.getAddress());
        sensorMetaData.setConnectionId(sensor.getConnectionId());
        sensorMetaData.setNo(String.format("#%d-%d", sensor.getConnectionId(), sensor.getAddress()));
        sensorMetaData.setDeviceSn(sensor.getSn());
        return save(sensorMetaData);
    }

    private TemperatureHumiditySensor findBySn(String sn) {
        final TemperatureHumiditySensor query = new TemperatureHumiditySensor();
        query.setDeviceSn(sn);
        return findOneByQuery(query);
    }
}
