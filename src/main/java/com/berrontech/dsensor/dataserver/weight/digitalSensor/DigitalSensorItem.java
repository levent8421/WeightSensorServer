package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Slf4j
@Data
public class DigitalSensorItem {
    public DigitalSensorDriver Driver;
    public DigitalSensorGroup Group;

    public DigitalSensorManager getSensorMgr() {
        return Group.getManager();
    }

    public String SubGroup = "";
    public String SubGroupPosition = "";
    public String Cluster = "";

    public String getShortName() {
        return getSubGroup() + "-" + getSubGroupPosition();
    }

    public DigitalSensorDriver getDriver() {
        return Driver;
    }

    public DigitalSensorItem setDriver(DigitalSensorDriver driver) {
        Driver = driver;
        return this;
    }

    public int getReadTimeout() {
        return getGroup().getReadTimeout();
    }

    public int getCalibrateTimeout() {
        return getReadTimeout() + 3000;
    }

    public int getDoZeroTimeout() {
        return getReadTimeout() + 2000;
    }

    public int getWriteParamTimeout() {
        return getReadTimeout() + 1000;
    }

    ;

    public boolean isAutoRestoreZeroOffset() {
        return getSensorMgr().isAutoRestoreZeroOffset();
    }

    public boolean isPowerUpZero() {
        return getSensorMgr().isPowerUpZero();
    }

    private DigitalSensorValues Values = new DigitalSensorValues();
    private DigitalSensorParams Params = new DigitalSensorParams();
    private DigitalSensorPassenger Passenger = new DigitalSensorPassenger();


    public enum EAddressMode {
        Not,
        Waiting,
        ProgramingSensor,
        BindingELabel,
        ProgramingBoth,
        Done,
    }

    public EAddressMode AddressMode = EAddressMode.Not;

    public boolean IsAddressMode() {
        return getAddressMode() != EAddressMode.Not;
    }

    public boolean IsNotAddressMode() {
        return !IsAddressMode();
    }

    public boolean IsAddressWaiting() {
        return getAddressMode() == EAddressMode.Waiting;
    }

    public boolean IsAddressProgramingSensor() {
        return getAddressMode() == EAddressMode.ProgramingSensor;
    }

    ;

    public boolean IsAddressBindingELabel() {
        return getAddressMode() == EAddressMode.BindingELabel;
    }

    public boolean IsAddressProgramingBoth() {
        return getAddressMode() == EAddressMode.ProgramingBoth;
    }

    public boolean IsAddressDone() {
        return getAddressMode() == EAddressMode.Done;
    }


    private int TotalErrors;
    private int TotalSuccess;
    private int ContinueErrors;
    public boolean Online = false;
    public int HighResCounter;
    public boolean CountInAccuracy = true;

    public boolean IsOnline() {
        return Online && (getTotalSuccess() > 0 && getContinueErrors() < 2);
    }

    public boolean IsCountOutOfAccuracy() {
        return !isCountInAccuracy();
    }

    private void SetCommResult(boolean ok) {
        if (ok) {
            setOnline(true);
            TotalSuccess++;
            ContinueErrors = 0;
        } else {
            TotalErrors++;
            ContinueErrors++;
        }
    }

    public static DigitalSensorItem NewSensor(byte address, DigitalSensorDriver driver, DigitalSensorGroup group) {
        DigitalSensorItem item = new DigitalSensorItem();
        item.Params = new DigitalSensorParams();
        item.Params.setAddress(address);
        item.Driver = driver;
        item.Group = group;
        return item;
    }

    public static DigitalSensorItem NewDefaultSensor(DigitalSensorDriver driver, DigitalSensorGroup group) {
        DigitalSensorItem item = new DigitalSensorItem();
        item.Params = new DigitalSensorParams();
        item.Params.setAddress(DataPacket.AddressDefault);
        item.Driver = driver;
        item.Group = group;
        return item;
    }

