package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;

@Data
public class DigitalSensorValues {
    private static Object lock = new Object();
    private int RawCount;
    private int DynamicRawCount;
    private int Ramp;

    private BigDecimal GrossWeight = BigDecimal.ZERO;

    public String getGrossWeightStr() {
        return GrossWeight.toString();
    }

    public void setGrossWeightStr(String value) {
        try {
            GrossWeight = new BigDecimal(value);
        } catch (Exception ex) {
            GrossWeight = BigDecimal.ZERO;
        }
    }

    private String Unit = "kg";

    private float HighGross;

    public void setHighGross(float value) {
        HighGross = value;
        CalcCounting();
    }

    private float ZeroOffset;

    private float HighTare;

    public void setHighTare(float value) {
        HighTare = value;
        CalcCounting();
    }

    public float getHighNet() {
        return HighGross - HighTare;
    }

    public BigDecimal getTareWeight() {
        int dec = CalcDecimalPoints(GrossWeight);
        BigDecimal round = BuildRoundValue(1, dec);
        return Round(HighTare, round);
    }

    public void setTareWeight(BigDecimal value) {
        setHighTare(value.floatValue());
    }


    public static int CalcDecimalPoints(BigDecimal value) {
        String sv = String.valueOf(value);
        int pos = sv.indexOf('.');
        if (pos >= 0) {
            return sv.length() - pos - 1;
        }
        return 0;
    }


    public static BigDecimal BuildRoundValue(int step, int points) {
        BigDecimal d = BigDecimal.valueOf(step);
        BigDecimal rate10 = BigDecimal.valueOf(10);
        for (int n = 0; n < points; n++) {
            d.divide(rate10);
        }
        return d;
    }

    public static BigDecimal Round(double src, BigDecimal round) {
        long intPart = Math.round(src / round.doubleValue());
        BigDecimal rst = BigDecimal.valueOf(intPart);
        rst.divide(round);
        return rst;
    }

    public boolean isTared() {
        return HighTare != 0;
    }

    public BigDecimal getNetWeight() {
        return getGrossWeight().subtract(getTareWeight());
    }


    public static final byte StableMark = 'S';
    public static final byte DynamicMark = 'D';


    public enum EStatus {
        Unknow,
        Stable,
        Dynamic,
        UnderLoad,
        OverLoad,
    }

    private EStatus Status = EStatus.Unknow;

    public boolean isStable() {
        return Status == EStatus.Stable;
    }

    public void setStable() {
        Status = EStatus.Stable;
    }

    public boolean isDynamic() {
        return Status == EStatus.Dynamic;
    }

    public void setDynamic() {
        Status = EStatus.Dynamic;
    }

    public boolean isInRange() {
        return (isDynamic() || isStable());
    }

    public boolean isNotInRange() {
        return !isInRange();
    }

    public boolean isOverLoad() {
        return Status == EStatus.OverLoad;
    }

    public void setOverLoad() {
        Status = EStatus.OverLoad;
    }

    public boolean isUnderLoad() {
        return Status == EStatus.UnderLoad;
    }

    public void setUnderLoad() {
        Status = EStatus.UnderLoad;
    }


    public static boolean isStableMark(byte mark) {
        if (mark == StableMark) {
            return true;
        }
        return false;
    }

    public void CheckStatus(byte stableMark, BigDecimal capacity, BigDecimal increment) {
        if (HighGross > capacity.floatValue()) {
            setOverLoad();
        } else if (HighGross < increment.floatValue() * (-10)) {
            setUnderLoad();
        } else if (isStableMark(stableMark)) {
            setStable();
        } else {
            setDynamic();
        }
    }

    private double APW = 0;
    private int ReferenceN = 5;

    public boolean isPieceCounting() {
        return APW > 0;
    }

    public boolean isNotPieceCounting() {
        return !isPieceCounting();
    }

    private int PieceCount = 0;
    private double PieceCountAccuracy;

    private void CalcCounting() {
        double apw = APW;
        if (apw > 0) {
            synchronized (lock) {
                PieceCount = (int) Math.round(HighGross / APW);
                PieceCountAccuracy = 1 - Math.abs((getHighNet() - apw * PieceCount) / apw);
            }
        }
    }

    private boolean Highlight;

    public boolean isNotHighlight() {
        return !Highlight;
    }

//
//    public static Double toGram(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_kg: {
//                val *= 1000;
//                break;
//            }
//            case WeightConstInfo.Unit_t: {
//                val *= 1000 * 1000;
//                break;
//            }
//            case WeightConstInfo.Unit_jin: {
//                val *= 1000 * 0.5;
//                break;
//            }
//            case WeightConstInfo.Unit_oz: {
//                val *= 28.349523125;
//                break;
//            }
//            case WeightConstInfo.Unit_lb: {
//                val *= 16 * 28.349523125;
//                break;
//            }
//        }
//        return val;
//    }
//
//    public static Double toKiloGram(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_g: {
//                val *= 0.001;
//                break;
//            }
//            default:
//                val = toKiloGram(toGram(src, srcUnit), WeightConstInfo.Unit_g);
//                break;
//        }
//        return val;
//    }
//
//    public static Double toJin(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_g: {
//                val *= 2 * 0.001;
//                break;
//            }
//            default:
//                val = toJin(toGram(src, srcUnit), WeightConstInfo.Unit_g);
//                break;
//        }
//        return val;
//    }
//
//    public static Double toTon(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_g: {
//                val *= 0.000001;
//                break;
//            }
//            default:
//                val = toTon(toGram(src, srcUnit), WeightConstInfo.Unit_g);
//                break;
//        }
//        return val;
//    }
//
//    public static Double toOunce(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_g: {
//                val *= 0.0352739619496;
//                break;
//            }
//            case WeightConstInfo.Unit_lb: {
//                val *= 16;
//                break;
//            }
//            default:
//                val = toOunce(toGram(src, srcUnit), WeightConstInfo.Unit_g);
//                break;
//        }
//        return val;
//    }
//
//    public static Double toPound(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_oz: {
//                val *= 0.0625;
//                break;
//            }
//            default:
//                val = toPound(toOunce(src, srcUnit), WeightConstInfo.Unit_oz);
//                break;
//        }
//        return val;
//    }
//
//    public double toUnit(double src, int srcUnit) {
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_g:
//                return toGram(src, WeightConstInfo.Unit_kg);
//            case WeightConstInfo.Unit_kg:
//                return toKiloGram(src, WeightConstInfo.Unit_kg);
//            case WeightConstInfo.Unit_t:
//                return toTon(src, WeightConstInfo.Unit_kg);
//            case WeightConstInfo.Unit_jin:
//                return toJin(src, WeightConstInfo.Unit_kg);
//            case WeightConstInfo.Unit_lb:
//                return toPound(src, WeightConstInfo.Unit_kg);
//            case WeightConstInfo.Unit_oz:
//                return toOunce(src, WeightConstInfo.Unit_kg);
//            default:
//                return src;
//        }
//
//    }
//
//    public static Double fromGram(double src, int srcUnit) {
//        Double val = src;
//        switch (srcUnit) {
//            case WeightConstInfo.Unit_kg: {
//                val /= 1000;
//                break;
//            }
//            case WeightConstInfo.Unit_t: {
//                val /= 1000 * 1000;
//                break;
//            }
//            case WeightConstInfo.Unit_oz: {
//                val /= 28.349523125;
//                break;
//            }
//            case WeightConstInfo.Unit_lb: {
//                val /= 16 * 28.349523125;
//                break;
//            }
//        }
//        return val;
//    }
}
