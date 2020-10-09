package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;

@Data
public class DataPacket {
    // 0x02 0xFD len:1 ver:1 cmd:1 content:n checksum:1
    // |heads   |len  |data
    public static final String DefaultCharsetName = "UTF-8";
    public static final byte Head1 = 0x02;
    public static final byte Head2 = (byte) 0xFD;
    private byte Version = 0x21;
    private byte Address = 0x00;
    private byte Cmd = 0;
    public byte[] Content;
    private byte Checksum;

    public int getContentLength() {
        return (Content == null ? 0 : Content.length);
    }

    // special addresses
    public static final byte AddressDefault = 0x00;
    public static final byte AddressBroadcast = (byte) 0xFF;
    public static final byte AddressConditionalBroadcast = (byte) 0xFE;
    public static final int AddressMin = 0x01;
    public static final int AddressMax = 0xF0;
    public static final int AddressELabelStart = 100;
    public static final int AddressELabelEnd = 199;
    public static final int AddressXSensorStart = 201;
    public static final int AddressXSensorEnd = 230;


    public int GetLength() {
        return (1 + 1 + 1 + getContentLength() + 1);
    }

    public byte CalcChecksum() {
        byte cs = Version;
        cs ^= Address;
        cs ^= Cmd;
        if (Content != null) {
            for (byte b : Content) {
                cs ^= b;
            }
        }
        return cs;
    }

    public byte[] ToBytes() {
        // 0x02 0xFD len:1 ver:1 cmd:1 content:n checksum:1
        // |heads   |len  |data                           |
        byte[] bts = new byte[1 + 1 + 1 + GetLength()];
        bts[0] = Head1;
        bts[1] = Head2;
        bts[2] = (byte) GetLength();
        bts[3] = getVersion();
        bts[4] = getAddress();
        bts[5] = getCmd();
        if (getContent() != null) {
            System.arraycopy(getContent(), 0, bts, 6, getContent().length);
        }
        bts[bts.length - 1] = CalcChecksum();
        return bts;
    }

    public static DataPacket ParseData(byte[] data) {
        DataPacket pack = new DataPacket();
        pack.setVersion(data[0]);
        pack.setAddress(data[1]);
        pack.setCmd(data[2]);
        pack.setContent(Arrays.copyOfRange(data, 3, data.length - 1));
        pack.setChecksum(data[data.length - 1]);
        return pack;
    }


    public interface ERecvCmd {
        byte RawCount = 'R';
        byte SetAddress = 'S';
        byte Weight = 'W';
        byte HighResolution = 'H';
        byte ReadParam = 'P';
        byte WriteParam = 'Q';
        byte Calibrate = 'C';
        byte DoZero = 'Z';
        byte XSensors = 'X';

        byte IdBroadcast = 'N';
        byte SetWorkMode = 'M';
        byte ConditionalSetAddress = 'T';

        byte Upgrade = 'U';

        byte ELabel = 'L';
    }

    public interface ESendCmd {
        byte RawCount = 'r';
        byte SetAddress = 's';
        byte Weight = 'w';
        byte HighResolution = 'h';
        byte ReadParam = 'p';
        byte WriteParam = 'q';
        byte Calibrate = 'c';
        byte DoZero = 'z';
        byte XSensors = 'x';

        byte IdBroadcast = 'n';
        byte SetWorkMode = 'm';
        byte ConditionalSetAddress = 't';

        byte Upgrade = 'u';

        byte ELabel = 'l';
    }

    public interface EParam {
        int None = 0x00;

        int PtZeroRawCount = 0x01;
        int PtZeroWeight = 0x02;
        int PtMidRawCount = 0x03;
        int PtMidWeight = 0x04;
        int PtSpanRawCount = 0x05;
        int PtSpanWeight = 0x06;

        int Increment = 0x10;
        int ZeroCapture = 0x11;
        int CreepCorrect = 0x12;
        int StableRange = 0x13;
        int Capacity = 0x14;
        int GeoFactor = 0x15;
        int StableSpeed = 0x16;

        int FirmwareVersion = 0x50;
        int PCBASn = 0x51;
        int DeviceSn = 0x52;
        int DeviceModel = 0x53;

        int Locker = 0xF0;
    }

