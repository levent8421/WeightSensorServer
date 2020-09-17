package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.*;
import com.berrontech.dsensor.dataserver.common.util.CollectionUtils;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.service.general.TemperatureHumiditySensorService;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.dto.SystemError;
import com.berrontech.dsensor.dataserver.weight.holder.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
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
public class SensorMetaDataService implements ThreadFactory {
    private final ExecutorService threadPool;
    private final WeightDataHolder weightDataHolder;
    private final DeviceConnectionService deviceConnectionService;
    private final WeightSensorService weightSensorService;
    private final SlotService slotService;
    private final TemperatureHumiditySensorService temperatureHumiditySensorService;
    private final WeightController weightController;
    private final BlockingQueue<Runnable> threadQueue;

    public SensorMetaDataService(WeightDataHolder weightDataHolder,
                                 DeviceConnectionService deviceConnectionService,
                                 WeightSensorService weightSensorService,
                                 SlotService slotService,
                                 WeightController weightController,
                                 TemperatureHumiditySensorService temperatureHumiditySensorService) {
        this.weightDataHolder = weightDataHolder;
        this.deviceConnectionService = deviceConnectionService;
        this.weightSensorService = weightSensorService;
        this.slotService = slotService;
        this.weightController = weightController;
        this.threadQueue = new LinkedBlockingDeque<>();
        this.threadPool = buildThreadPool();
        this.temperatureHumiditySensorService = temperatureHumiditySensorService;
    }

    private ExecutorService buildThreadPool() {
        return new ThreadPoolExecutor(1, 1,
                0, TimeUnit.MILLISECONDS, threadQueue, this);
    }

    public void refreshSlotTable() {
        if (!threadQueue.isEmpty()) {
            return;
        }
        threadPool.execute(this::doRefreshSlotTable);
    }

    private void doRefreshSlotTable() {
        final List<DeviceConnection> connections = deviceConnectionService.all();
        weightDataHolder.setConnections(connections);

        final List<WeightSensor> weightSensors = weightSensorService.all();
        weightDataHolder.setWeightSensors(weightSensors);

        final List<Slot> slots = slotService.all();
        weightDataHolder.setSlots(slots);

        final List<TemperatureHumiditySensor> temperatureHumiditySensors = temperatureHumiditySensorService.all();
        weightDataHolder.setTemperatureHumiditySensors(temperatureHumiditySensors);

        this.buildMemorySlotTable();
        this.buildTemperatureHumiditySensorTable();
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

    private void buildTemperatureHumiditySensorTable() {
        final Map<Integer, MemoryTemperatureHumiditySensor> temperatureHumiditySensorMap = weightDataHolder
                .getTemperatureHumiditySensors()
                .stream()
                .map(MemoryTemperatureHumiditySensor::of)
                .peek(sensor -> sensor.setData(new MemoryTemperatureHumidityData()))
                .collect(Collectors.toMap(MemoryTemperatureHumiditySensor::getId, v -> v));
        weightDataHolder.setTemperatureHumiditySensorTable(temperatureHumiditySensorMap);
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

    public void syncStateBySensor(Collection<MemoryWeightSensor> sensors) {
        if (CollectionUtils.isEmpty(sensors)) {
            return;
        }
        final Map<Integer, MemoryWeightSensor> sensorMap = sensors.stream().collect(Collectors.toMap(MemoryWeightSensor::getId, v -> v));
        for (MemorySlot slot : weightDataHolder.getSlotTable().values()) {
            final Collection<MemoryWeightSensor> slotSensors = slot.getSensors();
            if (CollectionUtils.isEmpty(slotSensors)) {
                continue;
            }

            int slotState = AbstractDevice485.STATE_ONLINE;
            for (MemoryWeightSensor sensor : slotSensors) {
                if (sensorMap.containsKey(sensor.getId())) {
                    final MemoryWeightSensor targetStateSensor = sensorMap.get(sensor.getId());
                    sensor.setState(targetStateSensor.getState());
                    if (!Objects.equals(targetStateSensor.getState(), AbstractDevice485.STATE_ONLINE)) {
                        slotState = targetStateSensor.getState();
                    }
                }
            }
            slot.setState(slotState);
        }
    }

    /**
     * 获取传感器异常信息
     *
     * @return 异常列表
     */
    public List<SystemError> getSensorErrors() {
        final List<SystemError> errors = new ArrayList<>();
        final Collection<MemorySlot> slots = weightDataHolder.getSlotTable().values();
        for (MemorySlot slot : slots) {
            final Collection<MemoryWeightSensor> sensors = slot.getSensors();
            if (CollectionUtils.isEmpty(sensors)) {
                continue;
            }
            for (MemoryWeightSensor sensor : sensors) {
                final SystemError err = tryReadError(sensor, slot);
                if (err != null) {
                    errors.add(err);
                }
            }
        }
        return errors;
    }

    private SystemError tryReadError(MemoryWeightSensor sensor, MemorySlot slot) {
        if (!Objects.equals(sensor.getState(), AbstractDevice485.STATE_OFFLINE)) {
            return null;
        }
        final SystemError error = new SystemError();
        error.setType(SystemError.TYPE_SENSOR_ERROR);
        error.setSensorAddress(sensor.getAddress485());
        error.setSensorState(sensor.getState());
        error.setSlotNo(slot.getSlotNo());
        error.setSlotState(slot.getState());
        error.setMessage("传感器离线");
        return error;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "Refresher");
    }
}
