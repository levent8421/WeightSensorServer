package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import java.util.List;

import com.berrontech.dsensor.dataserver.weight.utils.KeyValueList;
import lombok.Data;

@Data
public class DigitalSensorPassenger {
    public String MaterialUUID;
    public MaterialInfo Material = new MaterialInfo();
    public MaterialManager MaterialMgr = new MaterialManager();

    public List<MaterialInfo> getMaterials() {
        return MaterialMgr.getMaterials();
    }

    public DigitalSensorPassenger setMaterialUUID(String materialUUID) {
        MaterialUUID = materialUUID;
        PickupMaterial();
        return this;
    }

    public interface EContainer {
        int Unknow = 0;

        int Normal_S = 1;
        int Normal_M = 2;
        int Normal_L = 3;

        int Cold_A = 4;
        int Cold_B = 5;
    }

    public KeyValueList<Integer, String> Containers = new KeyValueList<>(
            new Integer[]{EContainer.Unknow, EContainer.Normal_S, EContainer.Normal_M, EContainer.Normal_L, EContainer.Cold_A, EContainer.Cold_B},
            new String[]{"未知", "常温-小", "常温-中", "常温-大", "冰箱-A", "冰箱-B"});
    public int Container = EContainer.Unknow;
    public String ContainerName;

    public String getContainerName() {
        return Containers.toValue(Container);
    }

    public void PickupMaterial() {
        if (getMaterials() != null) {
            for (MaterialInfo m : getMaterials()) {
                if (m.UUID.equalsIgnoreCase(MaterialUUID)) {
                    Material = m;
                    return;
                }
            }
        }
    }
}
