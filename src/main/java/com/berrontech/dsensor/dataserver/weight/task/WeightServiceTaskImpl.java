package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.AbstractDevice485;
import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.util.OSUtils;
import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.conf.SerialConfiguration;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.CalibrationException;
import com.berrontech.dsensor.dataserver.weight.NativeLibraryLoader;
import com.berrontech.dsensor.dataserver.weight.SnBuildException;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DataPacket;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorItem;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorManager;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorParams;
import com.berrontech.dsensor.dataserver.weight.dto.DeviceDetails;
import com.berrontech.dsensor.dataserver.weight.dto.DeviceState;
import com.berrontech.dsensor.dataserver.weight.dto.SensorPackageCounter;
import com.berrontech.dsensor.dataserver.weight.firmware.FirmwareResource;
import com.berrontech.dsensor.dataserver.weight.firmware.UpgradeFirmwareListener;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
            sensorManager.setSensorListener(new DigitalSensorListenerImpl(weightDataHolder, weightNotifier, sensorService));
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
                    if (done) {
                        log.debug("Scan done, try build weight sensors");
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

    @Override
    public void setAllCompensationStatus(boolean enable) {
        sensorManager.SetAllCreepCorrect(enable ? 0.5 : 0);
        ThreadUtils.trySleep(1200);
        sensorManager.SetAllZeroCapture(enable ? 3 : 0.5);
    }

    @Override
    public SensorPackageCounter getPackageCounter(Integer connectionId, int address) {
        final SensorPackageCounter counter = new SensorPackageCounter();
        final DigitalSensorItem sensor = sensorManager.FirstOrNull(connectionId, address);
        if (sensor == null) {
            return null;
        }
        log.debug("Find package counter for [{}/{}], sensor=[{}]", connectionId, address, sensor.getParams().getDeviceSn());
        counter.setTotalSuccess(sensor.getTotalSuccess());
        counter.setTotalErrors(sensor.getTotalErrors());
        counter.setContinueErrors(sensor.getContinueErrors());

        counter.setELabelErrors(sensor.getELabelTotalErrors());
        counter.setELabelSuccess(sensor.getELabelTotalSuccess());
        counter.setELabelContinueErrors(sensor.getELabelContinueErrors());
        return counter;
    }

    @Override
    public void cleanPackageCounter() {
        sensorManager.ClearAllCounters();
    }

    @Override
    public DeviceDetails getSensorDetails(Integer connectionId, Integer address) {
        val d = new DeviceDetails();
        val s = sensorManager.FirstOrNull(connectionId, address);
        if (s != null) {
            try {
                s.UpdateRawCount();
                s.UpdateHighResolution(false);
                s.UpdateParams();
                d.set(DeviceDetails.ADDRESS, address)
                        .set(DeviceDetails.WEIGHT, s.getValues().getNetWeight())
                        .set(DeviceDetails.HIGH_RESOLUTION, s.getValues().getHighNet())
                        .set(DeviceDetails.STABLE, s.getValues().isStable())
                        .set(DeviceDetails.ZERO_OFFSET, s.getValues().getZeroOffset())
                        .set(DeviceDetails.UNIT, s.getValues().getUnit())
                        .set(DeviceDetails.RAW_COUNT, s.getValues().getRawCount())
                        .set(DeviceDetails.COUNTING, s.getValues().isPieceCounting())
                        .set(DeviceDetails.APW, s.getValues().getAPW())
                        .set(DeviceDetails.PIECES, s.getValues().getPieceCount())
                        .set(DeviceDetails.PT1_RAW_COUNT, s.getParams().getPoint1RawCount())
                        .set(DeviceDetails.PT1_WEIGHT, s.getParams().getPoint1Weight())
                        .set(DeviceDetails.PT2_RAW_COUNT, s.getParams().getPoint2RawCount())
                        .set(DeviceDetails.PT2_WEIGHT, s.getParams().getPoint2Weight())
                        .set(DeviceDetails.CAPACITY, s.getParams().getCapacity())
                        .set(DeviceDetails.INCREMENT, s.getParams().getIncrement())
                        .set(DeviceDetails.GEO_FACTOR, s.getParams().getGeoFactor())
                        .set(DeviceDetails.ZERO_CAPTURE, s.getParams().getZeroCapture())
                        .set(DeviceDetails.CREEP_CORRECT, s.getParams().getCreepCorrect())
                        .set(DeviceDetails.STABLE_RANGE, s.getParams().getStableRange())
                        .set(DeviceDetails.PCB_SN, s.getParams().getPCBASn())
                        .set(DeviceDetails.DEVICE_SN, s.getParams().getDeviceSn())
                        .set(DeviceDetails.DEVICE_MODEL, s.getParams().getDeviceModel())
                        .set(DeviceDetails.FIRMWARE_VERSION, s.getParams().getFirmwareVersion());

                if (s.getParams().hasELabel()) {
                    val dtl = new DeviceDetails()
                            .set(DeviceDetails.ADDRESS, s.getParams().getELabelAddress())
                            .set(DeviceDetails.PCB_SN, s.getParams().getELabelPCBASn())
                            .set(DeviceDetails.DEVICE_SN, s.getParams().getELabelDeviceSn())
                            .set(DeviceDetails.DEVICE_MODEL, s.getParams().getELabelDeviceModel())
                            .set(DeviceDetails.FIRMWARE_VERSION, s.getParams().getELabelFirmwareVersion());
                    d.set(DeviceDetails.E_LABEL_DETAILS, dtl);
                }
            } catch (Exception ex) {
                log.warn("getSensorDetails", ex);
            }
        }
        return d;
    }

    @Override
    public void upgradeFirmware(Integer connectionId, Integer address, FirmwareResource resource, UpgradeFirmwareListener listener) {
        sensorManager.StopReading();
        DigitalSensorItem s = sensorManager.FirstOrNull(connectionId, address);
        if (s == null) {
            log.warn("#{} can not be found", address);
            return;
        }

        try {
            log.debug("#{} upgradeFirmware start", address);
            s.getGroup().stopAddressPrograming();
            if (s.UpgradeSensor(resource.getContent(), listener::onUpdate)) {
                listener.onSuccess(connectionId, address);
            } else {
                listener.onError(connectionId, address, new IllegalStateException("User Aborted"));
            }
        } catch (Exception ex) {
            log.warn("#{} upgradeFirmware failed: {}", address, ex);
            listener.onError(connectionId, address, ex);
        }
    }

    @Override
    public void upgradeElabelFirmware(Integer connectionId, Integer address, FirmwareResource resource, UpgradeFirmwareListener listener) {
        // TODO 电子标签固件升级
        sensorManager.StopReading();
        DigitalSensorItem s = sensorManager.FirstOrNull(connectionId, address);
        if (s == null) {
            log.warn("#{} can not be found", address);
            return;
        }

        try {
            log.debug("#{} upgradeElabelFirmware start", address);
            s.getGroup().stopAddressPrograming();
            if (s.UpgradeELabel(resource.getContent(), listener::onUpdate)) {
                listener.onSuccess(connectionId, address);
            } else {
                listener.onError(connectionId, address, new IllegalStateException("User Aborted"));
            }
        } catch (Exception ex) {
            log.warn("#{} upgradeElabelFirmware failed: {}", address, ex);
            listener.onError(connectionId, address, ex);
        }
    }

    @Override
    public void cancelUpgrade(Integer connectionId, Integer address) {
        sensorManager.StopReading();
        DigitalSensorItem s = sensorManager.FirstOrNull(connectionId, address);
        if (s == null) {
            log.warn("#{} can not be found", address);
            return;
        }
        s.AbortUpgrading();
    }

    @Override
    public void startScanTemperatureHumiditySensors(DeviceConnection connection) throws IOException {
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
        DigitalSensorUtils.buildDigitalSensorGroups(scanManager, Collections.singletonList(connection));
        scanManager.open();
        for (val g : scanManager.getGroups()) {
            log.debug("Try start scan: connId={}, commMode={}, serialName={}, netAddr={}:{}", g.getConnectionId(), g.getCommMode(), g.getCommSerial(), g.getCommAddress(), g.getCommPort());
            g.startScanXSensors();
        }
        processXSensorScanResult();
    }

    private void processXSensorScanResult() {
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
                    if (done) {
                        log.debug("Scan done, try build weight sensors");
                        List<MemoryTemperatureHumiditySensor> sensors = new ArrayList<>();
                        // convert to MemoryWeightSensor objects
                        for (val g : scanManager.getGroups()) {
                            for (val s : g.getScanResult()) {
                                MemoryTemperatureHumiditySensor sensor = new MemoryTemperatureHumiditySensor();
                                sensor.setConnectionId(g.getConnectionId());
                                sensor.setSn(s.getDeviceSn());
                                sensor.setAddress(s.getAddress());
                                sensor.setState(MemoryWeightSensor.STATE_ONLINE);
                                sensors.add(sensor);
                            }
                        }
                        log.debug("Build done, count={}", sensors.size());
                        weightNotifier.notifyTemperatureHumidityScanDone(sensors);
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
    public DeviceState getDeviceState(Integer connectionId, Integer address) {
        if (address < DataPacket.AddressELabelStart) {
            // is sensor
            DigitalSensorItem s = sensorManager.FirstOrNull(connectionId, address);
            if (s != null) {
                val state = new DeviceState();
                state.setConnectionId(connectionId);
                state.setAddress(address);
                state.setHasElabel(s.getParams().hasELabel());
                if (!s.isOnline()) {
                    state.setDeviceState(AbstractDevice485.STATE_OFFLINE);
                } else if (s.getParams().isDisabled()) {
                    state.setDeviceState(AbstractDevice485.STATE_DISABLE);
                } else {
                    state.setDeviceState(AbstractDevice485.STATE_ONLINE);
                }
                return state;
            }
        } else if (address < DataPacket.AddressXSensorStart) {
            // is e-label
            DigitalSensorItem s = sensorManager.FirstOrNull(connectionId, address - DataPacket.AddressXSensorStart);
            if (s != null && s.getParams().hasELabel()) {
                val state = new DeviceState();
                state.setConnectionId(connectionId);
                state.setAddress(address);
                state.setHasElabel(s.getParams().hasELabel());
                if (s.getELabelContinueErrors() > DigitalSensorItem.OfflineContinueErrorThreshold) {
                    state.setDeviceState(AbstractDevice485.STATE_OFFLINE);
                } else {
                    state.setDeviceState(AbstractDevice485.STATE_ONLINE);
                }
                return state;
            }
        } else if (address < DataPacket.AddressXSensorEnd) {
            // is x-sensor
            DigitalSensorItem s = sensorManager.FirstOrNull(connectionId, address);
            if (s != null) {
                val state = new DeviceState();
                state.setConnectionId(connectionId);
                state.setAddress(address);
                state.setHasElabel(s.getParams().hasELabel());
                if (!s.isOnline()) {
                    state.setDeviceState(AbstractDevice485.STATE_OFFLINE);
                } else if (s.getParams().isDisabled()) {
                    state.setDeviceState(AbstractDevice485.STATE_DISABLE);
                } else {
                    state.setDeviceState(AbstractDevice485.STATE_ONLINE);
                }
                return state;
            }
        }
        return null;
    }

    @Override
    public boolean setElabelAddressForSn(Integer connectionId, String sn, Integer address) {
        try {
            log.debug("#{} setElabelAddressForSn: connId={}, address={}, sn={}", address, connectionId, address, sn);
            log.debug("#{} setElabelAddressForSn: sn={}, length={}", address, sn, sn.length());
            val group = DigitalSensorUtils.tryLookupGroup(sensorManager, connectionId);
            if (group != null) {
                log.debug("#{} setElabelAddressForSn: group found", address);
                log.debug("#{} setElabelAddressForSn: stop programing", address);
                group.stopAddressPrograming();
                log.debug("#{} setElabelAddressForSn: stop reading", address);
                group.stopReading();

//                log.debug("#{} setElabelAddressForSn: switch to APM mode", address);
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.APM);
//                Thread.sleep(group.getCommLongInterval());
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.APM);
//                Thread.sleep(group.getCommLongInterval());

                log.debug("#{} setElabelAddressForSn: SetAddressByDeviceSn", address);
                group.SetAddressByDeviceSn(DigitalSensorParams.toELabelAddress(address), sn);
                Thread.sleep(group.getCommLongInterval());

//                log.debug("#{} setElabelAddressForSn: switch to Normal mode", address);
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.Normal);
//                Thread.sleep(group.getCommLongInterval());
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.Normal);

//                log.debug("#{} setElabelAddressForSn: restart reading", address);
//                group.startReading2();
                return true;
            }
        } catch (Exception ex) {
            log.warn("#{} setElabelAddressForSn: connId={}, address={}, sn={}", address, connectionId, address, sn, ex);
        }
        return false;
    }

    @Override
    public boolean setSensorAddressForSn(Integer connectionId, String sn, Integer address) {
        try {
            log.debug("#{} setSensorAddressForSn: connId={}, address={}, sn={}", address, connectionId, address, sn);
            log.debug("#{} setSensorAddressForSn: sn={}, length={}", address, sn, sn.length());
            val group = DigitalSensorUtils.tryLookupGroup(sensorManager, connectionId);
            if (group != null) {
                log.debug("#{} setSensorAddressForSn: group found", address);
                log.debug("#{} setSensorAddressForSn: stop programing", address);
                group.stopAddressPrograming();
                log.debug("#{} setSensorAddressForSn: stop reading", address);
                group.stopReading();

//                log.debug("#{} setSensorAddressForSn: switch to APM mode", address);
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.APM);
//                Thread.sleep(group.getCommLongInterval());
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.APM);
//                Thread.sleep(group.getCommLongInterval());

                log.debug("#{} setSensorAddressForSn: SetAddressByDeviceSn", address);
                group.SetAddressByDeviceSn(address, sn);
                Thread.sleep(group.getCommLongInterval());

//                log.debug("#{} setSensorAddressForSn: switch to Normal mode", address);
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.Normal);
//                Thread.sleep(group.getCommLongInterval());
//                DigitalSensorItem.setAllWorkMode(group.Driver, DataPacket.EWorkMode.Normal);

