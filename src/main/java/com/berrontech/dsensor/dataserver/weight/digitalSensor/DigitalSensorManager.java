package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("singleton")
@Slf4j
@Data
public class DigitalSensorManager {
    public static final String APP_NAME = "DigitalSensorManager";
    public static final String NAME = "DSmanager";
    private boolean AutoRestoreZeroOffset = true;
    private boolean PowerUpZero = false;
    private boolean SaveLogToFile = false;
    private List<DigitalSensorGroup> Groups = new ArrayList<>();
    private DigitalSensorGroup CurrentGroup;
    private DigitalSensorItem CurrentSensor;
    private boolean Opened = false;
    private boolean Reading = false;
    private DigitalSensorListener sensorListener;

    public boolean isNotReading() {
        return !Reading;
    }

    public boolean canStartReading() {
        return Opened && isNotReading();
    }

    public boolean isNotOpened() {
        return !Opened;
    }

    public DigitalSensorItem getFirstSensorOrDefault() {
        if (Groups.size() > 0) {
            if (Groups.get(0) != null) {
                DigitalSensorGroup group = Groups.get(0);
                if (group.getSensors() != null && group.getSensors().size() > 0) {
                    return group.getSensors().get(0);
                }
            }
        }
        return new DigitalSensorItem();
    }
//
//    public static DigitalSensorManager loadSettings(Context context) {
//        return UserSettingsHelper.getParam(context, APP_NAME, NAME, DigitalSensorManager.class, getDefaultSetting());
//    }

    private static DigitalSensorManager getDefaultSetting() {
        DigitalSensorManager manager = new DigitalSensorManager();
        manager.setGroups(new ArrayList<DigitalSensorGroup>());
        manager.setCurrentGroup(new DigitalSensorGroup());
        manager.setCurrentSensor(new DigitalSensorItem());
        return manager;
    }
//
//    public void saveSetting(Context context, DigitalSensorManager manager) {
//        UserSettingsHelper.setParam(context, APP_NAME, NAME, manager);
//        ToastUtils.showToast(context, context.getString(R.string.saveSuccess), false);
//    }

