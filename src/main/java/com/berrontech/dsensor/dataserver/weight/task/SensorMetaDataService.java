package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
        final Map<String, MemorySlot> slotTable = slotMap
                .values()
                .stream()
                .collect(Collectors.toMap(MemorySlot::getSlotNo, v -> v));
        weightDataHolder.setSlotTable(slotTable);
    }
}