    public void SetAddress() throws Exception {
        try {
            log.info("#{} SetAddress", Params.getAddress());
            DataPacket packet = DataPacket.BuildSetAddress(DataPacket.AddressDefault, (byte) Params.getAddress());
            synchronized (getDriver().getLock()) {
                packet = getDriver().WriteRead(packet, getWriteParamTimeout());
            }
            SetCommResult(true);
            log.info("#{} SetAddress Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void SetELabelAddress() throws Exception {
        try {
            int labelAddr = Params.getAddress() + DataPacket.AddressELabelStart;
            log.info("#{} SetELabelAddress: {}", Params.getAddress(), labelAddr);
            DataPacket packet = DataPacket.BuildSetAddress(DataPacket.AddressDefault, (byte) (labelAddr));
            synchronized (getDriver().getLock()) {
                packet = Driver.WriteRead(packet, getWriteParamTimeout());
            }
            SetCommResult(true);
            log.info("#{} SetAddress Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void ModifyAddress(byte newAddress) throws Exception {
        try {
            log.info("#{} ModifyAddress: {}", Params.getAddress(), newAddress);
            DataPacket packet = DataPacket.BuildSetAddress((byte) Params.getAddress(), newAddress);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getWriteParamTimeout());
            }
            SetCommResult(true);
            log.info("#{} ModifyAddress Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void ClearAddress() throws Exception {
        try {
            log.info("#{} ClearAddress", Params.getAddress());
            DataPacket packet = DataPacket.BuildSetAddress((byte) Params.getAddress(), DataPacket.AddressDefault);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getWriteParamTimeout());
            }
            SetCommResult(true);
            log.info("#{} ClearAddress Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public static void clearAllAddress(DigitalSensorDriver driver) {
        try {
            log.info("#{} ClearAllAddress", DataPacket.AddressBroadcast);
            DataPacket packet = DataPacket.BuildSetAddress(DataPacket.AddressBroadcast, DataPacket.AddressDefault);
            synchronized (driver.getLock()) {
                driver.Write(packet);
            }
        } catch (Exception ex) {
            log.warn("clearAllAddress failed", ex);
        }
    }

    public static void setAllWorkMode(DigitalSensorDriver driver, int mode) {
        try {
            log.info("#{} SetAllWorkMode: {}", DataPacket.AddressBroadcast, mode);
            DataPacket packet = DataPacket.BuildSetWorkMode(DataPacket.AddressBroadcast, mode);
            synchronized (driver.getLock()) {
                driver.Write(packet);
            }
        } catch (Exception ex) {
            log.warn("setAllWorkMode failed", ex);
        }
    }

    public static boolean TryReadDeviceSnBroadcast(DigitalSensorDriver driver, Map<String, Object> devInfo, int timeout) throws Exception {
        log.debug("#{} TryReadDeviceSnBroadcast", DataPacket.AddressConditionalBroadcast);
        int type = DataPacket.EDeviceType.Unknow;
        String sn = null;
        synchronized (driver.getLock()) {
            long endTime = System.currentTimeMillis() + timeout;
            do {
                try {
                    Long t = endTime - System.currentTimeMillis();
                    DataPacket packet = driver.Read(t.intValue());
                    if (packet.getCmd() == DataPacket.ERecvCmd.IdBroadcast && packet.getContentLength() > 3) {
                        int tp = packet.Content[0];
                        int pm = packet.Content[1];
                        if (pm == DataPacket.EParam.DeviceSn) {
                            type = tp;
                            sn = new String(packet.Content, 2, packet.getContentLength() - 2, Charset.forName(DataPacket.DefaultCharsetName));
                            devInfo.put("type", type);
                            devInfo.put("sn", sn);
                            return true;
                        }
                    }
                } catch (TimeoutException ex) {
                    // do nothing
                }
            } while (System.currentTimeMillis() <= endTime);
            return false;
        }
    }

    public void SetAddressByDeviceSn(int type, String sn) throws Exception {
        try {
            log.info("#{} SetAddressByDeviceSn", DataPacket.AddressConditionalBroadcast);
            byte newAddress;
            switch (type) {
                default: {
                    newAddress = (byte) Params.getAddress();
                    break;
                }
                case DataPacket.EDeviceType.ELabel: {
                    newAddress = (byte) (DataPacket.AddressELabelStart + Params.getAddress());
                    break;
                }
            }
            DataPacket packet = DataPacket.BuildSetAddressByDeviceSn(newAddress, sn);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getWriteParamTimeout());
            }
            SetCommResult(true);
            log.info("#{} SetAddressByDeviceSn: {} -> {}", DataPacket.AddressConditionalBroadcast, sn, Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void UpdateRawCount() throws Exception {
        try {
            //log.debug("#{} UpdateRawCount", Params.getAddress());
            DataPacket packet = DataPacket.BuildGetRawCount((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
            }
            SetCommResult(true);
            Values.setDynamicRawCount(ByteHelper.bytesToInt(packet.Content, 0, 4));
            Values.setRawCount(ByteHelper.bytesToInt(packet.Content, 4, 4));
            Values.setRamp(Params.calcRamp(Values.getRawCount()));
            //log.debug("#{} UpdateRawCount Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void UpdateWeight() throws Exception {
        try {
            //log.debug("#{} UpdateWeight", Params.getAddress());
            DataPacket packet = DataPacket.BuildGetWeight((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout());
            }
            SetCommResult(true);
            Values.setGrossWeightStr(new String(packet.Content, 1, packet.Content.length - 1));
            Values.CheckStatus(packet.Content[0], Params.getCapacity(), Params.getIncrement());
            //log.debug("#{} UpdateWeight Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void UpdateHighResolution(boolean skipUnStable) throws Exception {
        try {
            //log.debug("#{} UpdateHighResolution", Params.getAddress());
            DataPacket packet = DataPacket.BuildGetHighResolution((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
            }
            SetCommResult(true);
            byte counter = packet.Content[0];
            if (counter == 0) {
                if (HighResCounter == 0) {
                    if (isPowerUpZero()) {
                        DoZero();
                    }
                } else {
                    if (isAutoRestoreZeroOffset()) {
                        // sensor power lost, and auto restore is on, try do restore
                        SetZeroOffset(false, Values.getZeroOffset());
                    }
                }
            } else {
                boolean stable = DigitalSensorValues.isStableMark(packet.Content[1]);
                if (!stable && skipUnStable) {
                    // skip unstable values
                } else {
                    HighResCounter = counter;
                    Values.setGrossWeightStr(new String(packet.Content, 2, 8).trim());
                    Values.setHighGross(ByteHelper.bytesToFloat(packet.Content, 10, 4));
                    Values.setZeroOffset(ByteHelper.bytesToFloat(packet.Content, 14, 4));
                    Values.CheckStatus(packet.Content[1], Params.getCapacity(), Params.getIncrement());

                    TryNotifyListener();
                }
            }
            //log.debug("#{} UpdateHighResolution Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void UpdateHighResolution2(boolean skipUnStable) throws Exception {
        try {
            //log.debug("#{} UpdateHighResolution2", Params.getAddress());
            DataPacket packet = DataPacket.BuildGetHighResolution((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
            }
            SetCommResult(true);
            byte counter = packet.Content[0];
            if (counter == 0) {
                if (HighResCounter == 0) {
                    if (isPowerUpZero()) {
                        DoZero();
                    }
                } else {
                    if (isAutoRestoreZeroOffset()) {
                        // sensor power lost, and auto restore is on, try do restore
                        SetZeroOffset(false, Values.getZeroOffset());
                    }
                }
            } else {
                boolean stable = DigitalSensorValues.isStableMark(packet.Content[1]);
                if (!stable && skipUnStable) {
                    // skip unstable values
                } else {
                    HighResCounter = counter;
                    Values.setGrossWeightStr(new String(packet.Content, 2, 8));
                    Values.setHighGross(ByteHelper.bytesToFloat(packet.Content, 10, 4));
                    Values.setZeroOffset(ByteHelper.bytesToFloat(packet.Content, 14, 4));
                    Values.CheckStatus(packet.Content[1], Params.getCapacity(), Params.getIncrement());
                    if (Passenger.getMaterial().getAPW() > 0) {
                        // apw from passenger will replace local apw
                        Values.setAPW(Passenger.getMaterial().getAPW());
                        setCountInAccuracy(Math.abs(1 - Values.getPieceCountAccuracy()) <= Passenger.getMaterial().getTolerance());
                        TryNotifyListener();
                    } else {
                        setCountInAccuracy(true);
                    }
                    UpdateELabel();
                }
            }
            //log.debug("#{} UpdateHighResolution2 Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }


    DigitalSensorValues.EStatus LatsNotifyState = DigitalSensorValues.EStatus.Unknow;
    int LastNotifyPCS = -999999;
    double LastNotifyWeight = -999999;

    private void TryNotifyListener() {
        if (LatsNotifyState != Values.getStatus())
        {
            if (getGroup().getManager().getSensorListener().onSensorStateChanged(this)) {
                LatsNotifyState = Values.getStatus();
            }
        }
        if (LastNotifyWeight != Values.getNetWeight().doubleValue()) {
            if (getGroup().getManager().getSensorListener().onWeightChanged(this)) {
                LastNotifyWeight = Values.getNetWeight().doubleValue();
            }
        }
        if (LastNotifyPCS != Values.getPieceCount()) {
            if (getGroup().getManager().getSensorListener().onPieceCountChanged(this)) {
                LastNotifyPCS = Values.getPieceCount();
            }
        }
    }

    private String LastPartNumber;
    private String LastPartName;
    private String LastBinNo;
    private String LastWeight;
    private String LastPCS;

    public void UpdateELabel() throws Exception {
        if (!Params.hasELabel()) {
            return;
        }

        int status = GetELabelStatus();
        if (status == -1) {
            // not online
            return;
        }

        if ((status & DataPacket.EELabelStatusBits.Inited) == 0) {
            // not inited
            status |= DataPacket.EELabelStatusBits.Inited;
            LastPartNumber = null;
            LastPartName = null;
            LastBinNo = null;
            LastWeight = null;
            // restore enable mark
            if (Params.isEnabled()) {
                status |= DataPacket.EELabelStatusBits.Enabled;
            }
        } else {
            // inited
            // get enable mark
            Params.setEnabled((status & DataPacket.EELabelStatusBits.Enabled) != 0);
        }
        // update highlight mark
        if (Values.isHighlight()) {
            status |= DataPacket.EELabelStatusBits.Highlight;
        } else {
            status &= ~DataPacket.EELabelStatusBits.Highlight;
        }
        SetELabelStatus(status);
        String number;
        String name;
        String bin;
        String wgt;
        String pcs;
        if (Params.isEnabled()) {
            number = Passenger.getMaterial().getNumber();
            name = Passenger.getMaterial().getName();
            bin = getShortName();
            wgt = Values.getNetWeight() + " " + Values.getUnit();
            pcs = String.valueOf(Values.getPieceCount());
        } else {
            number = " ";
            name = " ";
            bin = getShortName();
            wgt = Values.getNetWeight() + " " + Values.getUnit();
            pcs = " ";
        }
        if (Objects.equals(LastPartNumber, number)) {
            SetELabelPartNumber(number);
            LastPartNumber = number;
        }
        if (Objects.equals(LastPartName, name)) {
            SetELabelPartName(name);
            LastPartName = name;
        }
        if (Objects.equals(LastBinNo, bin)) {
            SetELabelBinNo(bin);
            LastBinNo = bin;
        }
        if (Objects.equals(LastWeight, wgt)) {
            SetELabelWeight(wgt);
            LastWeight = wgt;
        }
        if (Objects.equals(LastPCS, pcs)) {
            SetELabelPieceCount(pcs);
            LastPCS = pcs;
        }
    }

    public void DoZero() throws Exception {
        DoZero(false);
    }

    public void DoZero(boolean save) throws Exception {
        try {
            log.info("#{} DoZero: save={}", Params.getAddress(), save);
            DataPacket packet = DataPacket.BuildDoZero((byte) Params.getAddress(), save);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getDoZeroTimeout());
            }
            SetCommResult(true);
            Values.setZeroOffset(ByteHelper.bytesToFloat(packet.Content, 0, 4));
            log.info("#{} DoZero Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void SetZeroOffset(boolean save, float offset) throws Exception {
        try {
            log.info("#{} SetZeroOffset: save={}, offset={}", Params.getAddress(), save, offset);
            DataPacket packet = DataPacket.BuildDoZero((byte) Params.getAddress(), save, offset);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getDoZeroTimeout());
            }
            SetCommResult(true);
            Values.setZeroOffset(ByteHelper.bytesToFloat(packet.Content, 0, 4));
            log.info("#{} SetZeroOffset Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public static void doAllZero(DigitalSensorDriver driver, boolean save) {
        try {
            log.info("#{} DoAllFactoryZero: save={}", DataPacket.AddressBroadcast, save);
            DataPacket packet = DataPacket.BuildDoZero(DataPacket.AddressBroadcast, save);
            synchronized (driver.getLock()) {
                driver.Write(packet);
            }
        } catch (Exception e) {
            log.warn("DoAllZero failed", e);
        }
    }

    public void DoTare() {
        SetTare(Values.getGrossWeight());
    }

    public void SetTare(BigDecimal weight) {
        Values.setTareWeight(weight);
    }

    public void SetTare(float weight) {
        Values.setHighTare(weight);
    }


    public void ClearTare() {
        Values.setHighTare(0);
    }

    public void Reference(int count) {
        log.info("#{} Reference", Params.getAddress());
        try {
            if (count <= 0) {
                count = 10;
            }
            int times = 10;
            double total = 0;
            for (int t = 0; t < times; t++) {
                total += Values.getHighNet();
                Thread.sleep(100);
            }
            Values.setAPW(total / times / count);
        } catch (Exception ex) {
            // ignore
            log.warn("#{} Reference failed", Params.getAddress(), ex);
        }
    }

    public void ClearCounting() {
        Values.setAPW(0);
    }


    public void CalibrateZero() throws Exception {
        try {
            log.info("#{} CalibrateZero", Params.getAddress());
            int point = DataPacket.ECalibrationPoint.PointZero;
            float weight = 0;
            DataPacket packet = DataPacket.BuildCalibrate((byte) Params.getAddress(), point, weight);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getCalibrateTimeout());
            }
            SetCommResult(true);
            int result = packet.Content[0];
            switch (result) {
                case DataPacket.EResult.OK: {
                    break;
                }
                default: {
                    throw new Exception("Calibrate zero failed: point=" + point + ", weight=" + weight + ", result=" + result);
                }
            }
            log.info("#{} CalibrateZero Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void CalibrateSpan(float weight) throws Exception {
        try {
            log.info("#{} CalibrateSpan: weight={}", Params.getAddress(), weight);
            int point = DataPacket.ECalibrationPoint.PointSpan;
            DataPacket packet = DataPacket.BuildCalibrate((byte) Params.getAddress(), point, weight);
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getCalibrateTimeout());
            }
            SetCommResult(true);
            int result = packet.Content[0];
            switch (result) {
                case DataPacket.EResult.OK: {
                    break;
                }
                default: {
                    throw new Exception("Calibrate span failed: point=" + point + ", weight=" + weight + ", result=" + result);
                }
            }
            log.info("#{} CalibrateSpan Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public DataPacket ReadParam(int param) throws Exception {
        return ReadParam(param, 0);
    }

    public DataPacket ReadParam(int param, int retries) throws Exception {
        DataPacket packet = DataPacket.BuildReadParam((byte) Params.getAddress(), param);
        log.info("#{} ReadParam: name={}, retries={}", packet.getAddress(), param, retries);

        long endTime = System.currentTimeMillis() + getReadTimeout();
        synchronized (Driver.getLock()) {
            do {
                try {

                    packet = Driver.WriteRead(packet, getReadTimeout(), retries);
                    if (packet.Content[0] == param) {
                        SetCommResult(true);
                        return packet;
                    }
                } catch (TimeoutException ex) {
                    // ignore
                }
            } while (System.currentTimeMillis() <= endTime);
        }
        throw new TimeoutException("Get " + param + " failed: recv param= " + packet.Content[0]);
    }

    public byte[] ReadParamAsBytes(int param, byte[] defaultValue) throws IOException {
        try {
            DataPacket packet = ReadParam(param);
            byte[] value = new byte[packet.getContentLength()];
            System.arraycopy(packet.Content, 1, value, 0, packet.getContentLength());
            log.info("#{} ReadParamAsBytes: name={}, counts={}", Params.getAddress(), param, value.length);
            return value;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadParamAsBytes failed: {}", Params.getAddress(), ex.getMessage());
            return defaultValue;
        }
    }

    public BigDecimal ReadParamAsDecimal(int param, BigDecimal defaultValue) throws IOException {
        try {
            DataPacket packet = ReadParam(param);
            float str = ByteHelper.bytesToFloat(packet.Content, 1, 4);
            BigDecimal d = BigDecimal.valueOf(str);
            log.info("#{} ReadParamAsDecimal: name={}, value={}", Params.getAddress(), param, d);
            return d;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadParamAsDecimal failed: {}", Params.getAddress(), ex.getMessage());
            return defaultValue;
        }
    }

    public float ReadParamAsFloat(int param, float defaultValue) throws IOException {
        try {
            DataPacket packet = ReadParam(param);
            float value = ByteHelper.bytesToFloat(packet.Content, 1, 4);
            log.info("#{} ReadParamAsFloat: name={}, value={}", Params.getAddress(), param, value);
            return value;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadParamAsFloat failed: {}", Params.getAddress(), ex.getMessage());
            return defaultValue;
        }
    }

    public int ReadParamAsInt(int param, int defaultValue) throws IOException {
        try {
            DataPacket packet = ReadParam(param);
            int value = ByteHelper.bytesToInt(packet.Content, 1, 4);
            log.info("#{} ReadParamAsInt: name={}, value={}", Params.getAddress(), param, value);
            return value;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadParamAsInt failed: {}", Params.getAddress(), ex.getMessage());
            return defaultValue;
        }
    }

    public String ReadParamAsString(int param, String defaultValue) throws IOException {
        return ReadParamAsString(param, defaultValue, 0);
    }

    public String ReadParamAsString(int param, String defaultValue, int retries) throws IOException {
        try {
            DataPacket packet = ReadParam(param, retries);
            String value = new String(packet.Content, 1, packet.getContentLength() - 1, Charset.forName(DataPacket.DefaultCharsetName));
            log.info("#{} ReadParamAsString: name={}, value={}", Params.getAddress(), param, value);
            return value;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadParamAsString failed: {}", Params.getAddress(), ex.getMessage());
            return defaultValue;
        }
    }

    private int WriteParam(DataPacket packet) throws Exception {
        return WriteParam(packet, 0);
    }

    private int WriteParam(DataPacket packet, int retries) throws Exception {
        try {
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getWriteParamTimeout(), retries);
            }
            SetCommResult(true);
            int result = (int) packet.Content[0];
            log.info("#{} WriteParam: result={}", Params.getAddress(), result);
            return result;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public int WriteParam(int param, int value) throws Exception {
        log.info("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam((byte) Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteParam(int param, BigDecimal value) throws Exception {
        log.info("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam((byte) Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteParam(int param, float value) throws Exception {
        log.info("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam((byte) Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteParam(int param, String value, int maxLen) throws Exception {
        log.debug("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam((byte) Params.getAddress(), param, value, maxLen);
        return WriteParam(packet);
    }

    public int WriteParam(int param, byte[] value) throws Exception {
        log.debug("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value.length);
        DataPacket packet = DataPacket.BuildWriteParam((byte) Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int GetPoint1RawCount() throws Exception {
        return ReadParamAsInt(DataPacket.EParam.PtZeroRawCount, Params.getPoint1RawCount());
    }

    public int GetPoint2RawCount() throws Exception {
        return ReadParamAsInt(DataPacket.EParam.PtSpanRawCount, Params.getPoint2RawCount());
    }

    public float GetPoint2Weight() throws Exception {
        return ReadParamAsFloat(DataPacket.EParam.PtSpanWeight, (float) Params.getPoint2Weight());
    }

    public void SetIncrement(BigDecimal value) throws Exception {
        WriteParam(DataPacket.EParam.Increment, value);
    }

    public BigDecimal GetIncrement() throws Exception {
        return ReadParamAsDecimal(DataPacket.EParam.Increment, Params.getIncrement());
    }

    public void SetCapacity(BigDecimal value) throws Exception {
        WriteParam(DataPacket.EParam.Capacity, value);
    }

    public BigDecimal GetCapacity() throws Exception {
        return ReadParamAsDecimal(DataPacket.EParam.Capacity, Params.getCapacity());
    }

    public void SetGeoFactor(double value) throws Exception {
        WriteParam(DataPacket.EParam.GeoFactor, (float) value);
    }

    public float GetGeoFactor() throws Exception {
        return ReadParamAsFloat(DataPacket.EParam.GeoFactor, (float) Params.getGeoFactor());
    }

    public void SetZeroCapture(double value) throws Exception {
        WriteParam(DataPacket.EParam.ZeroCapture, (float) value);
    }

    public double GetZeroCapture() throws Exception {
        return ReadParamAsFloat(DataPacket.EParam.ZeroCapture, (float) Params.getZeroCapture());
    }

    public void SetCreepCorrect(double value) throws Exception {
        WriteParam(DataPacket.EParam.CreepCorrect, (float) value);
    }

    public static void setAllCreepCorrect(DigitalSensorDriver driver, double value) {
        try {
            log.info("#{} SetAllCreepCorrect: value={}", DataPacket.AddressBroadcast, value);
            int param = DataPacket.EParam.CreepCorrect;
            DataPacket packet = DataPacket.BuildWriteParam(DataPacket.AddressBroadcast, param, (float) value);
            synchronized (driver.getLock()) {
                driver.Write(packet);
            }
        } catch (Exception ex) {
            log.warn("#{} SetAllCreepCorrect failed", ex);
        }
    }

    public double GetCreepCorrect() throws Exception {
        return ReadParamAsFloat(DataPacket.EParam.CreepCorrect, (float) Params.getCreepCorrect());
    }

    public void SetStableRange(double value) throws Exception {
        WriteParam(DataPacket.EParam.StableRange, (float) value);
    }

    public double GetStableRange() throws Exception {
        return ReadParamAsFloat(DataPacket.EParam.StableRange, (float) Params.getStableRange());
    }

    public void SetStableSpeed(double value) throws Exception {
        WriteParam(DataPacket.EParam.StableSpeed, (float) value);
    }

    public double GetStableSpeed() throws Exception {
        return ReadParamAsFloat(DataPacket.EParam.StableSpeed, (float) Params.getStableSpeed());
    }

    private byte[] GetFirmwareVersionBytes() throws Exception {
        return ReadParamAsBytes(DataPacket.EParam.FirmwareVersion, new byte[4]);
    }

    public String GetFirmwareVersion() throws Exception {
        try {
            log.info("#{} GetFirmwareVersion", Params.getAddress());
            byte[] value = GetFirmwareVersionBytes();
            String d = value[0] + ":D2." + value[1] + ":D2." + value[2] + ":D2." + value[3] + ":D3";
            log.info("#{} GetFirmwareVersion: value={}", Params.getAddress(), d);
            return d;
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }


    public void SetPCBASn(String value) throws Exception {
        WriteParam(DataPacket.EParam.PCBASn, value, 16);
    }

    public String GetPCBASn() throws Exception {
        return ReadParamAsString(DataPacket.EParam.PCBASn, Params.getPCBASn());
    }

    public void SetDeviceSn(String value) throws Exception {
        WriteParam(DataPacket.EParam.DeviceSn, value, 16);
    }

    public String GetDeviceSn() throws Exception {
        return GetDeviceSn(0);
    }

    public String GetDeviceSn(int retries) throws Exception {
        return ReadParamAsString(DataPacket.EParam.DeviceSn, Params.getDeviceSn(), retries);
    }

    public void SetDeviceModel(String value) throws Exception {
        WriteParam(DataPacket.EParam.DeviceModel, value, 16);
    }


    public String GetDeviceModel() throws Exception {
        return ReadParamAsString(DataPacket.EParam.DeviceModel, Params.getDeviceModel());
    }


    public void SetELabelPartNumber(String value) throws Exception {
        WriteELabelPartNumber(value);
    }

    public void SetELabelPartName(String value) throws Exception {
        WriteELabelPartName(value);
    }

    public void SetELabelWeight(String value) throws Exception {
        WriteELabelWeight(value);
    }

    public void SetELabelPieceCount(String value) throws Exception {
        WriteELabelPCS(value);
    }

    public void SetELabelBinNo(String value) throws Exception {
        WriteELabelBinNo(value);
//        if (TextUtils.isTrimedEmpty(value))
//            writeELabelDefaultLogo();
//        else
//            writeELabelBarcode(value);
    }

    public void SetELabelStatus(int value) throws Exception {
        WriteELabelStatus(value);
    }

    public int GetELabelStatus() throws Exception {
        return ReadELabelStatus();
    }


    public DataPacket OperateELabel(byte cmd, byte page, byte totalPage, byte[] data) throws Exception {
        int address = Params.getAddress() + DataPacket.AddressELabelStart;
        int dataLen = 0;
        if (data != null) {
            dataLen = data.length;
        }
        DataPacket packet = DataPacket.BuildELabelCmd((byte) address, cmd, page, totalPage, data);
        log.info("#{} OperateELabel: cmd={}, page={}, totalPage={}, dataLen={}", packet.getAddress(), cmd, page, totalPage, dataLen);

        long endTime = System.currentTimeMillis() + getReadTimeout();
        synchronized (Driver.getLock()) {
            Driver.Write(packet);
            do {
                packet = Driver.Read(packet.getAddress(), DataPacket.ERecvCmd.ELabel, getReadTimeout());
                if (packet.Content[1] == (byte) cmd) {
                    SetCommResult(true);
                    return packet;
                }
            } while (System.currentTimeMillis() <= endTime);
        }
        throw new TimeoutException("OperateELabel failed");
    }

    private int ReadELabelAsInt(byte cmd, byte page, byte totalPage) throws Exception {
        try {
            DataPacket packet = OperateELabel(cmd, page, totalPage, null);
            log.info("#{} ReadELabelAsInt: cmd={}, counts={}", Params.getAddress(), cmd, (packet.getContentLength() - 1));
            return ByteHelper.bytesToInt(packet.Content, 3, 4);
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            // read error used default
            throw ex;
        }
    }

    public int ReadELabelStatus() throws Exception {
        return ReadELabelAsInt((byte) DataPacket.EELabelCmdID.ReadStatus, (byte) 0, (byte) 1);
    }

    public void WriteELabelStatus(int status) throws Exception {
        try {
            DataPacket packet = OperateELabel((byte) DataPacket.EELabelCmdID.WriteStatus, (byte) 0, (byte) 1, ByteHelper.intToBytes(status));
            byte result = packet.Content[0];
            log.debug("#{} WriteELabelStatus: status={}, result={}", Params.getAddress(), String.format("%x", status), result);
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            // read error used defaultthrow ex;
        }
    }

    void WriteELabelString(byte cmd, byte page, byte totalPage, int color, String str) throws Exception {
        try {
            if (str == null) {
                str = "";
            }
            byte[] bts = Charset.forName(DataPacket.DefaultCharsetName).encode(str).array();
            byte[] content = new byte[1 + 4 + bts.length];
            content[0] = (byte) DataPacket.EELabelPalette.Bpp16;
            ByteHelper.intToBytes(color, content, 1);
            System.arraycopy(bts, 0, content, 5, bts.length);
            DataPacket packet = OperateELabel(cmd, page, totalPage, content);
            byte result = packet.Content[0];
            log.info("#{} WriteELabelString: str={}, result={}", Params.getAddress(), str, result);
            if (result != DataPacket.EResult.OK) {
                throw new Exception("WriteELabelString Failed");
            }
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            // read error used default
            throw ex;
        }
    }

    public void WriteELabelPartNumber(String value) throws Exception {
        WriteELabelString((byte) DataPacket.EELabelCmdID.WritePartNumber, (byte) 0, (byte) 1, DataPacket.EELabelColor.White, value);
    }

    public void WriteELabelPartName(String value) throws Exception {
        WriteELabelString((byte) DataPacket.EELabelCmdID.WritePartName, (byte) 0, (byte) 1, DataPacket.EELabelColor.White, value);
    }

    public void WriteELabelWeight(String value) throws Exception {
        WriteELabelString((byte) DataPacket.EELabelCmdID.WriteWeight, (byte) 0, (byte) 1, DataPacket.EELabelColor.Black, value);
    }

    public void WriteELabelPCS(String value) throws Exception {
        WriteELabelString((byte) DataPacket.EELabelCmdID.WritePCS, (byte) 0, (byte) 1, isCountInAccuracy() ? DataPacket.EELabelColor.Black : DataPacket.EELabelColor.Red, value);
    }

    public void WriteELabelBinNo(String value) throws Exception {
        WriteELabelString((byte) DataPacket.EELabelCmdID.WriteBinNo, (byte) 0, (byte) 1, DataPacket.EELabelColor.Black, value);
    }

    public void UpdateParams() throws Exception {
        try {
            log.info("#{} UpdateParams", Params.getAddress());
            synchronized (Driver.getLock()) {
                Params.setPoint1RawCount(GetPoint1RawCount());
                Params.setPoint2RawCount(GetPoint2RawCount());
                Params.setPoint2Weight(GetPoint2Weight());
                Params.setCapacity(GetCapacity());
                Params.setGeoFactor(GetGeoFactor());
                Params.setIncrement(GetIncrement());
                Params.setZeroCapture(GetZeroCapture());
                Params.setCreepCorrect(GetCreepCorrect());
                Params.setStableRange(GetStableRange());

                Params.setFirmwareVersion(GetFirmwareVersion());
                Params.setPCBASn(GetPCBASn());
                Params.setDeviceSn(GetDeviceSn());
                Params.setDeviceModel(GetDeviceModel());
            }
            SetCommResult(true);
            log.info("#{} UpdateParams Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void Unlock() throws Exception {
        WriteParam(DataPacket.EParam.Locker, 20200505);
    }
}