    public void open() {
        if (Opened) {
            return;
        }
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.Open();
                } catch (Exception ex) {
                    log.warn("Open", ex);
                }
            }
        }
        Opened = true;
    }

    public void close() {
        if (!Opened) {
            return;
        }
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.Close();
                } catch (Exception ex) {
                    log.warn("Close", ex);
                }
            }
        }
        Opened = false;
    }

    public void Init() {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.Init(this);
                } catch (Exception ex) {
                    log.warn("Init", ex);
                }
            }
        }
    }

    public void BuildSubGroups() {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.BuildSubGroups();
                } catch (Exception ex) {
                    log.warn("BuildSubGroups", ex);
                }
            }
        }
    }

    public void startReading() {
        if (Reading) {
            return;
        }
        Reading = true;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                log.debug("Start reading [{}]", g.getName());
                try {
                    g.startReading2();
                } catch (Exception ex) {
                    log.warn("StartReading", ex);
                }
            }
        }
    }

    public void StopReading() {
        if (!Reading) {
            return;
        }
        Reading = false;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.stopReading();
                } catch (Exception ex) {
                    log.warn("StopReading", ex);
                }
            }
        }
    }

    public void shutdown() {
        StopReading();
        close();
    }

    public DigitalSensorGroup NewGroup() {
        int newid = Groups.stream().max(Comparator.comparingInt(a -> a.getId())).orElse(new DigitalSensorGroup()).getId() + 1;
        DigitalSensorGroup group = DigitalSensorGroup.NewGroup(newid, this);
        Groups.add(group);
        return group;
    }

    public DigitalSensorGroup BuildSingleDefaultGroup() {
        List<DigitalSensorGroup> groups = new ArrayList<>();
        DigitalSensorGroup group = DigitalSensorGroup.NewSingleDefaultGroup(this);
        groups.add(group);
        Groups = groups;
        return group;
    }

    public void DeleteGroup(DigitalSensorGroup group) {
        if (Groups.size() > 0) {
            List<DigitalSensorGroup> groups = Groups;
            groups.remove(group);
            Groups = groups;
        }
    }

    public void DoAllZero(boolean save) {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.DoAllZero(save);
                } catch (Exception ex) {
                    log.warn("{} DoAllZero failed: {}", g.Driver.toString(), ex.getMessage());
                }
            }
        }
    }

    public void SetAllCreepCorrect(double value) {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.SetAllCreepCorrect(value);
                } catch (Exception ex) {
                    log.warn("{} SetAllCreepCorrect failed: {}", g.Driver.toString(), ex.getMessage());
                }
            }
        }
    }

    public void SetAllZeroCapture(double value) {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.SetAllZeroCapture(value);
                } catch (Exception ex) {
                    log.warn("{} SetAllZeroCapture failed: {}", g.Driver.toString(), ex.getMessage());
                }
            }
        }
    }

    public DigitalSensorItem FirstOrNull(String slotNo) {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                for (DigitalSensorItem s : g.getSensors()) {
                    if (Objects.equals(s.getSubGroup(), slotNo)) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    public List<DigitalSensorItem> Filter(String slotNo) {
        List<DigitalSensorItem> sensors = new ArrayList<>();
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                for (DigitalSensorItem s : g.getSensors()) {
                    if (Objects.equals(s.getSubGroup(), slotNo)) {
                        sensors.add(s);
                    }
                }
            }
        }
        return sensors;
    }

    public DigitalSensorItem FirstOrNull(int connectionId, int address) {
        if (Groups != null && Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                if (g.getConnectionId() == connectionId) {
                    for (DigitalSensorItem s : g.getSensors()) {
                        if (s.getParams().getAddress() == address) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void ClearAllCounters()
    {
        if (Groups != null && Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                    for (DigitalSensorItem s : g.getSensors()) {
                        s.setTotalSuccess(0);
                        s.setTotalErrors(0);
                }
            }
        }
    }

    public boolean HighlightMaterial(String barcode) {
        boolean found = false;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                for (DigitalSensorItem s : g.getSensors()) {
                    if (s.getPassenger() != null) {
                        if (s.getPassenger().getMaterial() != null) {
                            try {
                                if (barcode.equalsIgnoreCase(s.getPassenger().getMaterial().getNumber())) {
                                    found = true;
                                    s.DoHighlight(10 * 1000);
                                }
                            } catch (Exception ex) {
                                log.warn("#{} HighlightMaterial({}) failed: {}", s.getParams().getAddress(), barcode, ex.getMessage());
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    public boolean HighlightSlot(String slotNo, long duration) {
        boolean found = false;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                for (DigitalSensorItem s : g.getSensors()) {
                    synchronized (s.getDriver().getLock()) {
                        try {
                            if (Objects.equals(s.getSubGroup(), slotNo)) {
                                found = true;
                                s.DoHighlight(duration);
                            }
                        } catch (Exception ex) {
                            log.warn("#{} HighlightSlot({}) failed: {}", s.getParams().getAddress(), slotNo, ex.getMessage());
                        }
                    }
                }
            }
        }
        return found;
    }

    public boolean HighlightSlots(Collection<String> slots, long duration) {
        boolean found = false;
        if (Groups.size() > 0 && slots != null) {
            for (DigitalSensorGroup g : Groups) {
                synchronized (g.getDriver().getLock()) {
                    for (DigitalSensorItem s : g.getSensors()) {
                        try {
                            if (slots.contains(s.getSubGroup())) {
                                found = true;
                                s.DoHighlight(duration);
                            }
                        } catch (Exception ex) {
                            log.warn("#{} HighlightSlots failed: {}", s.getParams().getAddress(), ex.getMessage());
                        }
                    }
                }
            }
        }
        return found;
    }

    public void DeHighlightAll() {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                synchronized (g.getDriver().getLock()) {
                    for (DigitalSensorItem s : g.getSensors()) {
                        try {
                            s.DeHighlight();
                        } catch (Exception ex) {
                            log.warn("#{} DeHighlightAll failed: {}", s.getParams().getAddress(), ex.getMessage());
                        }
                    }
                }
            }
        }
    }


    public boolean EnableSlot(Collection<String> slotNo, boolean enable) {
        boolean found = false;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                for (DigitalSensorItem s : g.getSensors()) {
                    synchronized (s.getDriver().getLock()) {
                        try {
                            if (Objects.equals(s.getSubGroup(), slotNo)) {
                                found = true;
                                s.SetEnabled(true);
                            } else {
                                s.SetEnabled(false);
                            }
                        } catch (Exception ex) {
                            log.warn("#{} EnableSlot({}):{} failed: {}", s.getParams().getAddress(), slotNo, enable, ex.getMessage());
                        }
                    }
                }
            }
        }
        return found;
    }

}
