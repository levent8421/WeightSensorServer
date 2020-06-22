package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.repository.mapper.WeightSensorMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 11:04
 * Class Name: WeightSensorServiceImpl
 * Author: Levent8421           `
 * Description:
 * 重力传感器相关业务行为实现
 *
 * @author Levent8421
 */
@Service
public class WeightSensorServiceImpl extends AbstractServiceImpl<WeightSensor> implements WeightSensorService {
    private final WeightSensorMapper weightSensorMapper;

    public WeightSensorServiceImpl(WeightSensorMapper weightSensorMapper) {
        super(weightSensorMapper);
        this.weightSensorMapper = weightSensorMapper;
    }

    @Override
    public void deleteByConnection(Integer connectionId) {
        val query = new WeightSensor();
        query.setConnectionId(connectionId);
        weightSensorMapper.delete(query);
    }

    @Override
    public List<WeightSensor> findByConnection(Integer connectionId) {
        val query = new WeightSensor();
        query.setConnectionId(connectionId);
        return weightSensorMapper.select(query);
    }

    @Override
    public List<WeightSensor> createOrUpdateSensor(Collection<MemoryWeightSensor> sensors) {
        final Map<Integer, Map<Integer, WeightSensor>> connectionSensorTable = new HashMap<>(16);
        val existsSensors = all();
        for (WeightSensor sensor : existsSensors) {
            val sensorTable = connectionSensorTable.computeIfAbsent(sensor.getConnectionId(), key -> new HashMap<>(16));
            sensorTable.put(sensor.getId(), sensor);
        }
        final Map<Integer, WeightSensor> returnSensors = new HashMap<>(16);
        final List<WeightSensor> saveSensors = new ArrayList<>();
        for (MemoryWeightSensor sensor : sensors) {
            val connection = sensor.getConnectionId();
            val address = sensor.getAddress485();
            final WeightSensor weightSensor;
            if (!connectionSensorTable.containsKey(connection) || !connectionSensorTable.get(connection).containsKey(address)) {
                weightSensor = createSensor(sensor);
                saveSensors.add(weightSensor);
            } else {
                val existsSensor = connectionSensorTable.get(connection).get(address);
                existsSensor.setConnectionId(connection);
                existsSensor.setDeviceSn(sensor.getDeviceSn());
                existsSensor.setState(WeightSensor.getStateString(sensor.getState()));
                weightSensor = updateById(existsSensor);
            }
            if (!returnSensors.containsKey(weightSensor.getId())) {
                returnSensors.put(weightSensor.getId(), weightSensor);
            }
        }
        save(saveSensors);
        return new ArrayList<>(returnSensors.values());
    }

    @Override
    public void updateState(Integer id, int state) {
        final int rows = weightSensorMapper.updateState(id, state);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update SensorState[" + state + "] ,id=" + id);
        }
    }

    private WeightSensor createSensor(MemoryWeightSensor sensor) {
        val weightSensor = new WeightSensor();
        weightSensor.setState(WeightSensor.getStateString(sensor.getState()));
        weightSensor.setConnectionId(sensor.getConnectionId());
        weightSensor.setAddress(sensor.getAddress485());
        weightSensor.setDeviceSn(sensor.getDeviceSn());
        return save(weightSensor);
    }
}
