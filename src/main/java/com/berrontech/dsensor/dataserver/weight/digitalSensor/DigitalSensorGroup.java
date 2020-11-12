package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/9/25 10:19
 * Class Name: DigitalSensorGroup
 * Author: Lastnika
 * Description:
 *
 * @author Lastnika
 */
@Slf4j
@Data
@SuppressWarnings("unused")
public class DigitalSensorGroup {
    private DigitalSensorManager Manager;
    private String Title = "G";
    private int Id = 0;

    public String getName() {
        return Title + Id;
    }

    /**
     * 连接类型
     */
    public enum ECommMode {
        /**
         * 未知
         */
        None,
        /**
         * 串口
         */
        Com,
        /**
         * TCP网络
         */
        Net,
    }

    public List<ECommMode> CommModes = Arrays.asList(ECommMode.None, ECommMode.Com, ECommMode.Net);
    private int ConnectionId;
    private ECommMode CommMode = ECommMode.Com;
    private String CommSerial;
    private String CommAddress;
    private int CommPort;

    public boolean isModeNone() {
        return CommMode == ECommMode.None;
    }

    public boolean isModeNotNone() {
        return !isModeNone();
    }

    public boolean isModeCom() {
        return CommMode == ECommMode.Com;
    }

    public boolean isModeNet() {
        return CommMode == ECommMode.Net;
    }

    public DigitalSensorDriver Driver = new DigitalSensorDriver();
    private int CommInterval = 10;
    private int CommLongInterval = 50;

    private int ReadTimeout = 100;
    private boolean OnlyShowStable = false;

    private List<DigitalSensorItem> Sensors = new ArrayList<>();

    public int getSensorCount() {
        return Sensors.size();
    }

    private DigitalSensorItem CurrentSensor;

//    private List<DigitalSensorSubGroup> SubGroups = new ArrayList<>();

    public void BuildSubGroups() {
        BuildClusterSensors();
        BuildMergedSensors();

//        List<DigitalSensorSubGroup> sgs = new List<DigitalSensorSubGroup>();
//        var lst = (from n in MergedSensors select n.SubGroup)?.Distinct().OrderByDescending(a => a).ToList();
//        foreach (var n in lst)
//        {
//            var sg = new DigitalSensorSubGroup()
//            {
//                Name = n,
//                Sensors = (from s in MergedSensors where s.SubGroup == n select s)?.OrderBy(a => a.SubGroupPosition).ToList() ?? new List<DigitalSensorItem>(),
//            };
//            sgs.Add(sg);
//        }
//        SubGroups = sgs;
    }


    private List<DigitalSensorCluster> ClusterSensors;

    private List<DigitalSensorItem> MergedSensors;

    private void BuildClusterSensors() {
        List<DigitalSensorCluster> clusters = new ArrayList<>();
        val clus = Sensors.stream().filter(s -> s.getSubGroupPosition() > 0).collect(Collectors.groupingBy(DigitalSensorItem::getSubGroup));
        for (Map.Entry<String, List<DigitalSensorItem>> itItem : clus.entrySet()) {
            val group = itItem.getKey();
            val item = new DigitalSensorCluster();
            item.setChildren(itItem.getValue());
            val first = item.getFirstChild();
            item.setGroup(first.getGroup());
            item.setSubGroup(first.getSubGroup());
            item.setPassenger(first.getPassenger());
            item.setSubGroupId(first.getSubGroupId());
            item.calc();
            clusters.add(item);
        }
        ClusterSensors = clusters;
    }

    private void BuildMergedSensors() {
        List<DigitalSensorItem> sensors = new ArrayList<>();
        val lst1 = Sensors.stream().filter(s -> s.getSubGroupPosition() == 0).collect(Collectors.toList());
        sensors.addAll(lst1);
        sensors.addAll(ClusterSensors);
        MergedSensors = sensors;
    }


    private boolean Opened = false;
    private boolean Reading = false;
    public boolean AddressPrograming = false;

