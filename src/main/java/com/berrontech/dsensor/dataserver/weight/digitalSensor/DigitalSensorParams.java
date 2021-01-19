package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.weight.utils.KeyValueList;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Data
public class DigitalSensorParams {
    private int Address;
    private int Point1RawCount = 1000000;
    private double Point1Weight = 0;
    private int Point2RawCount = 1100000;
    private double Point2Weight = 10;
    private BigDecimal Increment = new BigDecimal("0.01");
    private BigDecimal Capacity = BigDecimal.valueOf(10E10);
    private String FactoryUnit = "kg";
    private String DisplayUnit = null;
    private boolean AutoDisplayUnit = true;
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
    private double[] XSensorLowers = {0, 0};
    private double[] XSensorUppers = {60, 100};
    private int Id;
    private String backupSensorSn;
    private String backupELabelSn;
    private boolean NegativeMode = false;

    public int ELabelModel = EELabelModel.None;
    private boolean Enabled = true;


    public String getSafeDisplayUnit() {
        if (AutoDisplayUnit && TextUtils.isTrimedEmpty(DisplayUnit)) {
            if (Capacity != null && Capacity.compareTo(BigDecimal.ONE) < 0) {
                return "g";
            } else if (Increment != null && Increment.compareTo(BigDecimal.TEN) >= 0) {
                return "t";
            } else {
                return "kg";
            }
        }
        return DisplayUnit == null ? FactoryUnit : DisplayUnit;
    }

    public static int toELabelAddress(int address) {
        if (address < DataPacket.AddressELabelStart) {
            return address + DataPacket.AddressELabelStart;
        }
        return address;
    }

    public int getELabelAddress() {
        return toELabelAddress(Address);
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
        int TempHumi = 2;
    }

    public static boolean IsValidSn(String sn, String partNo) {
        if (sn != null && sn.startsWith(partNo)) {
            return IsSnInRule(sn);
        }
        return false;
    }

    static final Pattern SnMatcher = Pattern.compile("^\\d{8}[0-9A-Z]{4}[0-9A-Z]{4}$");

    public static boolean IsSnInRule(String sn) {
        Matcher m = SnMatcher.matcher(sn);
        return m.matches();
    }

    public static String BuildNewSensorDeviceSn() {
        return BuildNewSn("80000077");
    }

    public static String BuildNewELabelDeviceSn() {
        return BuildNewSn("80000078");
    }

    private static char IntToCh(int val) {
        if (val >= 0) {
            if (val < 10) {
                return (char) ('0' + val);
            } else if (val < 10 + 26) {
                return (char) ('A' + val - 10);
            } else if (val < 10 + 26 + 26) {
                return (char) ('a' + val - 10 - 26);
            }
            return '?';
        }
        return '?';
    }

    private static String IntToCh(int val, int len) {
        StringBuilder sb = new StringBuilder();
        int max = 10 + 16;
        while (len-- > 0) {
            sb.insert(0, IntToCh(val % max));
            val /= max;
        }
        return sb.toString();
    }


    public static String BuildNewSn(String partNo) {
        String bom = partNo;
        Calendar dt = Calendar.getInstance(TimeZone.getDefault());
        int year = dt.get(Calendar.YEAR) - 2017;
        int month = dt.get(Calendar.MONTH);
        int day = dt.get(Calendar.DAY_OF_MONTH);
        int hour = dt.get(Calendar.HOUR_OF_DAY);
        int min = dt.get(Calendar.MINUTE);
        int sec = dt.get(Calendar.SECOND);
        int ms = dt.get(Calendar.MILLISECOND);
        int tms = hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000 + ms;
        tms /= 200;

        String sn = String.format("%s%c%c%02d%s",
                bom,
                IntToCh(year), IntToCh(month), day,
                IntToCh(tms, 4));
        return sn;
    }

}
