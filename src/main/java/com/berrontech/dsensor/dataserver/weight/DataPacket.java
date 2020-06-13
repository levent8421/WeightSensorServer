package com.berrontech.dsensor.dataserver.weight;

import com.berrontech.dsensor.dataserver.common.util.ByteUtils;
import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 17:22
 * Class Name: DataPacket
 * Author: Levent8421
 * Description:
 * 重力传感器数据包
 *
 * @author Levent8421
 */
@Data
public class DataPacket {
    public static final byte PACKAGE_START_1 = 0x02;
    public static final int PACKAGE_START_2 = 0xFD;
    public static final int DATA_OFFSET = 3;
    public static final int NO_DATA_LEN = DATA_OFFSET + 4;

    private int dataLength;
    private int version;
    private int address;
    private int command;
    private byte[] data;
    private int crc;

    public void calcDataLength() {
        this.dataLength = data == null ? 4 : data.length + 4;
    }

    public int doCRC() {
        final byte[] crcBytes = new byte[dataLength];
        crcBytes[0] = ByteUtils.int2Byte(version);
        crcBytes[1] = ByteUtils.int2Byte(address);
        crcBytes[2] = ByteUtils.int2Byte(command);
        if (data != null) {
            System.arraycopy(data, 0, crcBytes, 3, data.length);
        }
        this.crc = ByteUtils.crc(crcBytes, 0, dataLength);
        return crc;
    }
}
