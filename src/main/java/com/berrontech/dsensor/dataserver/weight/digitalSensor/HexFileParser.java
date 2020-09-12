package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class HexFileParser {


    private int CurrentAddress = 0;
    private int CurrentLine = 0;

    private String[] Lines = null;

    public void Import(byte[] hexFileContent) {
        String ct = new String(hexFileContent);
        Lines = ct.split("[\r,\n]");
    }

    public void Reset() {
        this.CurrentAddress = 0;
        this.CurrentLine = 0;
    }

    private HexFileLineInfo NextLine() throws Exception {
        String line;
        do {
            if (Lines == null || CurrentLine >= Lines.length) {
                return null;
            }
            line = Lines[CurrentLine++].trim();
        } while (Strings.isBlank(line));
        HexFileLineInfo info = HexFileLineInfo.Parse(line);
        switch (info.RecordType) {
            case HexFileLineInfo.RecordTypes.SegmentExt:
            case HexFileLineInfo.RecordTypes.LinearExt: {
                this.CurrentAddress = info.DataToAddress();
                break;
            }
            default: {
                break;
            }
        }

        return info;
    }

    public List<HexDataBlockInfo> ReadToDataBlocks() throws Exception {
        Reset();
        List<HexDataBlockInfo> blocks = new ArrayList<>();
        int startAdd = this.CurrentAddress;
        byte[] data = new byte[0];
        boolean eof = false;
        do {
            HexFileLineInfo line = NextLine();
            if (line == null) {
                throw new Exception("Unexpected file end");
            }
            switch (line.RecordType) {
                case HexFileLineInfo.RecordTypes.Data: {
                    if (startAdd + data.length == this.CurrentAddress + line.Offset) {
                        // continuous data, append to the end
                        byte[] newbuf = new byte[data.length + line.DataLength];
                        System.arraycopy(newbuf, 0, data, 0, data.length);
                        System.arraycopy(newbuf, data.length, line.Data, 0, line.DataLength);
                    } else {
                        // not continuous address, save history, start new block recording
                        if (data.length != 0) {
                            // save
                            blocks.add(new HexDataBlockInfo(startAdd, data));
                        }

                        // start new block
                        startAdd = this.CurrentAddress + line.Offset;
                        data = new byte[line.DataLength];
                        System.arraycopy(data, 0, line.Data, 0, line.DataLength);
                    }
                    break;
                }
                case HexFileLineInfo.RecordTypes.EOF: {
                    // not continuous address, save history, start new block recording
                    if (data.length != 0) {
                        // save
                        blocks.add(new HexDataBlockInfo(startAdd, data));
                    }
                    eof = true;
                    break;
                }
                default: {
                    break;
                }
            }
        } while (!eof);

        return blocks;
    }


    public static class HexFileLineInfo {

        // line type:
        // [:][2 char (1 byte) length][4 char (2 bytes) offset address][2 char (1 byte) type][n * 2 char (n bytes) data][2 char (1 byte) checksum][0d][0a]
        // [0][1 ~ 2][3 ~ 6][7 ~ 8][9 ~ 9+n*2][9+n*2+1 ~ 9+n*2+2][0d0a]
        // checksum = NOT( MOD ( SUM( from length char to the end of data bytes ) ) ) + 0x01
        //   or     = 0x100 - MOD( SUM( from length char to the end of data bytes ) )

        public interface RecordTypes {
            int Unknow = -1;
            int Data = 0;
            int EOF = 1;                    // end of file
            int SegmentExt = 2;             // 80x86: base address of 4-19 bits, must move left 4 bits before using, << 4
            int SegmentStart = 3;           // 80x86: specifies the initial content of the CS:IP registers. The address field is 0000, the byte count is 04, the first two bytes are the CS value, the latter two are the IP value.
            int LinearExt = 4;              // 80x386: base address of 16-31 bits, must move left 16 bits before using, << 16
            int LinearStart = 5;            // 80x386: The four data bytes represent the 32-bit value loaded into the EIP register of the 80386 and higher CPU.
        }

        public static final char HeadChar = ':';
        public byte DataLength;

        public int Offset;

        public int RecordType;

        public byte[] Data;

        public int CalcChecksum() {
            int sum = 0;
            sum += DataLength;
            sum += ByteHelper.calcSum(ByteHelper.intToBytes(Offset));
            sum += RecordType;
            if (this.Data != null) {
                sum += ByteHelper.calcSum(Data);
            }
            sum = (byte) (0x100 - sum);
            return sum;
        }

        public HexFileLineInfo() {
            Clear();
        }

        public void Clear() {
            DataLength = 0;
            Offset = 0;
            RecordType = RecordTypes.Unknow;
            Data = null;
        }

        public int DataToAddress() {
            if (Data == null) {
                return 0;
            }

            int address = 0;
            switch (this.RecordType) {
                case RecordTypes.SegmentExt: {
                    address = BytesToAddress(Data);
                    address = address << 4;
                    break;
                }
                case RecordTypes.LinearExt: {
                    address = BytesToAddress(Data);
                    address = address << 16;
                    break;
                }
                default: {
                    break;
                }
            }
            return address;
        }

        public static int BytesToAddress(byte[] bytes) {
            int address = 0;
            for (byte b : bytes) {
                address = (address << 8) + b;
            }
            return address;
        }

        public static byte[] HexStrToBytes(String str) {
            byte[] buf = new byte[str.length() / 2];
            for (int pos = 0; pos < buf.length; pos++) {
                buf[pos] = ByteHelper.hexCharsToByte(str, pos * 2, 2);
            }
            return buf;
        }

        public static byte[] HexStrToBytes(String str, int offset, int length) {
            return HexStrToBytes(str.substring(offset, offset + length));
        }

        public static HexFileLineInfo Parse(String line) throws Exception {
            // [:][2 char (1 byte) length][4 char (2 bytes) offset address][2 char (1 byte) type][n * 2 char (n bytes) data][2 char (1 byte) checksum][0d][0a]
            // [0][1 ~ 2][3 ~ 6][7 ~ 8][9 ~ 9+n*2][9+n*2+1 ~ 9+n*2+2][0d0a]
            HexFileLineInfo info = new HexFileLineInfo();
            if (line.charAt(0) != HeadChar) {
                throw new Exception(String.format("Unexpected head char: %c / %c", line.charAt(0), HeadChar));
            }
            info.DataLength = ByteHelper.hexCharsToByte(line, 1, 2);

            int expLen = 1 + 2 + 4 + 2 + info.DataLength * 2 + 2;
            if (line.length() < expLen) {
                throw new Exception(String.format("line is too short: %d < %d", line.length(), expLen));
            }

            info.Offset = BytesToAddress(HexStrToBytes(line, 3, 4));
            info.RecordType = ByteHelper.hexCharsToByte(line, 7, 2);
            info.Data = HexStrToBytes(line, 9, info.DataLength * 2);
            byte checksum = ByteHelper.hexCharsToByte(line, 9 + info.DataLength * 2, 2);
            if (checksum != info.CalcChecksum()) {
                throw new Exception(String.format("Error checksum: %d / %d", checksum, info.CalcChecksum()));
            }

            return info;
        }
    }

    public static class HexDataBlockInfo {
        public int Address;

        public byte[] Data;

        public HexDataBlockInfo() {
        }

        public HexDataBlockInfo(int address, byte[] data) {
            this.Address = address;
            this.Data = data;
        }
    }

}

