package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.util.OSUtils;
import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.conf.SerialConfiguration;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.NativeLibraryLoader;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorItem;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorManager;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    private final WeightNotifier weightNotifier;
    /**
     * Digital Sensor Manager
     */
    private final DigitalSensorManager sensorManager;

    private final WeightSensorService sensorService;
    private NativeLibraryLoader nativeLibraryLoader;
    private SerialConfiguration serialConfiguration;


    @Autowired
    public WeightServiceTaskImpl(WeightDataHolder weightDataHolder,
                                 WeightNotifier weightNotifier,
                                 DigitalSensorManager sensorManager,
                                 WeightSensorService sensorService,
                                 SerialConfiguration serialConfiguration,
                                 NativeLibraryLoader nativeLibraryLoader) {
        this.weightDataHolder = weightDataHolder;
        this.weightNotifier = weightNotifier;
        this.sensorManager = sensorManager;
        this.sensorService = sensorService;
        this.serialConfiguration = serialConfiguration;
        this.nativeLibraryLoader = nativeLibraryLoader;
    }

    /**
     * 组件初始换完成之后加载本地SO库
     */
    @PostConstruct
    public void loadLibrary() {
        final String osName = OSUtils.getOsName();
        final String arch = OSUtils.getArch();

        nativeLibraryLoader.loadLib(osName, arch, OSUtils.isWindows());
    }

    @Override
    public void setup() {

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
                    // write zero offset to database in period
                    Thread.sleep(1000);
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
            sensorManager.shutdown();
        }
    }

    @Override
    public void afterStop() {

    }


    ////////////////////////////////////////////////////////////////////////////
    // WeightControllerImpl

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
        DigitalSensorUtils.buildDigitalSensorGroups(scanManager, connections);
        scanManager.open();
        for (val g : scanManager.getGroups()) {
            log.debug("Try start scan: connId={}, commMode={}, serialName={}, netAddr={}:{}", g.getConnectionId(), g.getCommMode(), g.getCommSerial(), g.getCommAddress(), g.getCommPort());
            g.startScan();
        }
        processScanResult();
    }

    @Override
    public void startScan(DeviceConnection connection) throws IOException {
        startScan(Collections.singletonList(connection));
    }

    @Override
    public void setSku(String slotNo, MemorySku sku) {
        if (sensorManager == null) {
            return;
        }
        DigitalSensorItem sensor = sensorManager.FirstOrNull(slotNo);
        if (sensor == null) {
            log.warn("Slot({}) is not found", slotNo);
        } else {
            DigitalSensorUtils.setSkuToSensor(sku, sensor.getPassenger().getMaterial());
        }
    }

    @Override
    public void onMetaDataChanged() {
        startSensorManager();
    }

    @Override
    public void shutdown() {
        if (sensorManager != null) {
            sensorManager.shutdown();
        }
    }

    @Override
    public void onSlotStateChanged(String slotNo, int state) {
        switch (state) {
            case WeightSensor.STATE_ONLINE: {
                sensorManager.EnableSlot(Collections.singleton(slotNo), true);
                break;
            }
            case WeightSensor.STATE_DISABLE: {
                sensorManager.EnableSlot(Collections.singleton(slotNo), false);
                break;
            }
            default: {
                log.warn("try set slot({}) to an unaccepted state: {}", slotNo, state);
                break;
            }
        }
    }

    @Override
    public void doZeroAll() {
        if (sensorManager.isOpened()) {
            sensorManager.DoAllZero(true);
            log.info("Do all zero");
        }
    }

    @Override
    public void doZero(String slotNo) {
        val sensors = sensorManager.Filter(slotNo);
        if (sensors == null || sensors.size() <= 0) {
            log.info("Can not found slot({})", slotNo);
        } else {
            for (val sensor : sensors) {
                try {
                    sensor.DoZero(true);
                    log.info("#{} Slot({}) set to zero", sensor.getParams().getAddress(), slotNo);
                } catch (Exception ex) {
                    log.warn("#{} Slot({}) Do zero failed,{}", sensor.getParams().getAddress(), slotNo, ex.getMessage());
                }
            }
        }
    }

    @Override
    public boolean isScanning() {
        if (scanManager == null) {
            return false;
        }
        return scanManager.isOpened();
    }

    @Override
    public void highlight(String slotNo, long duration) {
        sensorManager.HighlightSlot(slotNo, duration);
    }

    @Override
    public void highlight(Collection<String> slots, long duration) {
        sensorManager.HighlightSlots(slots, duration);
    }
    //////////////////////////////////////////////////////////
    // local functions

    DigitalSensorManager scanManager;
    boolean scanning = false;
    final Object scanLock = new Object();
    private ExecutorService threadPool = null;

    private ExecutorService createThreadPool() {
        if (threadPool == null) {
            threadPool = ThreadUtils.createThreadPoolExecutorService(2, 2, "WeightControllerThread");
        }
        return threadPool;
    }

    private void startSensorManager() {
        DigitalSensorUtils.buildDigitalSensors(sensorManager, weightDataHolder);
        if (sensorManager.getSensorListener() == null) {
            sensorManager.setSensorListener(new DigitalSensorListenerImpl(weightDataHolder, weightNotifier));
        }
        sensorManager.Init();
        sensorManager.open();
        sensorManager.startReading();
    }

    private void processScanResult() {
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
                                sensor.setHasElable(s.hasELabel());
                                sensors.add(sensor);
                            }
                        }
                        log.debug("Build done, count={}", sensors.size());

                        weightNotifier.notifyScanDone(sensors);
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


}
