package com.berrontech.dsensor.dataserver.weight.digitalSensor;

public class DataPacket22 extends DataPacket {
    @Override
    public int getVersion() {
        return Version22;
    }

    @Override
    public int getChecksumLength() {
        return 2;
    }

    @Override
    public byte[] CalcChecksum() {
        int cs1 = getVersion();
        cs1 += getAddress();
        cs1 += getCmd();
        if (Content != null) {
            for (byte b : Content) {
                cs1 += b;
            }
        }
        cs1 = ~cs1;

        int cs2 = getVersion();
        cs2 ^= getAddress();
        cs2 ^= getCmd();
        if (Content != null) {
            for (byte b : Content) {
                cs2 ^= (b & 0xFF);
            }
        }
        cs2 ^= cs1;

        return new byte[]{(byte) (cs1 & 0xFF), (byte) (cs2 & 0xFF)};
    }
}
