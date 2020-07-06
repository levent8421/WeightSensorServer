package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DigitalSensorValues {
    private static final Object lock = new Object();
    private int RawCount;
    private int DynamicRawCount;
    private int Ramp;

    private BigDecimal GrossWeight = BigDecimal.ZERO;

    public String getGrossWeightStr() {
        return GrossWeight.toString();
    }

    public void setGrossWeightStr(String value) {
        try {
            GrossWeight = new BigDecimal(value.trim());
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
                PieceCountAccuracy = calcPieceCountAccuracy(PieceCount, apw, getHighNet());
            }
        }
    }

    /**
     * 计算计件精度
     * 计检精度 = 1 - { abs[ 实重 -（件数 * 单重)] / 单重 }
     *
     * @param count  件数
     * @param apw    单重
     * @param weight 实重
     * @return 几件精度
     */
    private double calcPieceCountAccuracy(int count, double apw, double weight) {
        final double expectWeight = apw * count;
        final double tolerance = Math.abs(expectWeight - weight);
        return 1 - (tolerance / apw);
    }

    private boolean Highlight;

    public boolean isNotHighlight() {
        return !Highlight;
    }
}
