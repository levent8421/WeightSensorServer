package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

@Slf4j
@Data
public abstract class DataPacket {
    // Ver  |heads      |len    |data                                    |
    // V21:  0x02 0xFD   len:1   ver:1 addr:1 cmd:1 content:n xor:1
    // V22:  0x02 0xFD   len:1   ver:1 addr:1 cmd:1 content:n ~cs:1 xor:1

    public static final String DefaultCharsetName = "UTF-8";

    public static final byte Head1 = 0x02;
    public static final byte Head2 = (byte) 0xFD;

    public static final int  Version21 = 0x21;
    public static final int  Version22 = 0x22;

    public abstract int getVersion();

    private int Address = 0x00;
    private int Cmd = 0;
    public byte[] Content;
    private byte[] Checksum;

    public int getContentLength() {
        return (Content == null ? 0 : Content.length);
    }

    public abstract int getChecksumLength();

    // special addresses
    public static final int AddressDefault = 0x00;
    public static final int AddressBroadcast = 0xFF;
    public static final int AddressConditionalBroadcast = 0xFE;
    public static final int AddressMin = 0x01;
    public static final int AddressMax = 0xF0;
    public static final int AddressELabelStart = 100;
    public static final int AddressELabelEnd = 199;
    public static final int AddressXSensorStart = 201;
    public static final int AddressXSensorEnd = 230;


    public int GetLength() {
        return (1 + 1 + 1 + getContentLength() + getChecksumLength());
    }

    public abstract byte[] CalcChecksum();

    public boolean isChecksumOK() {
        return Arrays.equals(CalcChecksum(), Checksum);
    }

    public byte[] ToBytes() {
        // |heads   |len  |data                           |
        byte[] bts = new byte[1 + 1 + 1 + GetLength()];
        bts[0] = Head1;
        bts[1] = Head2;
        bts[2] = (byte) (GetLength() & 0xFF);
        bts[3] = (byte) (getVersion() & 0xFF);
        bts[4] = (byte) (getAddress() & 0xFF);
        bts[5] = (byte) (getCmd() & 0xFF);
        if (getContent() != null) {
            System.arraycopy(getContent(), 0, bts, 6, getContent().length);
        }
        byte[] cs = CalcChecksum();
        System.arraycopy(cs, 0, bts, bts.length - cs.length, cs.length);
        return bts;
    }

    public static DataPacket ParseData(byte[] data) throws TimeoutException {
        DataPacket pack = null;
        int ver = data[0] & 0xFF;
        switch (ver)
        {
            case Version21:
            {
                pack = new DataPacket21();
                pack.setAddress(data[1] & 0xFF);
                pack.setCmd(data[2] & 0xFF);
                pack.setContent(Arrays.copyOfRange(data, 3, data.length - 1));
                pack.setChecksum(Arrays.copyOfRange(data, data.length - 1, data.length));
                break;
            }
            case Version22:
            {
                pack = new DataPacket22();
                pack.setAddress(data[1] & 0xFF);
                pack.setCmd(data[2] & 0xFF);
                pack.setContent(Arrays.copyOfRange(data, 3, data.length - 2));
                pack.setChecksum(Arrays.copyOfRange(data, data.length - 2, data.length));
                break;
            }
            default:
            {
                throw new TimeoutException(String.format("Unknow pack version: 0x%X", ver));
            }
        }
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

        int Inputs1 = 0x30;
        int Outputs1 = 0x32;
        int BatteryV = 0x35;
        int ExtPowerV = 0x36;
        int Com1Baud = 0x37;
        int ExtFunctions = 0x40;

        int FirmwareVersion = 0x50;
        int PCBASn = 0x51;
        int DeviceSn = 0x52;
        int DeviceModel = 0x53;

        int Locker = 0xF0;
    }

    public static int ToRecvCmd(int cmd) {
        return Character.toUpperCase(cmd);
    }

    public int ToRecvCmd() {
        return ToRecvCmd(getCmd());
    }


    public interface ECalibrationPoint {
        int PointZero = 0;
        int PointMiddle = 1;
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
        byte Unknow = 0;
        byte Bpp32 = 1;
        byte Bpp16 = 2;
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

    public DataPacket Build(int address, int cmd, byte[] content) {
        setAddress(address);
        setCmd(cmd);
        setContent(content);
        return this;
    }

    public DataPacket Build(int address, int cmd) {
        return Build(address, cmd, null);
    }

    public DataPacket BuildGetRawCount(int address) {
        return Build(address, ESendCmd.RawCount);
    }

    public DataPacket BuildGetWeight(int address) {
        return Build(address, ESendCmd.Weight);
    }

    public DataPacket BuildGetHighResolution(int address) {
        return Build(address, ESendCmd.HighResolution);
    }

    public DataPacket BuildGetXSensors(int address) {
        return Build(address, ESendCmd.XSensors);
    }

    public DataPacket BuildSetAddress(int address, int newAddress) {
        byte[] content = new byte[]{
                (byte) (newAddress & 0xFF),
        };
        return Build(address, ESendCmd.SetAddress, content);
    }

    public DataPacket BuildReadParam(int address, int param) {
        byte[] content = new byte[]{
                (byte) (param & 0xFF),
        };
        return Build(address, ESendCmd.ReadParam, content);
    }