//                log.debug("#{} setSensorAddressForSn: restart reading", address);
//                group.startReading2();
                return true;
            }
        } catch (Exception ex) {
            log.warn("#{} setSensorAddressForSn", address, ex);
        }
        return false;
    }

    @Override
    public String rebuildSnForElabel(Integer connectionId, Integer address) throws SnBuildException {
        String sn = null;
        try {
            DigitalSensorItem sensor = DigitalSensorUtils.tryLookupSensor(sensorManager, connectionId, address);
            if (sensor != null) {
                sn = DigitalSensorParams.BuildNewELabelDeviceSn();
                sensor.UnlockELabel();
                sensor.SetELabelSn(sn);
                sensor.getParams().setBackupELabelSn(sn);
                return sn;
            } else {
                log.warn("rebuildSnForElabel: can not found sensor, connectionId={}, address={}", connectionId, address);
                throw new SnBuildException(connectionId, address, sn);
            }
        } catch (Exception ex) {
            log.warn("rebuildSnForElabel error: connectionId={}, address={}", connectionId, address, ex);
            throw new SnBuildException(connectionId, address, sn);
        }
    }

    @Override
    public String rebuildSnForSensor(Integer connectionId, Integer address) throws SnBuildException {
        String sn = null;
        try {
            DigitalSensorItem sensor = DigitalSensorUtils.tryLookupSensor(sensorManager, connectionId, address);
            if (sensor != null) {
                sn = DigitalSensorParams.BuildNewSensorDeviceSn();
                sensor.Unlock();
                sensor.SetDeviceSn(sn);
                sensor.getParams().setBackupSensorSn(sn);
                return sn;
            } else {
                log.warn("rebuildSnForSensor: can not found sensor, connectionId={}, address={}", connectionId, address);
                throw new SnBuildException(connectionId, address, sn);
            }
        } catch (Exception ex) {
            log.warn("rebuildSnForSensor error: connectionId={}, address={}", connectionId, address, ex);
            throw new SnBuildException(connectionId, address, sn);
        }
    }

    @Override
    public void calibrateTemperatureSensor(Integer connectionId, Integer address, BigDecimal currentTemperature) throws CalibrationException {
        // TODO 温度传感器标定
        log.info("Calibrate temp sensor [{}], temp=[{}]", address, currentTemperature);
        try {
            DigitalSensorItem sensor = DigitalSensorUtils.tryLookupSensor(sensorManager, connectionId, address);
            if (sensor != null) {
                if (sensor.getParams().getDeviceType() != DigitalSensorParams.EDeviceType.TempHumi) {
                    throw new Exception(String.format("This sensor(%d) is not a TemperatureSensor", sensor.getParams().getDeviceType()));
                }
                if (!sensor.UpdateXSensors()) {
                    throw new Exception(String.format("Update xSensor failed"));
                }
                BigDecimal offset = currentTemperature.subtract(sensor.getValues().getXSensors()[0]);
                log.info("Calibrate temp sensor [{}], offset=[{}]", address, offset);
                sensor.SetZeroOffset(true, offset.floatValue());
            } else {
                throw new Exception("Cannot found sensor");
            }
        } catch (Exception ex) {
            log.warn("calibrateTemperatureSensor error: connectionId={}, address={}", connectionId, address, ex);
            throw new CalibrationException(ex.getMessage());
        }
    }
}
