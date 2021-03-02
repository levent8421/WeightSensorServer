package com.berrontech.dsensor.dataserver.weight.digitalSensor;

public class DataPacket21 extends DataPacket {
    @Override
    public int getVersion() {
        return Version21;
    }

    @Override
    public int getChecksumLength() {
        return 1;
    }

    @Override
    public byte[] CalcChecksum() {
        int cs = getVersion();
        cs ^= getAddress();
        cs ^= getCmd();
        if (Content != null) {
            for (byte b : Content) {
                cs ^= (b & 0xFF);
            }
        }
        return new byte[]{(byte) (cs & 0xFF)};
    }
}
