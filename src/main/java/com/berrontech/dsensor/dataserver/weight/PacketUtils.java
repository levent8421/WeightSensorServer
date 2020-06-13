package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.util.ByteUtils;
import lombok.val;

import static com.berrontech.dsensor.dataserver.common.util.ByteUtils.int2Byte;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 19:20
 * Class Name: PacketUtils
 * Author: Levent8421
 * Description:
 * 数据包工具类
 *
 * @author Levent8421
 */
public class PacketUtils {

    /**
     * 计算CRC
     *
     * @param version version
     * @param address address
     * @param command command
     * @param data    data
     * @return crc
     */
    public static int calcPacketCRC(int version, int address, int command, byte[] data) {
        int crc = 0;
        crc ^= version;
        crc ^= address;
        crc ^= command;
        if (data != null) {
            for (byte b : data) {
                final int ib = ByteUtils.byte2Int(b);
                crc ^= ib;
            }
        }
        return crc;
    }

    /**
     * 数据包转二进制
     *
     * @param packet 数据包
     * @return 二进制数组
     */
    public static byte[] asBytes(DataPacket packet) {
        final byte[] data = new byte[packet.getDataLength() + DataPacket.DATA_OFFSET + 1];
        data[0] = 0;
        data[1] = DataPacket.PACKAGE_START_1;
        data[2] = int2Byte(DataPacket.PACKAGE_START_2);
        data[3] = int2Byte(packet.getDataLength());
        data[4] = int2Byte(packet.getVersion());
        data[5] = int2Byte(packet.getAddress());
        data[6] = int2Byte(packet.getCommand());
        final byte[] pData = packet.getData();
        if (pData != null) {
            System.arraycopy(pData, 0, data, 6, pData.length);
        }
        data[data.length - 1] = ByteUtils.int2Byte(packet.getCrc());
        return data;
    }

    /**
     * 字节数组转数据包
     *
     * @param data 字节数组
     * @return 数据包
     */
    public static DataPacket asPacket(byte[] data) throws PacketException {
        val packet = new DataPacket();
        final int len = data.length;
        packet.setDataLength(len);
        packet.setVersion(data[0]);
        packet.setAddress(data[1]);
        packet.setCommand(data[2]);
        final byte[] pData = new byte[len - DataPacket.NO_DATA_LEN];
        System.arraycopy(data, 3, pData, 0, pData.length);
        packet.setData(pData);
        final int crc = ByteUtils.byte2Int(data[data.length - 1]);
        final int calcCrc = ByteUtils.crc(data, 0, data.length - 1);
        if (crc != calcCrc) {
            val msg = String.format("Package CRC Not match,calc/pack=[%s/%s]", packet.getCrc(), crc);
            throw new PacketException(msg);
        }
        packet.setCrc(calcCrc);
        return packet;
    }
}