    public boolean isNotOpened() {
        return !Opened;
    }

    public boolean isNotReading() {
        return !Reading;
    }

    public boolean isNotAddressPrograming() {
        return !AddressPrograming;
    }

    public boolean canStartAddressPrograming() {
        return Opened && isNotAddressPrograming();
    }

    public void Open() {
        switch (CommMode) {
            default: {
                break;
            }
            case Com: {
                OpenCom(CommSerial);
                break;
            }
            case Net: {
                OpenNet(CommAddress, CommPort);
                break;
            }
        }
    }

    public void OpenCom(String port) {
        OpenCom(port, 115200);
    }

    public void OpenCom(String port, int baud) {
        Close();
        Opened = true;
        Driver.OpenCom(port, baud);
    }

    public void OpenNet(String address, int port) {
        Close();
        Opened = true;
        Driver.OpenNet(address, port);
    }

    public void Close() {
        Driver.close();
        Opened = false;
    }

    public List<String> getSubGroupNames() {
        List<String> lst = new ArrayList<>();
        for (DigitalSensorItem s : Sensors) {
            lst.add(s.SubGroup);
        }
        Collections.sort(lst);
        if (!lst.contains("")) {
            lst.add(0, "");
        }
        return lst;
    }

    public static int getMaxSensorAddress(List<DigitalSensorItem> sensors) {
        return sensors.stream().max(Comparator.comparingInt(a -> a.getParams().getAddress())).orElse(new DigitalSensorItem()).getParams().getAddress();
    }

    public void BuildSensors(int count) {
        if (count <= 0) {
            return;
        }
        List<DigitalSensorItem> sensors = new ArrayList<>();
        if (Sensors != null) {
            sensors = Sensors;
        }
        while (sensors.size() > count) {
            sensors.remove(sensors.size() - 1);
        }
        while (sensors.size() < count) {
            sensors.add(DigitalSensorItem.NewSensor(getMaxSensorAddress(sensors) + 1, Driver, this));
        }
        Sensors = sensors;
    }

    public void ReBuildSensors(int count) {
        if (count <= 0) {
            return;
        }
        List<DigitalSensorItem> sensors = new ArrayList<>();
        while (sensors.size() < count) {
            sensors.add(DigitalSensorItem.NewSensor(sensors.size() + 1, Driver, this));
        }
        Sensors = sensors;
    }

    public void BuildSingleDefaultSensors() {
        List<DigitalSensorItem> sensors = new ArrayList<>();
        sensors.add(DigitalSensorItem.NewDefaultSensor(Driver, this));
        Sensors = sensors;
    }

    public void Init(DigitalSensorManager manager) {
        Manager = manager;
        for (DigitalSensorItem s : Sensors) {
            s.Driver = Driver;
            s.Group = this;
        }
        BuildSubGroups();
    }

    public static List<String> removeDuplicate(List<String> list) {
        Set<String> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }

//    public void BuildSubGroups() {
//        try {
//            List<DigitalSensorSubGroup> sgs = new ArrayList<>();
//            List<String> lst = new ArrayList<>();
//            for (DigitalSensorItem n : Sensors) {
//                lst.add(n.getSubGroup());
//            }
//            Collections.sort(removeDuplicate(lst));
//            for (String n : lst) {
//                DigitalSensorSubGroup sg = new DigitalSensorSubGroup();
//                sg.Name = n;
//                for (DigitalSensorItem s : Sensors) {
//                    if (s.getSubGroup().equals(n)) {
//                        sg.getSensors().add(s);
//                    }
//                }
//                sgs.add(sg);
//            }
//            SubGroups = sgs;
//        } catch (Exception ex) {
//            log.warn("BuildSubGroups failed", ex);
//        }
//    }

    public static DigitalSensorGroup NewGroup(int id, DigitalSensorManager manager) {
        DigitalSensorGroup group = new DigitalSensorGroup();
        group.setId(id);
        group.setManager(manager);
        group.BuildSensors(1);
        return group;
    }

