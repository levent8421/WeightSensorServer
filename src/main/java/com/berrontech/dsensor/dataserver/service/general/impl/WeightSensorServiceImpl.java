package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.repository.mapper.WeightSensorMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
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
@Slf4j
public class WeightSensorServiceImpl extends AbstractServiceImpl<WeightSensor> implements WeightSensorService {
    private final WeightSensorMapper weightSensorMapper;

    public WeightSensorServiceImpl(WeightSensorMapper weightSensorMapper) {
        super(weightSensorMapper);
        this.weightSensorMapper = weightSensorMapper;
    }

    @Override
    public void deleteByConnection(Integer connectionId) {
        final WeightSensor query = new WeightSensor();
        query.setConnectionId(connectionId);
        weightSensorMapper.delete(query);
    }

    @Override
    public List<WeightSensor> findByConnection(Integer connectionId) {
        final WeightSensor query = new WeightSensor();
        query.setConnectionId(connectionId);
        return weightSensorMapper.select(query);
    }

    @Override
    public List<WeightSensor> createOrUpdateSensor(Collection<MemoryWeightSensor> sensors) {
        final List<WeightSensor> existsSensors = all();
        final Map<String, WeightSensor> sensorTable = existsSensors.stream()
                .collect(Collectors.toMap(WeightSensor::getDeviceSn, v -> v));
        final Map<Integer, WeightSensor> addressSensorTable = existsSensors.stream()
                .collect(Collectors.toMap(WeightSensor::getAddress, v -> v));
        final Map<String, MemoryWeightSensor> scannedSensorTable = tryBuildDeviceSnSensorTable(sensors);

        final List<WeightSensor> updatedSensors = new ArrayList<>();
        final List<WeightSensor> sensorsToSave = new ArrayList<>();
        for (MemoryWeightSensor sensor : scannedSensorTable.values()) {
            if (sensorTable.containsKey(sensor.getDeviceSn())) {
                // 扫描到的传感器已经在数据库中
                final WeightSensor existsSensor = sensorTable.get(sensor.getDeviceSn());
                final WeightSensor res = updateSensorInfo(existsSensor, sensor);
                updatedSensors.add(res);
            } else if (addressSensorTable.containsKey(sensor.getAddress485())) {
                //新扫描的物理地址已经存在
                final WeightSensor existsSensor = addressSensorTable.get(sensor.getAddress485());
                log.warn("Scan Done: Find a Sensor [{},{}] And Exists Sensor[{},{}] with same address, IGNORE!!!",
                        sensor.getDeviceSn(), sensor.getAddress485(),
                        existsSensor.getDeviceSn(), existsSensor.getAddress());
            } else {
                // 新的传感器被扫描到
                final WeightSensor res = createSensor(sensor);
                sensorTable.put(res.getDeviceSn(), res);
                sensorsToSave.add(res);
                updatedSensors.add(res);
            }
        }
        save(sensorsToSave);
        return updatedSensors;
    }

    private Map<String, MemoryWeightSensor> tryBuildDeviceSnSensorTable(Collection<MemoryWeightSensor> sensors) {
        final Map<String, MemoryWeightSensor> sensorMap = new HashMap<>(128);
        for (MemoryWeightSensor sensor : sensors) {
            final String sn = sensor.getDeviceSn();
            log.info("Scanned weightSensor [{}/{}]", sn, sensor.getAddress485());
            if (sensorMap.containsKey(sn)) {
                log.error("Scanned duplicate WeightSensor[{}]", sn);
            }
            sensorMap.put(sn, sensor);
        }
        return sensorMap;
    }

    private WeightSensor updateSensorInfo(WeightSensor base, MemoryWeightSensor update) {
        base.setDeviceSn(update.getDeviceSn());
        base.setAddress(update.getAddress485());
        base.setConnectionId(update.getConnectionId());
        base.setState(update.getState());
        base.setHasElabel(update.getHasElable());
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
        final WeightSensor weightSensor = new WeightSensor();
        weightSensor.setState(sensor.getState());
        weightSensor.setConnectionId(sensor.getConnectionId());
        weightSensor.setAddress(sensor.getAddress485());
        weightSensor.setDeviceSn(sensor.getDeviceSn());
        weightSensor.setZeroReference(WeightSensor.DEFAULT_ZERO_REFERENCE);
        weightSensor.setHasElabel(sensor.getHasElable());
        return weightSensor;
    }

