package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class DigitalSensorManager {
    private static String TAG = DigitalSensorManager.class.getName();
    public static final String APP_NAME = "DigitalSensorManager";
    public static final String NAME = "DSmanager";
    public boolean AutoRestoreZeroOffset = true;
    public boolean PowerUpZero = false;
    public boolean SaveLogToFile = false;
    public List<DigitalSensorGroup> Groups;
    public DigitalSensorGroup CurrentGroup;
    public DigitalSensorItem CurrentSensor;
    public DigitalSensorItem FirstSensorOrDefault;
    public boolean IsOpened = false;
    public boolean IsNotOpened = true;
    public boolean IsReading = false;
    public boolean IsNotReading = true;
    public boolean CanStartReading = false;

    public boolean isPowerUpZero() {
        return PowerUpZero;
    }

    public DigitalSensorManager setPowerUpZero(boolean powerUpZero) {
        PowerUpZero = powerUpZero;
        return this;
    }

    public boolean isSaveLogToFile() {
        return SaveLogToFile;
    }

    public DigitalSensorManager setSaveLogToFile(boolean saveLogToFile) {
        SaveLogToFile = saveLogToFile;
        return this;
    }

    public List<DigitalSensorGroup> getGroups() {
        return Groups;
    }

    public DigitalSensorManager setGroups(List<DigitalSensorGroup> groups) {
        Groups = groups;
        return this;
    }

    public DigitalSensorGroup getCurrentGroup() {
        return CurrentGroup;
    }

    public DigitalSensorManager setCurrentGroup(DigitalSensorGroup currentGroup) {
        CurrentGroup = currentGroup;
        return this;
    }

    public DigitalSensorItem getCurrentSensor() {
        return CurrentSensor;
    }

    public DigitalSensorManager setCurrentSensor(DigitalSensorItem currentSensor) {
        CurrentSensor = currentSensor;
        return this;
    }

    public DigitalSensorManager setFirstSensorOrDefault(DigitalSensorItem firstSensorOrDefault) {
        FirstSensorOrDefault = firstSensorOrDefault;
        return this;
    }

    public boolean isNotReading() {
        return !IsReading;
    }

    public boolean isCanStartReading() {
        return IsOpened && IsNotReading;
    }

    public boolean isNotOpened() {
        return !IsOpened;
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

    public void Open() {
        if (IsOpened) {
            return;
        }
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.Open();
                } catch (Exception ex) {
                    log.error(TAG, "Open", ex);
                }
            }
        }
        IsOpened = true;
    }

    public void Close() {
        if (!IsOpened) {
            return;
        }
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.Close();
                } catch (Exception ex) {
                    log.error(TAG, "Close", ex);
                }
            }
        }
        IsOpened = false;
    }

    public void Init() {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.Init(this);
                } catch (Exception ex) {
                    log.error(TAG, "Init", ex);
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
                    log.error(TAG, "BuildSubGroups", ex);
                }
            }
        }
    }

    public void StartReading() {
        if (IsReading) {
            return;
        }
        IsReading = true;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.startReading2();
                } catch (Exception ex) {
                    log.error(TAG, "StartReading", ex);
                }
            }
        }
    }

    public void StopReading() {
        if (!IsReading) {
            return;
        }
        IsReading = false;
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                try {
                    g.stopReading();
                } catch (Exception ex) {
                    log.error(TAG, "StopReading", ex);
                }
            }
        }
    }

    public DigitalSensorGroup NewGroup() {
        int newid = 1;
        List<DigitalSensorGroup> groups = Groups;
        if (Groups.size() > 0) {
            Collections.sort(Groups, (u1, u2) -> {
                int diff = u1.getId() - u2.getId();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0;
            });
            newid = Groups.get(Groups.size() - 1).getId() + 1;
        }
        DigitalSensorGroup group = DigitalSensorGroup.NewGroup(newid);
        groups.add(group);
        Groups = groups;
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
                g.DoAllZero(save);
            }
        }
    }

    public void SetAllCreepCorrect(double value) {
        if (Groups.size() > 0) {
            for (DigitalSensorGroup g : Groups) {
                g.SetAllCreepCorrect(value);
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
                            if (barcode.equalsIgnoreCase(s.getPassenger().getMaterial().getNumber())) {
                                found = true;
                                s.getValues().setHighlight(true);
                            } else {
                                s.getValues().setHighlight(false);
                            }
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
                for (DigitalSensorItem s : g.getSensors()) {
                    s.getValues().setHighlight(false);
                }
            }
        }
    }
}