    public static DigitalSensorGroup NewSingleDefaultGroup(DigitalSensorManager manager) {
        DigitalSensorGroup group = new DigitalSensorGroup();
        group.setManager(manager);
        group.BuildSingleDefaultSensors();
        return group;
    }

    private ExecutorService ThreadPool = null;

    private ExecutorService createThreadPool() {
        if (ThreadPool == null) {
            ThreadPool = ThreadUtils.createThreadPoolExecutorService(2, 2, getName() + "Thread");
        }
        return ThreadPool;
    }

    public void startReading() {
        if (isNotOpened() || isReading()) {
            return;
        }
        Reading = true;

        log.info("Start reading");

        createThreadPool().execute(() -> {
            try {
                int idx = 0;
                while (isReading()) {
                    if (idx >= Sensors.size()) {
                        idx = 0;
                    }
                    try {

                        DigitalSensorItem sensor = Sensors.get(idx);
                        if (sensor != null) {
                            if (sensor.getParams().isXSensor()) {
                                sensor.UpdateXSensors();
                                sensor.TryNotifyListener();
                            } else {
                                sensor.UpdateRawCount();
                                sensor.UpdateHighResolution(OnlyShowStable);

                                ClusterSensors.stream().filter(s -> s.getChildren().contains(sensor)).findFirst().ifPresent(DigitalSensorCluster::calc);
                            }
                        }
                        Thread.sleep(CommInterval);
                    } catch (TimeoutException ex) {
                        log.debug(ex.getMessage());
                    }
                    idx++;
                }
            } catch (Exception ex) {
                log.warn("Do Reading failed", ex);
            } finally {
                Reading = false;
            }
        });
    }

    public void startReading2() {
        log.debug("Start reading [{}]", getName());
        if (isNotOpened() || isReading()) {
            log.debug("Is not opend or reading ");
            return;
        }
        Reading = true;

        log.info("Start reading2, sensors={}", Sensors.size());

        createThreadPool().execute(() ->
        {
            try {
                int idx = 0;
                boolean lastConnected = false;
                while (isReading()) {
                    if (lastConnected != Driver.getConnection().isConnected()) {
                        lastConnected = Driver.getConnection().isConnected();
                        if (Driver.getConnection().isConnected()) {
                            log.info("Conn={} Connected", ConnectionId);
                        } else {
                            log.warn("Conn={} DISCONNECTED", ConnectionId);
                        }
                    }
                    if (idx >= Sensors.size()) {
                        idx = 0;
                    }
                    if (lastConnected) {
                        DigitalSensorItem sensor = Sensors.get(idx);
                        try {
                            if (sensor != null) {
                                if (sensor.getParams().isXSensor()) {
                                    sensor.UpdateXSensors();
                                    sensor.TryNotifyListener();
                                } else {
                                    if (sensor.UpdateHighResolution2(OnlyShowStable)) {
                                        //log.debug("#{} UpdateELabel in UpdateHighResolution2", Params.getAddress());
                                        sensor.UpdateELabel();
                                        //log.debug("#{} UpdateELabel in UpdateHighResolution2 done", Params.getAddress());
                                        val s2 = ClusterSensors.stream().filter(s -> s.getChildren().contains(sensor)).findFirst().orElse(null);
                                        if (s2 != null) {
                                            s2.calc();
                                            s2.UpdateELabel();
                                            s2.TryNotifyListener();
                                        }
                                    }
                                }
                            }
                        } catch (TimeoutException ex) {
                            log.debug("#{} Packet Lost", sensor.getParams().getAddress());
                        } catch (IOException ex) {
                            // port closed
                            try {
                                // try release port
                                Close();
                            } catch (Exception ex2) {
                                // Ignore
                            }
                            for (DigitalSensorItem s : Sensors) {
                                s.setOnline(false);
                                s.TryNotifyListener();
                            }
                            log.info("Port is closed");
                            break;
                        } catch (Exception ex) {
                            // unexpected error
                            log.warn("Error in StartReading2 ", ex);
                        }
                    }
                    Thread.sleep(CommInterval);
                    idx++;
                }
            } catch (Exception ex) {
                log.warn("StartReading2 failed", ex);
            } finally {
                Reading = false;
            }
        });
    }

