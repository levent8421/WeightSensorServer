package com.berrontech.dsensor.dataserver.common.util;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 17:27
 * Class Name: ByteUtils
 * Author: Levent8421
 * Description:
 * Byte Utils
 *
 * @author Levent8421
 */
public class ByteUtils {
    /**
     * 把一个字节转换为一个无符号整型数据
     *
     * @param b byte
     * @return int
     */
    public static int byte2Int(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return 0xFF & b;
        }
    }

    /**
     * 把整型数据转换为一字节数据
     *
     * @param i int
     * @return byte
     */
    public static byte int2Byte(int i) {
        return (byte) (i & 0xFF);
    }

    /**
     * 计算CRC校验码
     *
     * @param bytes    二进制数组
     * @param startPos start position
     * @param len      Length
     * @return crc
     */
    public static int crc(byte[] bytes, int startPos, int len) {
        int crc = 0;
        for (int i = startPos; i < startPos + len; i++) {
            int b = byte2Int(bytes[i]);
            crc ^= b;
        }
        return crc;
    }
}