    public static byte ToRecvCmd(byte cmd) {
        return (byte) Character.toUpperCase(cmd);
    }

    public byte ToRecvCmd() {
        return ToRecvCmd(getCmd());
    }


    public interface ECalibrationPoint {
        int PointZero = 0;
        int PointSpan = 2;
    }

    public interface EELabelStatusBits {
        int Inited = 0x01;
        int Enabled = 0x02;
        int Highlight = 0x04;
        int PowerSave = 0x08;
        int LongPressedMark = 0x10;
    }

    public interface EWorkMode {
        int Normal = 0;
        int APM = 1; // active programming mode
    }

    public interface EDeviceType {
        int Unknow = 0;
        int DigitalSensor = 1;
        int ELabel = 2;
    }

    public interface EUpgradePackNo {
        int Head = 0x00;

        int DataHead = 0x01;
        int DataEnd = 0xF0;

        int End = 0xF1;

        int Query = 0xFE;
        int Start = 0xFF;
    }


    public interface EELabelCmdID {
        int Unknow = 0x00;

        int ReadStatus = 0x01;

        int WriteStatus = 0x81;
        int WritePartNumber = 0xA1;
        int WritePartName = 0xA2;
        int WriteWeight = 0xA3;
        int WritePCS = 0xA4;
        int WriteBinNo = 0xA5;
        int WriteLogo = 0xC1;
    }

    public interface EELabelPalette {
        int Unknow = 0;
        int Bpp32 = 1;
        int Bpp16 = 2;
    }

    public interface EELabelColor {
        int White = 0xFFFF;
        int Black = 0x0000;
        int Blue = 0x001F;
        int Green = 0x0308;
        int Yellow = 0xFE66;
        int Red = 0xF800;
        int Gray = 0x632C;
    }


    public interface EResult {
        int OK = 0x00;

        int ErrIndex = 0x01;
        int ErrParam = 0x02;
        int ErrSensor = 0x03;
        int ErrStorage = 0x04;
        int ErrStatus = 0x05;

        int ErrComm = 0x80;
        int ErrUnknow = 0xFF;
    }

    public static DataPacket Build(byte address, byte cmd, byte[] content) {
        DataPacket pack = new DataPacket();
        pack.setAddress(address);
        pack.setCmd(cmd);
        pack.setContent(content);
        pack.setChecksum(pack.CalcChecksum());
        return pack;
    }

    public static DataPacket Build(byte address, byte cmd) {
        return Build(address, cmd, null);
    }

    public static DataPacket BuildGetRawCount(byte address) {
        return Build(address, ESendCmd.RawCount);
    }

    public static DataPacket BuildGetWeight(byte address) {
        return Build(address, ESendCmd.Weight);
    }

    public static DataPacket BuildGetHighResolution(byte address) {
        return Build(address, ESendCmd.HighResolution);
    }

    public static DataPacket BuildGetXSensors(byte address) {
        return Build(address, ESendCmd.XSensors);
    }

    public static DataPacket BuildSetAddress(byte address, byte newAddress) {
        byte[] content = new byte[]{
                newAddress,
        };
        return Build(address, ESendCmd.SetAddress, content);
    }

    public static DataPacket BuildReadParam(byte address, int param) {
        byte[] content = new byte[]{
                (byte) param,
        };
        return Build(address, ESendCmd.ReadParam, content);
    }

    public static DataPacket BuildWriteParam(byte address, int param, byte value) {
        byte[] bts = new byte[]{value};
        return BuildWriteParam(address, param, bts);
    }

    public static DataPacket BuildWriteParam(byte address, int param, int value) {
        byte[] bts = ByteHelper.intToBytes(value);
        return BuildWriteParam(address, param, bts);
    }

    public static DataPacket BuildWriteParam(byte address, int param, float value) {
        byte[] bts = ByteHelper.floatToBytes(value);
        return BuildWriteParam(address, param, bts);
    }

    public static DataPacket BuildWriteParam(byte address, int param, BigDecimal value) {
        return BuildWriteParam(address, param, value.floatValue());
    }

    public static DataPacket BuildWriteParam(byte address, int param, String value, int maxLen) {
        if (value == null) {
            value = "";
        }
        byte[] bts = Charset.forName(DefaultCharsetName).encode(value).array();
        if (bts.length > maxLen) {
            bts = Arrays.copyOf(bts, maxLen);
        }
        return BuildWriteParam(address, param, bts);
    }

