package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightData;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/19 10:34
 * Class Name: SensorMetaDataService
 * Author: Levent8421
 * Description:
 * 重力传感器元数据业务组件
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class SensorMetaDataService {
    private final WeightDataHolder weightDataHolder;
    private final DeviceConnectionService deviceConnectionService;
    private final WeightSensorService weightSensorService;
    private final SlotService slotService;
    private final WeightController weightController;

    public SensorMetaDataService(WeightDataHolder weightDataHolder,
                                 DeviceConnectionService deviceConnectionService,
                                 WeightSensorService weightSensorService,
                                 SlotService slotService,
                                 WeightController weightController) {
        this.weightDataHolder = weightDataHolder;
        this.deviceConnectionService = deviceConnectionService;
        this.weightSensorService = weightSensorService;
        this.slotService = slotService;
        this.weightController = weightController;
    }

    public void refreshSlotTable() {
        final List<DeviceConnection> connections = deviceConnectionService.all();
        weightDataHolder.setConnections(connections);

        final List<WeightSensor> weightSensors = weightSensorService.all();
        weightDataHolder.setWeightSensors(weightSensors);

        final List<Slot> slots = slotService.all();
        weightDataHolder.setSlots(slots);
        this.buildMemorySlotTable();
        weightController.onMetaDataChanged();
    }

    private void buildMemorySlotTable() {
        final Map<Integer, MemorySlot> slotMap = weightDataHolder
                .getSlots()
                .stream()
                .map(MemorySlot::of)
                .collect(Collectors.toMap(MemorySlot::getId, v -> v));
        final Map<Integer, DeviceConnection> connectionMap = weightDataHolder
                .getConnections()
                .stream()
                .collect(Collectors.toMap(DeviceConnection::getId, v -> v));
        weightDataHolder.getWeightSensors().stream().map(MemoryWeightSensor::of).forEach(sensor -> {
            sensor.setConnection(connectionMap.get(sensor.getConnectionId()));
            final MemorySlot slot = slotMap.get(sensor.getSlotId());
            if (slot == null) {
                return;
            }
            final Collection<MemoryWeightSensor> sensors = slot.getSensors() == null ? new ArrayList<>() : slot.getSensors();
            sensors.add(sensor);
            slot.setSensors(sensors);
        });
        final Map<String, MemorySlot> slotTable = new HashMap<>(16);
        slotMap.values()
                .stream()
                .peek(slot -> slot.setData(new MemoryWeightData()))
                .forEach(slot -> putSlotIntoSlotTable(slotTable, slot));
        weightDataHolder.setSlotTable(slotTable);
    }

    private void putSlotIntoSlotTable(Map<String, MemorySlot> slotTable, MemorySlot slot) {
        String slotNo = slot.getSlotNo();
        int num = 0;
        while (slotTable.containsKey(slotNo)) {
            log.warn("Duplicate slotNo [{}]", slotNo);
            slotNo = String.format("%s(%d)", slotNo, ++num);
        }
        slotTable.put(slotNo, slot);
    }

    /**
     * 更新slotTable 中的传感器状态
     *
     * @param sensors 传感器列表
     */
    public void updateSensorStateInSlotTable(Collection<MemoryWeightSensor> sensors) {
        final Map<String, MemorySlot> slotTable = weightDataHolder.getSlotTable();
        final Map<Integer, MemoryWeightSensor> sensorsTable = sensors.stream()
                .collect(Collectors.toMap(MemoryWeightSensor::getId, v -> v));
        for (MemorySlot slot : slotTable.values()) {
            final Collection<MemoryWeightSensor> slotSensors = slot.getSensors();
            if (slotSensors == null || slotSensors.isEmpty()) {
                continue;
            }
            for (MemoryWeightSensor sensor : slot.getSensors()) {
                if (sensorsTable.containsKey(sensor.getId())) {
                    final MemoryWeightSensor targetSensor = sensorsTable.get(sensor.getId());
                    sensor.setState(targetSensor.getState());
                    slot.setState(targetSensor.getState());
                }
            }
        }
    }
}
