package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MaterialManager {
    private static String TAG = MaterialManager.class.getName();
    public static final String APP_NAME = "MaterialManager";
    public static final String NAME = "Material";
    public List<MaterialInfo> Materials;
    public MaterialInfo CurrentMaterial;
    public int MaterialCount;

    public int getMaterialCount() {
        return Materials.size();
    }

//    public static MaterialManager loadSettings(Context context) {
//        return UserSettingsHelper.getParam(context, APP_NAME, NAME, MaterialManager.class, getDefaultSetting());
//    }

    private static MaterialManager getDefaultSetting() {
        MaterialManager manager = new MaterialManager();
        manager.CurrentMaterial = new MaterialInfo();
        manager.Materials = new ArrayList<>();
        return manager;
    }

//    public void saveSetting(Context context, MaterialManager manager) {
//        UserSettingsHelper.setParam(context, APP_NAME, NAME, manager);
//        ToastUtils.showToast(context, context.getString(R.string.saveSuccess), false);
//    }

    public MaterialInfo HighlightMaterial(String barcode) {
        for (MaterialInfo m : Materials) {
            if (m.Number.equalsIgnoreCase(barcode)) {
                CurrentMaterial = m;
            }
        }
        return CurrentMaterial;
    }
}