    public void stopReading() {
        if (isNotReading()) {
            return;
        }
        Reading = false;
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
            // do nothing
        }
        for (DigitalSensorItem s : Sensors) {
            s.setOnline(false);
        }
    }

    public void startAddressPrograming() {
        startAddressPrograming(Sensors);
    }

    public void startAddressPrograming(List<DigitalSensorItem> sensors) {
        if (isNotOpened() || isAddressPrograming()) {
            return;
        }
        AddressPrograming = true;

        log.info("Start programing addresses");
        createThreadPool().execute(() -> {
            try {
                log.debug("Clear online status");
                for (DigitalSensorItem s : sensors) {
                    s.setAddressMode(DigitalSensorItem.EAddressMode.Waiting);
                }
                synchronized (Driver.getLock()) {
                    for (DigitalSensorItem s : sensors) {
                        boolean done = false;
                        s.setAddressMode(DigitalSensorItem.EAddressMode.ProgramingSensor);
                        CurrentSensor = s;
                        while (isAddressPrograming() && !done) {
                            s.setAddressMode(DigitalSensorItem.EAddressMode.BindingELabel);
                            try {
                                log.debug("#{} Programing", s.getParams().getAddress());
                                if (s.getAddressMode() == DigitalSensorItem.EAddressMode.ProgramingSensor) {
                                    s.SetAddress();
                                    log.debug("#{} Address set", s.getParams().getAddress());
                                    if (s.getParams().hasELabel()) {
                                        s.setAddressMode(DigitalSensorItem.EAddressMode.BindingELabel);
                                    }
                                }
                                if (s.getAddressMode() == DigitalSensorItem.EAddressMode.BindingELabel) {
                                    s.SetELabelAddress();
                                    log.debug("#{} Address set", s.getParams().getAddress() + DataPacket.AddressELabelStart);
                                }
                                s.setAddressMode(DigitalSensorItem.EAddressMode.Done);
                                done = true;
                            } catch (TimeoutException ex) {
                                // ignore
                            } catch (IOException ex) {
                                // closed
                                log.debug("Port is closed");
                                return;
                            } catch (Exception ex) {
                                // log unexpected errors
                                log.warn("Error in startAddressProgramming", ex);
                            }
                            Thread.sleep(CommInterval);
                        }
                        if (isNotAddressPrograming()) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                log.warn("addressProgramingTask failed", ex);
            } finally {
                AddressPrograming = false;
                for (DigitalSensorItem s : Sensors) {
                    s.setAddressMode(DigitalSensorItem.EAddressMode.Not);
                }
            }
        });
    }

    public void startAPMPrograming(List<DigitalSensorItem> sensors) {
        if (isNotOpened() || isAddressPrograming()) {
            return;
        }
        AddressPrograming = true;

        log.info("Start APM addresses programing");

        createThreadPool().execute(() ->
        {
            try {
                synchronized (Driver.getLock()) {
                    DigitalSensorItem.setAllWorkMode(Driver, DataPacket.EWorkMode.APM);
                    Thread.sleep(getCommLongInterval());
                    DigitalSensorItem.setAllWorkMode(Driver, DataPacket.EWorkMode.APM);
                    Thread.sleep(getCommLongInterval());

                    CurrentSensor = sensors.get(0);
                    boolean notEnd = true;
                    while (notEnd) {
                        DigitalSensorItem s = CurrentSensor;
                        boolean sensorDone = false;
                        boolean labelDone = !s.getParams().hasELabel();

                        for (DigitalSensorItem a : sensors) {
                            if (a == s) {
                                if (sensorDone) {
                                    s.setAddressMode(DigitalSensorItem.EAddressMode.BindingELabel);
                                } else if (labelDone) {
                                    s.setAddressMode(DigitalSensorItem.EAddressMode.ProgramingSensor);
                                } else {
                                    s.setAddressMode(DigitalSensorItem.EAddressMode.ProgramingBoth);
                                }
                            } else {
                                s.setAddressMode(DigitalSensorItem.EAddressMode.Waiting);
                            }
                        }
                        while (isAddressPrograming() && !(sensorDone && labelDone)) {
                            try {
                                log.debug("#{} Wait DeviceSn Broadcast", s.getParams().getAddress());
                                int type = 0;
                                String sn = "";
                                Map<String, Object> map = new HashMap<>(16);
                                if (DigitalSensorItem.TryReadDeviceSnBroadcast(Driver, map, ReadTimeout)) {
                                    type = (int) map.getOrDefault("type", 0);
                                    sn = (String) map.getOrDefault("sn", "");
                                    Thread.sleep(CommInterval);
                                    s.SetAddressByDeviceSn(type, sn);
                                    switch (type) {
                                        case DataPacket.EDeviceType.DigitalSensor: {
                                            sensorDone = true;
                                            break;
                                        }
                                        case DataPacket.EDeviceType.ELabel: {
                                            labelDone = true;
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            } catch (TimeoutException ex) {
                                // ignore
                            } catch (IOException ex) {
                                // closed
                                log.debug("Port is closed");
                                return;
                            } catch (Exception ex) {
                                log.warn("Error in APM Programming", ex);
                            }
                            if (s != CurrentSensor) {
                                break;
                            }
                            if (sensorDone && labelDone) {
                                int next = sensors.indexOf(s) + 1;
                                if (next >= sensors.size()) {
                                    notEnd = false;
                                    break;
                                } else {
                                    CurrentSensor = sensors.get(next);
                                }
                            } else if (sensorDone) {
                                s.setAddressMode(DigitalSensorItem.EAddressMode.BindingELabel);
                            } else if (labelDone) {
                                s.setAddressMode(DigitalSensorItem.EAddressMode.ProgramingSensor);
                            }
                        }
                        if (isNotAddressPrograming()) {
                            break;
                        }
                    }
                    DigitalSensorItem.setAllWorkMode(Driver, DataPacket.EWorkMode.Normal);
                    Thread.sleep(getCommLongInterval());
                    DigitalSensorItem.setAllWorkMode(Driver, DataPacket.EWorkMode.Normal);
                }
            } catch (Exception ex) {
                log.warn("addressProgramingTask failed", ex);
            } finally {
                AddressPrograming = false;
                for (DigitalSensorItem s : Sensors) {
                    s.setAddressMode(DigitalSensorItem.EAddressMode.Not);
                }
            }
        });
    }

    public void stopAddressPrograming() {
        if (isNotAddressPrograming()) {
            return;
        }
        AddressPrograming = false;
        log.debug("Stop programing addresses");
        ThreadUtils.trySleep(1000);
    }

    private List<DigitalSensorParams> ScanResult;

    public void startScan(DigitalSensorScanListener listener) {
        startScan(DataPacket.AddressMin, DataPacket.AddressELabelStart - 1, listener);
    }

    public void startScanXSensors(DigitalSensorScanListener listener) {
        startScan(DataPacket.AddressXSensorStart, DataPacket.AddressXSensorEnd, listener);
    }

    public void startScan(int startAddress, int endAddress, DigitalSensorScanListener listener) {
        if (isNotOpened() || isAddressPrograming()) {
            return;
        }
        AddressPrograming = true;

        log.info("Start scan");
        if (listener != null) {
            listener.onScanStart(this, startAddress, endAddress);
        }

        ScanResult = new ArrayList<>();
        createThreadPool().execute(() ->
        {
            try {
                DigitalSensorItem sensor = DigitalSensorItem.NewDefaultSensor(Driver, this);
                synchronized (Driver.getLock()) {
                    for (int addr = startAddress; addr <= endAddress; addr++) {
                        if (isNotAddressPrograming()) {
                            throw new Exception("User aborted");
                        }
                        DigitalSensorParams params = sensor.getParams();
                        params.setAddress(addr);
                        params.setDeviceSn(null);
                        log.debug("#{} Try scan this device", addr);
                        if (listener != null) {
                            listener.onStartTest(sensor);
                        }
                        String sn = sensor.GetDeviceSn(1);
                        if (TextUtils.isTrimedEmpty(sn)) {
                            // this address not exists
                            log.debug("#{} This device not exists", addr);
                            if (listener != null) {
                                listener.onNotFound(sensor);
                            }
                        } else {
                            // exists
                            log.info("#{} Found this device: sn={}", addr, sn);
                            DigitalSensorParams newp = new DigitalSensorParams();
                            newp.setAddress(addr);
                            newp.setDeviceSn(sn);
                            newp.setELabelDeviceSn(sn);
                            if (addr >= DataPacket.AddressXSensorStart) {
                                log.debug("#{} Set as XSensor", addr);
                                newp.setDeviceType(DigitalSensorParams.EDeviceType.TempHumi);
                            }
                            ScanResult.add(newp);

                            if (addr < DataPacket.AddressELabelStart) {
                                // check ELabel
                                params.setAddress(addr + DataPacket.AddressELabelStart);
                                params.setDeviceSn(null);
                                log.debug("#{} Try scan elabel on this device", addr);
                                Thread.sleep(getCommLongInterval());
                                sn = sensor.GetDeviceSn(1);
                                if (TextUtils.isTrimedEmpty(sn)) {
                                    // this device has no elabel
                                    log.debug("#{} This device has no elabel", addr);
                                    newp.setELabelModel(DigitalSensorParams.EELabelModel.None);
                                } else {
                                    // exists
                                    log.info("#{} Found elabel on this device: sn={}", addr, sn);
                                    newp.setELabelModel(DigitalSensorParams.EELabelModel.V3);
                                    newp.setELabelDeviceSn(sn);
                                }
                            }
                            if (listener != null) {
                                listener.onFound(sensor);
                            }
                        }
                        Thread.sleep(getCommInterval());
                    }
                    if (listener != null) {
                        listener.onScanEnd(this);
                    }
                }
            } catch (Exception ex) {
                log.warn("Scan failed", ex);
                if (listener != null) {
                    listener.onScanFailed(this, ex.getMessage());
                }
            } finally {
                AddressPrograming = false;
                for (DigitalSensorItem s : Sensors) {
                    s.setAddressMode(DigitalSensorItem.EAddressMode.Not);
                }
            }
        });
    }

    public void stopScan() {
        stopAddressPrograming();
    }

    public void ClearAllAddresses() {
        if (isNotOpened()) {
            return;
        }
        DigitalSensorItem.clearAllAddress(Driver);
    }

    public void DoAllZero(boolean save) {
        if (isNotOpened()) {
            return;
        }
        DigitalSensorItem.doAllZero(Driver, save);
    }

    public void SetAllCreepCorrect(double value) {
        if (isNotOpened()) {
            return;
        }
        DigitalSensorItem.SetAllCreepCorrect(Driver, value);
    }

    public void SetAllZeroCapture(double value) {
        if (isNotOpened()) {
            return;
        }
        DigitalSensorItem.SetAllZeroCapture(Driver, value);
    }

    public void SetAddressByDeviceSn(int address, String sn) throws Exception {
        log.info("#{} SetAddressByDeviceSn: address={}, sn={}", DataPacket.AddressConditionalBroadcast, address, sn);
        DataPacket packet = DataPacket.BuildSetAddressByDeviceSn(address, sn);
        synchronized (Driver.getLock()) {
            Driver.WriteRead(packet, getReadTimeout() + 1000);
        }
        log.info("#{} SetAddressByDeviceSn: {} -> {}", DataPacket.AddressConditionalBroadcast, sn, address);
    }
}
