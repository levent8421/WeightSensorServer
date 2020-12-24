package com.berrontech.dsensor.dataserver.weight.utils.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DecimalHelper {

    public static final String DecimalSeparatorDot = ".";
    public static final String DecimalSeparatorComma = ",";

    public static final String GetDefaultSeparator() {
        return DecimalSeparatorDot;
    }

    public static BigDecimal Parse(float value, String separator) {
        DecimalFormat format = new DecimalFormat("0" + separator + "############");
        String s = format.format(value);
        return new BigDecimal(s);
    }

    public static BigDecimal Parse(double value, String separator) {
        DecimalFormat format = new DecimalFormat("0" + separator + "############");
        String s = format.format(value);
        return new BigDecimal(s);
    }

    public static BigDecimal Parse(float value) {
        return Parse(value, GetDefaultSeparator());
    }

    public static BigDecimal Parse(double value) {
        return Parse(value, GetDefaultSeparator());
    }
}
