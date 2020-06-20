package com.berrontech.dsensor.dataserver.weight.utils.helper;


import lombok.NonNull;

public class ByteHelper {

    public static int bytesToInt(byte[] src) {
        return bytesToInt(src, 0, 4);
    }

    public static int bytesToInt(byte[] src, int offset, int len) {
//        int value;
//        value = (int) ((src[offset] & 0xFF)
//                | ((src[offset+1] & 0xFF)<<8)
//                | ((src[offset+2] & 0xFF)<<16)
//                | ((src[offset+3] & 0xFF)<<24));
//        return value;
        int val = 0;
        int pos = 0;
        len = Math.min(len, 4);
        len = Math.min(len, src.length - offset);
        while (pos < len) {
            val |= ((src[offset + pos] & 0xFF) << (8 * pos));
            pos++;
        }
        return val;
    }

    public static void intToBytes(int val, @NonNull byte[] dst, int offset) {
        intToBytes(val, dst, offset, 4);
    }

    public static void intToBytes(int val, @NonNull byte[] dst, int offset, int len) {
//    byte[] src = new byte[4];
//    src[3] =  (byte) ((value>>24) & 0xFF);
//    src[2] =  (byte) ((value>>16) & 0xFF);
//    src[1] =  (byte) ((value>>8) & 0xFF);
//    src[0] =  (byte) (value & 0xFF);
//    return src;
        int pos = 0;
        len = Math.min(len, 4);
        len = Math.min(len, dst.length - offset);
        while (pos < len) {
            dst[offset + pos] = (byte) ((val >> (8 * pos)) & 0xFF);
            pos++;
        }
    }

    public static byte[] intToBytes(int val)
    {
        byte[] bts = new byte[4];
        intToBytes(val, bts, 0);
        return bts;
    }

    public static byte[] floatToBytes(float Value) {
        int accum = Float.floatToRawIntBits(Value);
        byte[] byteRet = new byte[4];
        byteRet[0] = (byte) (accum & 0xFF);
        byteRet[1] = (byte) ((accum >> 8) & 0xFF);
        byteRet[2] = (byte) ((accum >> 16) & 0xFF);
        byteRet[3] = (byte) ((accum >> 24) & 0xFF);
        return byteRet;
    }

    public static float bytesToFloat(byte[] bytes, int offset, int len) {
        return Float.intBitsToFloat(ByteHelper.bytesToInt(bytes, offset, len));
    }

    public static double bytesToDouble(byte[] bytes) {
        long l = bytesToLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public static long bytesToLong(byte[] bytes) {
        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16)) | (0xff000000L & ((long) bytes[3] << 24))
                | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    public static byte[] doubleToBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return longToBytes(intBits);
    }

    public static byte[] longToBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }
}
