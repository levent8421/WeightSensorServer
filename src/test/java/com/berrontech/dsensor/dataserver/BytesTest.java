package com.berrontech.dsensor.dataserver;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

/**
 * Create By Levent8421
 * Create Time: 2020/9/14 15:30
 * Class Name: BytesTest
 * Author: Levent8421
 * Description:
 * 测试数组转换
 *
 * @author Levent8421
 */
public class BytesTest {
    private static final byte PACKAGE_START = 0x0A;
    private static final byte PACKAGE_END = 0x0D;

    public void normalizeBytes(byte[] data) {
        final StringBuilder sb = new StringBuilder(toHexString(data[0]));
        for (int i = 1; i < data.length; i++) {
            sb.append(" ");
            sb.append(toHexString(data[i]));
        }
        final String hexStr = sb.toString();
        System.out.println(hexStr);
        System.out.println(hexStr.getBytes().length);
    }

    private String toHexString(byte data) {
        final String s = ("00" + Integer.toHexString(data & 0xFF));
        return s.substring(s.length() - 2).toUpperCase();
    }

    @Test
    public void testEncodeAsBytes() {
        byte[] res = encodeAsBytes("12345", (byte) 1, (byte) 2, 3, (byte) 4, "123", new Date());
        normalizeBytes(res);
    }

    private byte[] encodeAsBytes(String villageName, byte houseNum, byte terminalNum, int trashNum, byte type, String weight, Date date) {
        final byte[] res = new byte[43];
        res[0] = PACKAGE_START;
        res[42] = PACKAGE_END;
        // 小区名称
        final byte[] villageNameBytes = string2Bytes(villageName);
        fillBytes(res, villageNameBytes, 1, 20, (byte) 0x20);
        //清洁屋仪表
        res[21] = houseNum;
        res[22] = terminalNum;
        //垃圾桶编号
        res[23] = 0;
        res[24] = (byte) (trashNum >> 24);
        res[25] = (byte) (trashNum >> 16);
        res[26] = (byte) (trashNum >> 8);
        res[27] = (byte) trashNum;
        //垃圾种类
        res[28] = type;
        //显示重量
        weight = "      " + weight;
        weight = weight.substring(weight.length() - 6);
        final byte[] weightBytes = string2Bytes(weight);
        fillBytes(res, weightBytes, 29, 6, (byte) 0x20);
        //时间日期
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        res[35] = (byte) (calendar.get(Calendar.YEAR) - 2000);
        res[36] = (byte) (calendar.get(Calendar.MONTH) + 1);
        res[37] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        res[38] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        res[39] = (byte) calendar.get(Calendar.MINUTE);
        res[40] = (byte) calendar.get(Calendar.SECOND);
        //校验
        int sum = 0;
        for (int i = 1; i < 41; i++) {
            sum += res[i] & 0xFF;
        }
        final byte chk = (byte) (sum % 0x100);
        res[41] = chk;
        return res;
    }

    private byte[] string2Bytes(String str) {
        try {
            return str.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillBytes(byte[] dest, byte[] bytes, int start, int len, byte defaultByte) {
        for (int i = 0; i < len; i++) {
            final int destIndex = start + i;
            if (i >= bytes.length) {
                dest[destIndex] = defaultByte;
            } else {
                dest[destIndex] = bytes[i];
            }
        }
    }
}