    public static DataPacket BuildWriteParam(byte address, int param, byte[] value) {
        byte[] content = new byte[value.length + 1];
        content[0] = (byte) param;
        System.arraycopy(value, 0, content, 1, value.length);
        return Build(address, ESendCmd.WriteParam, content);
    }

    public static DataPacket BuildCalibrate(byte address, int point, float weight) {
        byte[] val = ByteHelper.floatToBytes(weight);
        byte[] content = new byte[val.length + 1];
        content[0] = (byte) point;
        System.arraycopy(val, 0, content, 1, val.length);
        return Build(address, ESendCmd.Calibrate, content);
    }

    public static DataPacket BuildDoZero(byte address, boolean save) {
        byte[] content = new byte[]{
                (byte) (save ? 1 : 0),
        };
        return Build(address, ESendCmd.DoZero, content);
    }

    public static DataPacket BuildDoZero(byte address, boolean save, float weight) {
        byte[] bts = ByteHelper.floatToBytes(weight);
        byte[] content = new byte[bts.length + 1];
        content[0] = (byte) (save ? 1 : 0);
        System.arraycopy(bts, 0, content, 1, bts.length);
        return Build(address, ESendCmd.DoZero, content);
    }

    public static DataPacket BuildSetWorkMode(byte address, int mode) {
        byte[] content = new byte[]{
                (byte) mode,
        };
        return Build(address, ESendCmd.SetWorkMode, content);
    }

    public static DataPacket BuildSetAddressByDeviceSn(byte newAddress, String sn) {
        if (sn == null) {
            sn = "";
        }
        byte[] bts = Charset.forName(DefaultCharsetName).encode(sn).array();
        byte[] content = new byte[bts.length + 2];
        content[0] = newAddress;
        content[1] = (byte) EParam.DeviceSn;
        System.arraycopy(bts, 0, content, 2, bts.length);
        return Build(AddressConditionalBroadcast, ESendCmd.ConditionalSetAddress, content);
    }


    public static DataPacket BuildUpgrade(byte address, byte packNo, byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        byte[] content = new byte[data.length + 1];
        content[0] = packNo;
        System.arraycopy(data, 0, content, 1, data.length);
        return Build(address, ESendCmd.Upgrade, content);
    }

    public static DataPacket BuildUpgradeHead(byte address, int flushAddress, int dataSize) {
        byte[] bts1 = ByteHelper.intToBytes(flushAddress);
        byte[] bts2 = ByteHelper.intToBytes(dataSize);
        byte[] content = new byte[bts1.length + bts2.length];
        System.arraycopy(bts1, 0, content, 0, bts1.length);
        System.arraycopy(bts2, 0, content, bts1.length, bts2.length);
        return BuildUpgrade(address, (byte) EUpgradePackNo.Head, content);
    }

    public static DataPacket BuildUpgradeEnd(byte address) {
        return BuildUpgrade(address, (byte) EUpgradePackNo.End, null);
    }

    public static DataPacket BuildUpgradeData(byte address, byte packNo, byte[] data) {
        return BuildUpgrade(address, packNo, data);
    }

    public static DataPacket BuildUpgradeQuery(byte address) {
        return BuildUpgrade(address, (byte) EUpgradePackNo.Query, null);
    }

    public static DataPacket BuildUpgradeStart(byte address, byte deviceType, int delay) {
        byte[] bts = ByteHelper.intToBytes(delay);
        byte[] content = new byte[bts.length + 1];
        content[0] = deviceType;
        System.arraycopy(bts, 0, content, 1, bts.length);
        return BuildUpgrade(address, (byte) EUpgradePackNo.Start, content);
    }

    public static DataPacket BuildELabelCmd(byte address, int cmd, byte page, byte totalPage, byte[] data) {
        byte[] content = new byte[3 + (data == null ? 0 : data.length)];
        content[0] = (byte) cmd;
        content[1] = page;
        content[2] = totalPage;
        if (data != null) {
            System.arraycopy(data, 0, content, 3, data.length);
        }
        return Build(address, ESendCmd.ELabel, content);
    }
}