    public DataPacket BuildWriteParam(int address, int param, byte value) {
        byte[] bts = new byte[]{value};
        return BuildWriteParam(address, param, bts);
    }

    public DataPacket BuildWriteParam(int address, int param, int value) {
        byte[] bts = ByteHelper.intToBytes(value);
        return BuildWriteParam(address, param, bts);
    }

    public DataPacket BuildWriteParam(int address, int param, float value) {
        byte[] bts = ByteHelper.floatToBytes(value);
        return BuildWriteParam(address, param, bts);
    }

    public DataPacket BuildWriteParam(int address, int param, BigDecimal value) {
        return BuildWriteParam(address, param, value.floatValue());
    }

    public DataPacket BuildWriteParam(int address, int param, String value, int maxLen) throws Exception {
        if (value == null) {
            value = "";
        }
        byte[] bts = value.getBytes(DefaultCharsetName);
        if (bts.length > maxLen) {
            bts = Arrays.copyOf(bts, maxLen);
        }
        return BuildWriteParam(address, param, bts);
    }

    public DataPacket BuildWriteParam(int address, int param, byte[] value) {
        byte[] content = new byte[value.length + 1];
        content[0] = (byte) (param & 0xFF);
        System.arraycopy(value, 0, content, 1, value.length);
        return Build(address, ESendCmd.WriteParam, content);
    }

    public DataPacket BuildCalibrate(int address, int point, float weight) {
        byte[] val = ByteHelper.floatToBytes(weight);
        byte[] content = new byte[val.length + 1];
        content[0] = (byte) (point & 0xFF);
        System.arraycopy(val, 0, content, 1, val.length);
        return Build(address, ESendCmd.Calibrate, content);
    }

    public DataPacket BuildDoZero(int address, boolean save) {
        byte[] content = new byte[]{
                (byte) (save ? 1 : 0),
        };
        return Build(address, ESendCmd.DoZero, content);
    }

    public DataPacket BuildDoZero(int address, boolean save, float weight) {
        byte[] bts = ByteHelper.floatToBytes(weight);
        byte[] content = new byte[bts.length + 1];
        content[0] = (byte) (save ? 1 : 0);
        System.arraycopy(bts, 0, content, 1, bts.length);
        return Build(address, ESendCmd.DoZero, content);
    }

    public DataPacket BuildSetWorkMode(int address, int mode) {
        byte[] content = new byte[]{
                (byte) (mode & 0xFF),
        };
        return Build(address, ESendCmd.SetWorkMode, content);
    }

    public DataPacket BuildSetAddressByDeviceSn(int newAddress, String sn) throws Exception {
        if (sn == null) {
            sn = "";
        }
        byte[] bts = sn.getBytes(DefaultCharsetName);
        byte[] content = new byte[bts.length + 2];
        content[0] = (byte) (newAddress & 0xFF);
        content[1] = (byte) (EParam.DeviceSn & 0xFF);
        System.arraycopy(bts, 0, content, 2, bts.length);
        log.debug("BuildSetAddressByDeviceSn: newAddress={}, sn={}, btsLength={}", newAddress, sn, bts.length);
        return Build(AddressConditionalBroadcast, ESendCmd.ConditionalSetAddress, content);
    }


    public DataPacket BuildUpgrade(int address, int packNo, byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        byte[] content = new byte[data.length + 1];
        content[0] = (byte) (packNo & 0xFF);
        System.arraycopy(data, 0, content, 1, data.length);
        return Build(address, ESendCmd.Upgrade, content);
    }

    public DataPacket BuildUpgradeHead(int address, int flushAddress, int dataSize) {
        byte[] bts1 = ByteHelper.intToBytes(flushAddress);
        byte[] bts2 = ByteHelper.intToBytes(dataSize);
        byte[] content = new byte[bts1.length + bts2.length];
        System.arraycopy(bts1, 0, content, 0, bts1.length);
        System.arraycopy(bts2, 0, content, bts1.length, bts2.length);
        return BuildUpgrade(address, (byte) EUpgradePackNo.Head, content);
    }

    public DataPacket BuildUpgradeEnd(int address) {
        return BuildUpgrade(address, EUpgradePackNo.End, null);
    }

    public DataPacket BuildUpgradeData(int address, int packNo, byte[] data) {
        return BuildUpgrade(address, packNo, data);
    }

    public DataPacket BuildUpgradeQuery(int address) {
        return BuildUpgrade(address, EUpgradePackNo.Query, null);
    }

    public DataPacket BuildUpgradeStart(int address, int deviceType, int delay) {
        byte[] bts = ByteHelper.intToBytes(delay);
        byte[] content = new byte[bts.length + 1];
        content[0] = (byte) (deviceType & 0xFF);
        System.arraycopy(bts, 0, content, 1, bts.length);
        return BuildUpgrade(address, (byte) (EUpgradePackNo.Start & 0xFF), content);
    }

    public DataPacket BuildELabelCmd(int address, int cmd, int page, int totalPage, byte[] data) {
        byte[] content = new byte[3 + (data == null ? 0 : data.length)];
        content[0] = (byte) (cmd & 0xFF);
        content[1] = (byte) (page & 0xFF);
        content[2] = (byte) (totalPage & 0xFF);
        if (data != null) {
            System.arraycopy(data, 0, content, 3, data.length);
        }
        return Build(address, ESendCmd.ELabel, content);
    }
}
