package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.*;
import com.berrontech.dsensor.dataserver.weight.holder.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:47
 * Class Name: WeightServiceTaskImpl
 * Author: Levent8421
 * Description:
 * 称重服务
 *
 * @author Levent8421
 */
@Component
@Slf4j
@Data
public class WeightServiceTaskImpl implements WeightServiceTask, WeightController {

    /**
     * 称重数据临时保存于此
     */
    private final WeightDataHolder weightDataHolder;
    /**
     * TCP API Client
     */
    private final ApiClient apiClient;

    private final WeightNotifier weightNotifier;
    /**
     * Digital Sensor Manager
     */
    private final DigitalSensorManager sensorManager;

    public WeightServiceTaskImpl(WeightDataHolder weightDataHolder,
                                 ApiClient apiClient,
                                 WeightNotifier weightNotifier,
                                 DigitalSensorManager sensorManager) {
        this.weightDataHolder = weightDataHolder;
        this.apiClient = apiClient;
        this.weightNotifier = weightNotifier;
        this.sensorManager = sensorManager;
    }

    @Override
    public void setup() {
        buildDigitalSensors(sensorManager, weightDataHolder);
        sensorManager.setSensorListener(new DigitalSensorListener() {
            @Override
            public boolean onSensorStateChanged(DigitalSensorItem sensor) {
                log.debug("#{} Notify onSensorStateChanged", sensor.getParams().getAddress());
                try {
                    WeightSensor s1 = weightDataHolder.getWeightSensors().stream()
                            .filter(s -> s.getDeviceSn().equals(sensor.getParams().getDeviceSn()))
                            .findFirst()
                            .orElse(null);
                    if (s1 != null) {
                        MemoryWeightSensor s2 = MemoryWeightSensor.of(s1);
                        s2.setState(toState(sensor));
                        Collection<MemoryWeightSensor> sensors = Collections.singleton(s2);
                        weightNotifier.sensorStateChanged(sensors);
                    }
                    return true;
                } catch (Exception ex) {
                    log.warn("notify onSensorStateChanged error: {}", ex.getMessage());
                    return false;
                }
            }

            @Override
            public boolean onPieceCountChanged(DigitalSensorItem sensor) {
                log.debug("#{} Notify onPieceCountChanged", sensor.getParams().getAddress());

                try {
                    final MemorySlot slot = weightDataHolder.getSlotTable().get(sensor.getShortName());
                    if (slot == null) {
                        log.debug("#{} Could not found slot ({})", sensor.getParams().getAddress(), sensor.getShortName());
                        return false;
                    }
                    if (slot.getData() == null) {
                        slot.setData(new MemoryWeightData());
                    }
                    val slotData = slot.getData();
                    slotData.setWeight(sensor.getValues().getNetWeight().multiply(BigDecimal.valueOf(1000)).intValue());
                    slotData.setCount(sensor.getValues().getPieceCount());
                    slotData.setTolerance((int) (sensor.getValues().getPieceCountAccuracy() * 100));
                    slotData.setToleranceState(sensor.isCountInAccuracy() ? MemoryWeightData.TOLERANCE_STATE_CREDIBLE : MemoryWeightData.TOLERANCE_STATE_INCREDIBLE);
                    final Collection<MemorySlot> slots = Collections.singleton(slot);
                    weightNotifier.countChange(slots);
                    return true;
                } catch (Exception ex) {
                    log.warn("notify onPieceCountChanged error: {}", ex.getMessage());
                    return false;
                }
            }

            @Override
            public boolean onSlotStateChanged(DigitalSensorItem sensor) {
                log.debug("#{} Notify onSlotStateChanged", sensor.getParams().getAddress());
                return true;
            }

            @Override
            public boolean onWeightChanged(DigitalSensorItem sensor) {
                log.debug("#{} Notify onWeightChanged", sensor.getParams().getAddress());
                try {
                    MemorySlot slot = weightDataHolder.getSlotTable().get(sensor.getShortName());
                    if (slot != null) {
                        if (slot.getData() == null) {
                            slot.setData(new MemoryWeightData());
                        }
                        slot.getData().setWeight(sensor.getValues().getNetWeight().multiply(BigDecimal.valueOf(1000)).intValue());
                        slot.getData().setWeightState(toState(sensor));
                        slot.setState(toState(sensor));
                    }
                    return true;
                } catch (Exception ex) {
                    log.warn("notify onPieceCountChanged error: {}", ex.getMessage());
                    return false;
                }
            }
        });
        sensorManager.open();
        sensorManager.startReading();
    }

