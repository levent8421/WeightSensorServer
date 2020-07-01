package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.repository.mapper.WeightSensorMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        val existsSensors = all();
        final Map<String, WeightSensor> sensorTable = existsSensors.stream()
                .collect(Collectors.toMap(WeightSensor::getDeviceSn, v -> v));
        final List<WeightSensor> updatedSensors = new ArrayList<>();
        final List<WeightSensor> sensorsToSave = new ArrayList<>();
        for (MemoryWeightSensor sensor : sensors) {
            if (sensorTable.containsKey(sensor.getDeviceSn())) {
                // 扫描到的传感器已经在数据库中
                val existsSensor = sensorTable.get(sensor.getDeviceSn());
                val res = updateSensorInfo(existsSensor, sensor);
                updatedSensors.add(res);
            } else {
                // 新的传感器被扫描到
                val res = createSensor(sensor);
                sensorTable.put(res.getDeviceSn(), res);
                sensorsToSave.add(res);
                updatedSensors.add(res);
            }
        }
        save(sensorsToSave);
        return updatedSensors;
    }

    private WeightSensor updateSensorInfo(WeightSensor base, MemoryWeightSensor update) {
        base.setDeviceSn(update.getDeviceSn());
        base.setAddress(update.getAddress485());
        base.setConnectionId(update.getConnectionId());
        base.setState(update.getState());
        return updateById(base);
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
        weightSensor.setState(sensor.getState());
        weightSensor.setConnectionId(sensor.getConnectionId());
        weightSensor.setAddress(sensor.getAddress485());
        weightSensor.setDeviceSn(sensor.getDeviceSn());
        weightSensor.setZeroReference(WeightSensor.DEFAULT_ZERO_REFERENCE);
        weightSensor.setHasElabel(false);
        return weightSensor;
    }

    @Override
    public List<WeightSensor> findBySlot(Integer slotId) {
        val query = new WeightSensor();
        query.setSlotId(slotId);
        return findByQuery(query);
    }

    @Override
    public void setZeroReference(Integer id, Double zeroReference) {
        final int rows = weightSensorMapper.updateZeroReference(id, zeroReference);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update ZeroReference["
                    + zeroReference
                    + "] for id ["
                    + id
                    + "]!");
        }
    }

    @Override
    public void setConfigStr(Integer id, String configStr) {
        final int rows = weightSensorMapper.updateConfigStr(id, configStr);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update ConfigStr["
                    + configStr
                    + "] for id ["
                    + id
                    + "]!");
        }
    }

    @Override
    public void setElabelStateBySlotId(Integer slotId, Boolean hasElable) {
        final int rows = weightSensorMapper.updateHasElableBySlotId(slotId, hasElable);
        if (rows <= 0) {
            throw new BadRequestException("No Rows updated," + rows);
        }
    }

    @Override
    public List<WeightSensor> listWithSlot() {
        return weightSensorMapper.selectAllWithSlot();
    }

    @Override
    public void updateElableState(Integer id, Boolean hasElable) {
        final int rows = weightSensorMapper.updateHasElable(id, hasElable);
        if (rows != 1) {
            throw new InternalServerErrorException("Error On Update Elable State, rows=" + rows);
        }
    }

    @Override
    public List<WeightSensor> findByAddress(int address) {
        return weightSensorMapper.selectByAddress(address);
    }
}
