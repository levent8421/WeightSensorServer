package com.berrontech.dsensor.dataserver.weight.utils.helper;

import java.math.BigDecimal;


public class WeightHelper {
    public static BigDecimal toUnit(BigDecimal weight, String unit, String targetUnit) {
        double w = toUnit(weight.doubleValue(), unit, targetUnit);
        return DecimalHelper.Parse(w);
    }


    public static double toGram(double weight, String srcUnit) {
        double val = weight;
        switch (srcUnit) {
            case "kg": {
                val *= 1000;
                break;
            }
            case "t": {
                val *= 1000 * 1000;
                break;
            }
            case "jin": {
                val *= 1000 * 0.5;
                break;
            }
            case "oz": {
                val *= 28.349523125;
                break;
            }
            case "lb": {
                val *= 16 * 28.349523125;
                break;
            }
            default: {
                break;
            }
        }

        return val;
    }

    public static double toKiloGram(double weight, String srcUnit) {
        double val = weight;
        switch (srcUnit) {
            case "g": {
                val *= 0.001;
                break;
            }
            default:
                val = toKiloGram(toGram(weight, srcUnit), "g");
                break;
        }
        return val;
    }

    public static double toJin(double weight, String srcUnit) {
        double val = weight;
        switch (srcUnit) {
            case "g": {
                val *= 2 * 0.001;
                break;
            }
            default:
                val = toJin(toGram(weight, srcUnit), "g");
                break;
        }
        return val;
    }

    public static double toTon(double weight, String srcUnit) {
        double val = weight;
        switch (srcUnit) {
            case "g": {
                val *= 0.000001;
                break;
            }
            default:
                val = toTon(toGram(weight, srcUnit), "g");
                break;
        }
        return val;
    }

    public static double toOunce(double weight, String srcUnit) {
        double val = weight;
        switch (srcUnit) {
            case "g": {
                val *= 0.0352739619496;
                break;
            }
            case "lb": {
                val *= 16;
                break;
            }
            default:
                val = toOunce(toGram(weight, srcUnit), "g");
                break;
        }
        return val;
    }

    public static double toPound(double weight, String srcUnit) {
        double val = weight;
        switch (srcUnit) {
            case "oz": {
                val *= 0.0625;
                break;
            }
            default:
                val = toPound(toOunce(weight, srcUnit), "oz");
                break;
        }
        return val;
    }

    public static double toUnit(double weight, String srcUnit, String dstUnit) {
        switch (dstUnit) {
            case "g":
                return toGram(weight, srcUnit);
            case "kg":
                return toKiloGram(weight, srcUnit);
            case "t":
                return toTon(weight, srcUnit);
            case "jin":
                return toJin(weight, srcUnit);
            case "lb":
                return toPound(weight, srcUnit);
            case "oz":
                return toOunce(weight, srcUnit);
            default:
                return weight;
        }

    }
}
