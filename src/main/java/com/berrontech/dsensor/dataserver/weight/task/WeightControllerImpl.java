package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorGroup;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorManager;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author lastn
 */
@Component
@Slf4j
public class WeightControllerImpl implements WeightController {

    private final WeightServiceTaskImpl weightServiceTask;
    public final WeightNotifier weightNotifier;

    DigitalSensorManager scanManager;
    boolean scanning = false;


    public WeightControllerImpl(WeightServiceTaskImpl weightServiceTask,
                                WeightNotifier weightNotifier) {
        this.weightServiceTask = weightServiceTask;
        this.weightNotifier = weightNotifier;
    }


    //////////////////////////////////////////////////////////////

    @Override
    public void startScan(Collection<DeviceConnection> connections) throws IOException {
        if (scanning) {
            throw new IOException("Scanning is in processing");
        }

        log.debug("Notify scan with full addresses");
        // shutdown connections
        log.debug("Try shutdown connections");
        weightServiceTask.getSensorManager().shutdown();

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
                scanning = false;
                scanManager.shutdown();
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
}
