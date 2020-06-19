package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public SensorMetaDataService(WeightDataHolder weightDataHolder,
                                 DeviceConnectionService deviceConnectionService,
                                 WeightSensorService weightSensorService,
                                 SlotService slotService) {
        this.weightDataHolder = weightDataHolder;
        this.deviceConnectionService = deviceConnectionService;
        this.weightSensorService = weightSensorService;
        this.slotService = slotService;
    }

    public void refreshSlotTable() {
        final List<DeviceConnection> connections = deviceConnectionService.all();
        weightDataHolder.setConnections(connections);

        final List<WeightSensor> weightSensors = weightSensorService.all();
        weightDataHolder.setWeightSensors(weightSensors);

        final List<Slot> slots = slotService.all();
        weightDataHolder.setSlots(slots);
        this.buildMemorySlotTable();
    }

    private void buildMemorySlotTable() {
//        weightDataHolder.getSlots().stream()
    }
}
