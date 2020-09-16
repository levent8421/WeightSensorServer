package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import com.berrontech.dsensor.dataserver.weight.utils.KeyValueList;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class DigitalSensorParams {
    private int Address;
    private int Point1RawCount = 1000000;
    private double Point1Weight = 0;
    private int Point2RawCount = 1100000;
    private double Point2Weight = 10;
    private BigDecimal Increment = new BigDecimal("0.01");
    private BigDecimal Capacity = BigDecimal.valueOf(10E10);
    private double GeoFactor = 1;
    private double StableSpeed = 0.5;
    private double ZeroCapture = 0.5;
    private double CreepCorrect = 0;
    private double StableRange = 0.5;
    private String FirmwareVersion;
    private String PCBASn;
    private String DeviceSn;
    private String DeviceModel;
    private String ELabelFirmwareVersion;
    private String ELabelPCBASn;
    private String ELabelDeviceSn;
    private String ELabelDeviceModel;
    private int DeviceType;
    private int Id;

    public int ELabelModel = EELabelModel.None;
    private boolean Enabled = true;

    public int getELabelAddress() {
        return Address + DataPacket.AddressELabelStart;
    }

    public boolean isXSensor() {
        return Address >= DataPacket.AddressXSensorStart;
        //return DeviceType == EDeviceType.Accelerator || DeviceType == EDeviceType.TempHumi;
    }

    public final static KeyValueList<Double, String> Increments = new KeyValueList<>(
            new Double[]{0.0001, 0.0002, 0.0005,
                    0.001, 0.002, 0.005,
                    0.01, 0.02, 0.05,
                    0.1, 0.2, 0.5,
                    1.0, 2.0, 5.0},
            new String[]{"0.0001", "0.0002", "0.0005", "0.001", "0.002", "0.005",
                    "0.01", "0.02", "0.05",
                    "0.1", "0.2", "0.5",
                    "1", "2", "5"});
    public final static KeyValueList<Double, String> ZeroCaptures = new KeyValueList<>(
            new Double[]{0.0, 0.5, 1.0, 2.0, 5.0, 10.0},
            new String[]{"禁用", "0.5d", "1d", "2d", "5d", "10d"});
    public final static KeyValueList<Double, String> CreepCorrects = new KeyValueList<>(
            new Double[]{0.0, 0.5, 1.0, 2.0, 5.0, 10.0},
            new String[]{"禁用", "0.5d", "1d", "2d", "5d", "10d"});
    public final static KeyValueList<Double, String> StableRanges = new KeyValueList<>(
            new Double[]{0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 999999.0},
            new String[]{"0.5d", "1d", "2d", "5d", "10d", "20d", "禁用"});

    public interface EELabelModel {
        int None = 0;
        int V1 = 1;
        int V2 = 2;
        int V3 = 3;
        int V4 = 4;
    }

    public KeyValueList<Integer, String> ELabelModels = new KeyValueList<>(
            new Integer[]{EELabelModel.None, EELabelModel.V1, EELabelModel.V2},
            new String[]{"无", "电子标签V1.0", "电子标签V2.0"});


    public boolean hasELabel() {
        return ELabelModel != EELabelModel.None;
    }

    public boolean isDisabled() {
        return !Enabled;
    }

    public Integer calcRamp(Integer rawCnt) {
        Integer dist = Point2RawCount - Point1RawCount;
        if (dist <= 0) {
            dist = 200000;
        }
        return (rawCnt - Point1RawCount) * 100 / dist;
    }

    public interface EDeviceType {
        int Unknow = 0;
        int WeightSensor = 1;
        int ELabel = 1;
        int Accelerator = 2;
        int TempHumi = 3;
    }

}