    private int toState(DigitalSensorItem sensor) {
        int state;
        if (!sensor.IsOnline()) {
            state = WeightSensor.STATE_OFFLINE;
        } else {
            switch (sensor.getValues().getStatus()) {
                case Dynamic:
                case Stable: {
                    state = WeightSensor.STATE_ONLINE;
                    break;
                }
                case UnderLoad: {
                    state = WeightSensor.STATE_UNDER_LOAD;
                    break;
                }
                case OverLoad: {
                    state = WeightSensor.STATE_OVERLOAD;
                    break;
                }
                default: {
                    state = MemoryWeightSensor.STATE_OFFLINE;
                    break;
                }
            }
        }
        return state;
    }

    public static void buildDigitalSensors(DigitalSensorManager sensorManager, WeightDataHolder weightDataHolder) {
        sensorManager.shutdown();
        sensorManager.getGroups().clear();
        for (DeviceConnection conn : weightDataHolder.getConnections()) {
            try {
                int count = (int) weightDataHolder.getWeightSensors().stream().filter((a) -> a.getConnectionId().equals(conn.getId())).count();
                if (count <= 0) {
                    continue;
                }
                DigitalSensorGroup group = sensorManager.NewGroup();
                switch (conn.getType()) {
                    default: {
                        log.info("Unknow connection type: {}", conn.getType());
                        break;
                    }
                    case DeviceConnection.TYPE_SERIAL: {
                        log.debug("Add group on serial: {}", conn.getTarget());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Com);
                        group.setCommSerial(conn.getTarget());
                        break;
                    }
                    case DeviceConnection.TYPE_NET: {
                        log.debug("Add group on tcp: {}", conn.getTarget());
                        String[] parts = conn.getTarget().split(":");
                        group.setCommMode(DigitalSensorGroup.ECommMode.Net);
                        group.setCommAddress(parts[0]);
                        if (parts.length > 1) {
                            group.setCommPort(Integer.parseInt(parts[1]));
                        } else {
                            final int defaultPort = 10086;
                            log.info("Use default net port: {}", defaultPort);
                            group.setCommPort(defaultPort);
                        }
                        break;
                    }
                }
                log.debug("Build sensors: {}", count);
                group.BuildSensors(count);

                int pos = 0;
                for (WeightSensor sen : weightDataHolder.getWeightSensors()) {
                    if (sen.getConnectionId().equals(conn.getId())) {
                        log.debug("Config sensor: conn={}, sen={}", conn, sen);
                        val sensor = group.getSensors().get(pos++);
                        val params = sensor.getParams();
                        params.setAddress(sen.getAddress());
                        params.setDeviceSn(sen.getDeviceSn());

                        var slot = weightDataHolder.getSlots().stream().filter(a -> a.getId().equals(sen.getSlotId())).findFirst().get();
                        val ms = weightDataHolder.getSlotTable().get(slot.getSlotNo());
                        sensor.setSubGroup(ms.getSlotNo());
                        val sku = ms.getSku();
                        if (sku != null) {
                            sensor.getPassenger().getMaterial().setNumber(sku.getSkuNo());
                            sensor.getPassenger().getMaterial().setName(sku.getName());
                            sensor.getPassenger().getMaterial().setAPW(sku.getApw() / 1000.0);
                            sensor.getPassenger().getMaterial().setTolerancePercent(sku.getTolerance());
                        }
                        if (slot.getHasElabel()) {
                            params.setELabelModel(DigitalSensorParams.EELabelModel.V3);
                        }

                    }
                }
            } catch (Exception ex) {
                log.error("buildDigitalSensors error: connId={}, target={}", conn.getId(), conn.getTarget(), ex);
            }
        }
    }

    /**
     * 主循环
     *
     * @return return false表示不再进行下一次循环
     */
    @Override
    public boolean loop() {
        if (sensorManager != null) {
            if (sensorManager.isOpened()) {
                try {
                    // I do not know what todo now
                    Thread.sleep(100);
                } catch (Exception ex) {
                    // Do nothing
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void beforeStop() {
        if (sensorManager != null) {
            sensorManager.StopReading();
            sensorManager.close();
        }
    }

    @Override
    public void afterStop() {

    }


    ////////////////////////////////////////////////////////////////////////////
    // WeightControllerImpl

    DigitalSensorManager scanManager;
    boolean scanning = false;
    final Object scanLock = new Object();


    @Override
    public void startScan(Collection<DeviceConnection> connections) throws IOException {
        synchronized (scanLock) {
            if (scanning) {
                throw new IOException("Scanning is in processing");
            }
            scanning = true;
        }

        log.debug("Notify scan with full addresses");
        // shutdown connections
        log.debug("Try shutdown connections");
        sensorManager.shutdown();

        // build scanner
        if (scanManager == null) {
            log.debug("Try build scan manager");
            scanManager = new DigitalSensorManager();
        }
        log.debug("Try build connection");
        buildDigitalSensors(scanManager, connections);
        scanManager.open();
        for (val g : scanManager.getGroups()) {
            log.debug("Try start scan: connId={}, commMode={}, serialName={}, netAddr={}:{}", g.getConnectionId(), g.getCommMode(), g.getCommSerial(), g.getCommAddress(), g.getCommPort());
            g.startScan();
        }
        createThreadPool().execute(() ->
        {
            try {
                while (scanManager.isOpened()) {
                    boolean done = true;
                    for (val g : scanManager.getGroups()) {
                        if (g.isAddressPrograming()) {
                            done = false;
                            break;
                        }
                    }
                    log.debug("Scan done, try build weight sensors");
                    if (done) {
                        List<MemoryWeightSensor> sensors = new ArrayList<>();
                        // convert to MemoryWeightSensor objects
                        for (val g : scanManager.getGroups()) {
                            for (val s : g.getScanResult()) {
                                MemoryWeightSensor sensor = new MemoryWeightSensor();
                                sensor.setConnectionId(g.getConnectionId());
                                sensor.setDeviceSn(s.getDeviceSn());
                                sensor.setAddress485(s.getAddress());
                                sensor.setState(MemoryWeightSensor.STATE_ONLINE);
                                sensors.add(sensor);
                            }
                        }
                        log.debug("Build done, count={}", sensors.size());

                        weightNotifier.notifySensorList(sensors);
                        break;
                    } else {
                        Thread.sleep(300);
                    }
                }
            } catch (Exception ex) {
                log.warn("Scan failed", ex);
            } finally {
                scanManager.shutdown();
                synchronized (scanLock) {
                    scanning = false;
                }
            }
        });
    }

    @Override
    public void startScan(DeviceConnection connection, int countOfSensors) throws IOException {

    }

    public static void buildDigitalSensors(DigitalSensorManager sensorManager, Collection<DeviceConnection> connections) {
        sensorManager.shutdown();
        sensorManager.getGroups().clear();
        for (DeviceConnection conn : connections) {
            try {
                DigitalSensorGroup group = sensorManager.NewGroup();
                switch (conn.getType()) {
                    default: {
                        log.info("Unknow connection type: {}", conn.getType());
                        break;
                    }
                    case DeviceConnection.TYPE_SERIAL: {
                        log.debug("Add group on serial: {}", conn.getTarget());
                        group.setConnectionId(conn.getId());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Com);
                        group.setCommSerial(conn.getTarget());
                        break;
                    }
                    case DeviceConnection.TYPE_NET: {
                        log.debug("Add group on tcp: {}", conn.getTarget());
                        String[] parts = conn.getTarget().split(":");
                        group.setConnectionId(conn.getId());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Net);
                        group.setCommAddress(parts[0]);
                        if (parts.length > 1) {
                            group.setCommPort(Integer.parseInt(parts[1]));
                        } else {
                            final int defaultPort = 10086;
                            log.info("Use default net port: {}", defaultPort);
                            group.setCommPort(defaultPort);
                        }
                        break;
                    }
                }
                log.debug("Build single default sensor");
                group.BuildSingleDefaultSensors();
            } catch (Exception ex) {
                log.error("buildDigitalSensors error: connId={}, target={}", conn.getId(), conn.getTarget(), ex);
            }
        }
    }

    private ExecutorService ThreadPool = null;

    private ExecutorService createThreadPool() {
        if (ThreadPool == null) {
            ThreadPool = ThreadUtils.createThreadPoolExecutorService(2, 2, "WeightControllerThread");
        }
        return ThreadPool;
    }


    ///////////////////////////////////////////////////////////////

    @Override
    public void setSku(String slotNo, MemorySku sku) {

    }

    @Override
    public void onConnectionChanged(Collection<DeviceConnection> connections) {

    }

    @Override
    public void onMetaDataChanged() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void updateSlotNo(Integer slotId, String slotNo) {

    }

    @Override
    public void onSlotStateChanged(String slotNo, int state) {

    }

    @Override
    public void doZeroAll() {
        // TODO  全部清零
        if (sensorManager.isOpened()) {
            sensorManager.DoAllZero(true);
            log.info("Do all zero");
        }
    }

    @Override
    public void doZero(String slotNo) {
        // TODO 清零指定货道
        val sensor = sensorManager.FirstOrNull(slotNo);
        if (sensor == null)
            log.info("Can not found slot ({})", slotNo);
        else {
            try {
                sensor.DoZero(true);
            } catch (Exception ex) {
                log.warn("#{} Do zero failed", sensor.getParams().getAddress(), ex.getMessage());
            }
        }
    }
}