    @Override
    public List<WeightSensor> findBySlot(Integer slotId) {
        final WeightSensor query = new WeightSensor();
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
        log.debug("Set sensor[{}] zeroOffset to [{}]", id, zeroReference);
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

    @Override
    public void setSensorsSlotTo(Set<Integer> sensorIds, Integer slotId) {
        weightSensorMapper.updateSlotIdBySlotId(slotId, -1);
        if (sensorIds.size() <= 0) {
            return;
        }
        final int rows = weightSensorMapper.updateSlotIdByIds(sensorIds, slotId);
        if (rows != sensorIds.size()) {
            throw new InternalServerErrorException("Update rows=[" + rows + "], accept rows=" + sensorIds.size());
        }
    }

    @Override
    public WeightSensor findPrimarySensor(int slotId) {
        return weightSensorMapper.selectPrimarySensorBySlotId(slotId);
    }

    @Override
    public List<WeightSensor> dumpAll() {
        return weightSensorMapper.dumpAll();
    }

    @Override
    public int resetSlotIdBySlotIds(List<Integer> slotIds) {
        return weightSensorMapper.resetSlotIdBySlotIds(slotIds);
    }

    @Override
    public void updateSn(Integer id, String sensorSn, String elabelSn) {
        final boolean shouldUpdateElabelSn = StringUtils.isNotBlank(elabelSn);
        final boolean shouldUpdateSensorSn = StringUtils.isNotBlank(sensorSn);
        if (!(shouldUpdateElabelSn || shouldUpdateSensorSn)) {
            return;
        }
        final WeightSensor sensor = require(id);
        if (shouldUpdateSensorSn) {
            sensor.setSensorSn(sensorSn);
            log.debug("Set sensorSn [{}] for id=[{}],address=[{}]", sensorSn, sensor.getId(), sensor.getAddress());
        }
        if (shouldUpdateElabelSn) {
            sensor.setElabelSn(elabelSn);
            log.debug("Set eLabelSn [{}] for id=[{}],address=[{}]", elabelSn, sensor.getId(), sensor.getAddress());
        }
        updateById(sensor);
    }

    private boolean sensorSnExists(String sn, Integer excludeId) {
        return weightSensorMapper.sensorSnExists(sn, excludeId) != null;
    }

    private boolean eLabelSnExists(String sn, Integer excludeId) {
        return weightSensorMapper.eLabelSnExists(sn, excludeId) != null;
    }

    @Override
    public boolean updateElabelSn(Integer id, String sn) {
        if (eLabelSnExists(sn, id)) {
            log.warn("Skip update ELabel SN [{}] for id [{}]", sn, id);
            return false;
        }
        final int rows = weightSensorMapper.updateElabelSnById(id, sn);
        log.info("Update ELabel SN to [{}] for id [{}], rows=[{}]", sn, id, rows);
        return rows == 1;
    }

    @Override
    public boolean updateSensorSn(Integer id, String sn) {
        if (sensorSnExists(sn, id)) {
            log.warn("Skip update Sensor SN [{}] for id [{}]!", sn, id);
            return false;
        }
        final int rows = weightSensorMapper.updateSensorSnById(id, sn);
        log.debug("Update sensor SN to [{}] for id [{}], rows=[{}]", sn, id, rows);
        return rows == 1;
    }

    @Override
    public int cleanAllBackupSn() {
        return weightSensorMapper.cleanAllBackupSn();
    }

    @Override
    public int getSensorCountBySlot(Integer slotId) {
        final Integer count = weightSensorMapper.selectCountBySlotId(slotId);
        return count == null ? 0 : count;
    }

    @Override
    public List<WeightSensor> findBySlotIdsWithSlot(List<Integer> slotIds) {
        return weightSensorMapper.selectBySlotIdsWithSlot(slotIds);
    }

    @Override
    public List<WeightSensor> findBySlotIds(List<Integer> slotIds) {
        return weightSensorMapper.selectBySlotIds(slotIds);
    }

    @Override
    public List<WeightSensor> findBySlotIdWithSlot(Integer slotId) {
        return weightSensorMapper.selectBySlotIdWithSlot(slotId);
    }
}
