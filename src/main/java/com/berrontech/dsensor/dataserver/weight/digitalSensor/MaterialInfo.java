package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;

@Data
public class MaterialInfo {
    public String UUID = java.util.UUID.randomUUID().toString();
    public String Number = "";
    public String ComplexBarcode = "";
    public String Name = "";
    public String Desc = "";
    public double APW;
    public double Tolerance = 0.2;
    public double TolerancePercent;

    public double getTolerancePercent() {
        return Tolerance * 100;
    }

    public MaterialInfo setTolerancePercent(double tolerancePercent) {
        TolerancePercent = tolerancePercent;
        Tolerance = TolerancePercent / 100;
        return this;
    }

    public MaterialInfo setToleranceInGram(double toleranceInGram) {
        if (APW <= 0 || toleranceInGram <= 0) {
            Tolerance = 1;
        } else {
            Tolerance = toleranceInGram / APW;
        }
        return this;
    }

    public int ShelfLifeDays = 30;
    public String ShortDesc;

    public String getShortDesc() {
        return Name + "(" + Number + ")";
    }

    public static String ParseBarcode(String code) {
        // 原始: 0999999999FB820200331ABCD#20200331#999999
        // 解析: 0 999999999 FB820200331ABCD # 20200331 # 999999
        //      固定0起始     批次号           分隔符      分隔符
        //        9位SKU号                     生产日期     唯一码
        // ^0\d{9}[A-Z0-9][A-Z0-9\-_]{0,14}#\d{8}#\S*$

        String number = code;
        try {
            Pattern p = Pattern.compile("^0\\d{9}[A-Z0-9][A-Z0-9\\-_]{0,14}#\\d{8}#\\S*$");
            Matcher m = p.matcher(code);
            if (m.find()) {
                // matched
                number = m.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }
}
