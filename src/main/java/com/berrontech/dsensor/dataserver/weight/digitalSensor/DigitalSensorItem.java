package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import com.berrontech.dsensor.dataserver.common.util.QrCodeUtil;
import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * Create By Lastnika
 * Create Time: 2020/7/2 14:59
 * Class Name: DigitalSensorItem
 * Author: Lastnika
 * Description:
 * DigitalSensorItem
 *
 * @author Lastnika
 */
@Slf4j
@Data
public class DigitalSensorItem {
    public DigitalSensorDriver Driver;
    public DigitalSensorGroup Group;

    public DigitalSensorManager getSensorMgr() {
        return Group.getManager();
    }

    public String SubGroup = "";
    public int SubGroupPosition = 0;
    public String Cluster = "";

    public boolean isSlotChild() {
        return SubGroupPosition > 0;
    }

    public boolean isSlotZombieChild() {
        return SubGroupPosition > 1;
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

    public int getEmptyFlashTimeout() {
        return getReadTimeout() + 10000;
    }

    public int getUpgradeReadTimeout() {
        return getReadTimeout() + 1000;
    }


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
    private boolean Online = false;
    private int HighResCounter;
    private boolean CountInAccuracy = true;
    private long HighlightDeadTime = 0;


    public boolean isOnline() {
        return Online && (getTotalSuccess() > 0 && getContinueErrors() < 2);
    }

    public boolean IsCountOutOfAccuracy() {
        return !isCountInAccuracy();
    }

    public enum EFlatStatus {
        Offline,
        Disabled,
        Normal,
        Underload,
        Overload,
    }

    public EFlatStatus getFlatStatus() {
        if (!isOnline()) {
            return EFlatStatus.Offline;
        } else if (Params.isDisabled()) {
            return EFlatStatus.Disabled;
        } else if (Values.isUnderLoad()) {
            return EFlatStatus.Underload;
        } else if (Values.isOverLoad()) {
            return EFlatStatus.Overload;
        } else {
            return EFlatStatus.Normal;
        }
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
                getDriver().WriteRead(packet, getWriteParamTimeout());
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
                Driver.WriteRead(packet, getWriteParamTimeout());
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
                Driver.WriteRead(packet, getWriteParamTimeout());
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
                Driver.WriteRead(packet, getWriteParamTimeout());
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

    public static boolean TryReadDeviceSnBroadcast(DigitalSensorDriver driver, Map<String, Object> devInfo, int timeout) {
        log.debug("#{} TryReadDeviceSnBroadcast", DataPacket.AddressConditionalBroadcast);
        int type;
        String sn;
        synchronized (driver.getLock()) {
            long endTime = System.currentTimeMillis() + timeout;
            do {
                try {
                    long t = endTime - System.currentTimeMillis();
                    DataPacket packet = driver.Read((int) t);
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
            if (type == DataPacket.EDeviceType.ELabel) {
                newAddress = (byte) (DataPacket.AddressELabelStart + Params.getAddress());
            } else {
                newAddress = (byte) Params.getAddress();
            }
            DataPacket packet = DataPacket.BuildSetAddressByDeviceSn(newAddress, sn);
            synchronized (Driver.getLock()) {
                Driver.WriteRead(packet, getWriteParamTimeout());
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
            DataPacket packet = DataPacket.BuildGetRawCount((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
            }
            SetCommResult(true);
            Values.setDynamicRawCount(ByteHelper.bytesToInt(packet.Content, 0, 4));
            Values.setRawCount(ByteHelper.bytesToInt(packet.Content, 4, 4));
            Values.setRamp(Params.calcRamp(Values.getRawCount()));
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void UpdateWeight() throws Exception {
        try {
            DataPacket packet = DataPacket.BuildGetWeight((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout());
            }
            SetCommResult(true);
            Values.setGrossWeightStr(new String(packet.Content, 1, packet.Content.length - 1));
            Values.CheckStatus(packet.Content[0], Params.getCapacity(), Params.getIncrement());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void UpdateHighResolution(boolean skipUnStable) throws Exception {
        try {
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

    public boolean UpdateHighResolution2(boolean skipUnStable) throws Exception {
        try {
//            log.debug("#{} UpdateHighResolution2", Params.getAddress());
//            long ticks = System.currentTimeMillis();
            DataPacket packet = DataPacket.BuildGetHighResolution((byte) Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
            }
//            ticks = System.currentTimeMillis() - ticks;
//            log.debug("#{} UpdateHighResolution2 done, usedMs={}", Params.getAddress(), ticks);
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
                return false;
            } else {
                boolean stable = DigitalSensorValues.isStableMark(packet.Content[1]);
                if (!stable && skipUnStable) {
                    // skip unstable values
                    return false;
                } else {
                    HighResCounter = counter;
                    Values.setGrossWeightStr(new String(packet.Content, 2, 8));
                    Values.setHighGross(ByteHelper.bytesToFloat(packet.Content, 10, 4));
                    Values.setZeroOffset(ByteHelper.bytesToFloat(packet.Content, 14, 4));
                    Values.CheckStatus(packet.Content[1], Params.getCapacity(), Params.getIncrement());
                    if (Passenger.getMaterial().getAPW() > 0) {
                        // apw from passenger will replace local apw
                        Values.setAPW(Passenger.getMaterial().getAPW());
                        // here SF set tolerance as gram not percent
                        setCountInAccuracy(calcCountAccuracy(Passenger.getMaterial().getTolerance(), Values));
                    } else {
                        setCountInAccuracy(true);
                    }
                    TryNotifyListener();
                    return true;
                }
            }
            //log.debug("#{} UpdateHighResolution2 Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    protected static boolean calcCountAccuracy(double tolerance, DigitalSensorValues values) {
        int pcs = values.getPieceCount();
        double apw = values.getAPW();
        double apwTolerance = apw * tolerance;
        double totalTol = apwTolerance * pcs;
        double resultTol = Math.min(apw / 2, totalTol);
        double error = Math.abs(values.getHighNet() - apw * pcs);
        return tolerance <= 0 || error < resultTol;
    }

    EFlatStatus LatsNotifyStatus = null;
    int LastNotifyPCS = -999999;
    boolean LastNotifyAccuracy = false;
    double LastNotifyPCSWeight = 0;
    double LastNotifyWeight = -999999;
    long LastSaveTicks = 0;

    public void TryNotifyListener() {
        if (LastSaveTicks <= 0) {
            LastSaveTicks = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - LastSaveTicks >= 5 * 60 * 1000) {
            // try save zero point every seconds
            LastSaveTicks = System.currentTimeMillis();
            getGroup().getManager().getSensorListener().onNotifySaveZeroOffset(this);
        }
        if (isSlotChild()) {
            // does not notify if it is a child slot
            return;
        }
        if (TextUtils.isTrimedEmpty(getSubGroup())) {
            // does not notify is slot is empty
            return;
        }
        if (LatsNotifyStatus != getFlatStatus()) {
            switch (getFlatStatus()) {
                case Offline:
                case Disabled:
                case Normal: {
                    if (getGroup().getManager().getSensorListener().onSensorStateChanged(this)) {
                        LatsNotifyStatus = getFlatStatus();
                    }
                    break;
                }
                default: {
                    // do not notify these status
                    break;
                }
            }
        }
        if (LastNotifyWeight != Values.getNetWeight().doubleValue()) {
            if (getGroup().getManager().getSensorListener().onWeightChanged(this)) {
                LastNotifyWeight = Values.getNetWeight().doubleValue();
            }
        }
        if (LastNotifyPCS != Values.getPieceCount() || isAccuracyChangedAndBigEnough()) {
            if (getGroup().getManager().getSensorListener().onPieceCountChanged(this)) {
                LastNotifyPCS = Values.getPieceCount();
                LastNotifyAccuracy = isCountInAccuracy();
                LastNotifyPCSWeight = Values.getHighNet();
            }
        }
    }

    private boolean isAccuracyChangedAndBigEnough() {
        return LastNotifyAccuracy != isCountInAccuracy() &&
                Math.abs(LastNotifyPCSWeight - Values.getHighNet()) > Params.getIncrement().floatValue() * 2;
    }


    private String LastPartNumber;
    private String LastPartName;
    private String LastBinNo;
    private String LastWeight;
    private String LastPCS;
    private boolean LastAccuracy;

    public void UpdateELabel(String number, String name, String bin, String wgt, String pcs) throws Exception {
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
            if (Values.isHighlight()) {
                status |= DataPacket.EELabelStatusBits.Highlight;
            }
            SetELabelStatus(status);
        } else {
            if (isSlotZombieChild()) {
                // always enable zombie slot
                int newStatus = status | DataPacket.EELabelStatusBits.Enabled;
                if (newStatus != status) {
                    log.debug("#{} Slot({}) is zombie, reset to enable", Params.getAddress(), getSubGroup());
                    SetELabelStatus(newStatus);
                }
            } else {
                // inited
                // get enable mark
                Params.setEnabled((status & DataPacket.EELabelStatusBits.Enabled) != 0);
            }
        }

        if (IsHighlighting()) {
            if (IsHighlightTimeout()) {
                DeHighlight();
            }
        } else {
            if ((status | DataPacket.EELabelStatusBits.Highlight) != 0  // ELabel is highlighting
                    && Values.isNotHighlight()) // buffered status is not highlight
            {
                // de highlight if previous operations failed
                DeHighlight();
            }
        }

        if (!Objects.equals(LastPartNumber, number)) {
            if (TextUtils.isTrimedEmpty(number)) {
                SetELabelPartNumber(number);
            } else {
                SetELabelPartNumber("SKU:" + number);  // add prefix: SKU
            }
            LastPartNumber = number;
        }
        if (!Objects.equals(LastPartName, name)) {
            SetELabelPartName(name);
            LastPartName = name;
        }
        if (!Objects.equals(LastBinNo, bin)) {
            SetELabelBinNo(bin);
            LastBinNo = bin;
        }
        if (!Objects.equals(LastWeight, wgt)) {
            SetELabelWeight(wgt);
            LastWeight = wgt;
        }
        if (!Objects.equals(LastPCS, pcs) || LastAccuracy != isCountInAccuracy()) {
            SetELabelPieceCount(pcs);
            LastPCS = pcs;
            LastAccuracy = isCountInAccuracy();
        }
    }

    public void UpdateELabel() throws Exception {
        String number;
        String name;
        String bin;
        String wgt;
        String pcs;
        if (Params.isEnabled()) {
            number = Passenger.getMaterial().getNumber();
            name = Passenger.getMaterial().getName();
            bin = getSubGroup();
            wgt = Values.getNetWeight() + " " + Values.getUnit();
            pcs = String.valueOf(Values.getPieceCount());
        } else {
            number = null;
            name = null;
            bin = getSubGroup();
            wgt = Values.getNetWeight() + " " + Values.getUnit();
            pcs = null;
        }

        if (!isSlotChild()) {
            UpdateELabel(number, name, bin, wgt, pcs);
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

    public static void SetAllCreepCorrect(DigitalSensorDriver driver, double value) {
        try {
            log.info("#{} SetAllCreepCorrect: value={}", DataPacket.AddressBroadcast, value);
            int param = DataPacket.EParam.CreepCorrect;
            DataPacket packet = DataPacket.BuildWriteParam(DataPacket.AddressBroadcast, param, (float) value);
            synchronized (driver.getLock()) {
                driver.Write(packet);
            }
        } catch (Exception ex) {
            log.warn("SetAllCreepCorrect failed", ex);
        }
    }

    public static void SetAllZeroCapture(DigitalSensorDriver driver, double value) {
        try {
            log.info("#{} SetAllZeroCapture: value={}", DataPacket.AddressBroadcast, value);
            int param = DataPacket.EParam.ZeroCapture;
            DataPacket packet = DataPacket.BuildWriteParam(DataPacket.AddressBroadcast, param, (float) value);
            synchronized (driver.getLock()) {
                driver.Write(packet);
            }
        } catch (Exception ex) {
            log.warn("SetAllZeroCapture failed", ex);
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
        switch (Params.getELabelModel()) {
            case DigitalSensorParams.EELabelModel.V3:
            default: {
                WriteELabelBinNo(value);
                if (TextUtils.isTrimedEmpty(value)) {
                    WriteELabelDefaultLogo();
                } else {
                    WriteELabelBarcode(value, 48);
                }
                break;
            }
            case DigitalSensorParams.EELabelModel.V4: {
                WriteELabelBinNo(value);
                if (TextUtils.isTrimedEmpty(value)) {
                    WriteELabelDefaultLogo();
                } else {
                    WriteELabelBarcode(value, 64);
                }
                break;
            }
        }
    }

    public void SetELabelStatus(int value) throws Exception {
        WriteELabelStatus(value);
    }

    public int GetELabelStatus() throws Exception {
        return ReadELabelStatus();
    }

    public void DoHighlight(long duration) {
        SetELabelHighlight(true);
        HighlightDeadTime = System.currentTimeMillis() + duration;
    }

    public void DeHighlight() {
        SetELabelHighlight(false);
        HighlightDeadTime = 0;
    }

    public boolean IsHighlighting() {
        return HighlightDeadTime > 0;
    }

    public boolean IsHighlightTimeout() {
        return IsHighlighting() && HighlightDeadTime < System.currentTimeMillis();
    }

    public void SetELabelHighlight(boolean highlight) {
        if (getParams().hasELabel()) {
            try {
                int status = GetELabelStatus();
                if (status == -1) {
                    return;
                }
                int newStatus = status;
                if (highlight) {
                    newStatus |= (int) DataPacket.EELabelStatusBits.Highlight;
                } else {
                    newStatus &= (~(int) DataPacket.EELabelStatusBits.Highlight);
                }
                if (status != newStatus) {
                    SetELabelStatus(newStatus);
                }
            } catch (Exception ex) {
                log.warn("WriteELabelHighlight failed:{}", ex.getMessage());
            }
        }
        Values.setHighlight(highlight);
    }

    public void SetEnabled(boolean enable) {
        if (getParams().hasELabel()) {
            try {
                int status = GetELabelStatus();
                if (status == -1) {
                    return;
                }
                int newStatus = status;
                if (enable) {
                    newStatus |= (int) DataPacket.EELabelStatusBits.Enabled;
                } else
                    newStatus &= (~(int) DataPacket.EELabelStatusBits.Enabled);
                if (status != newStatus) {
                    SetELabelStatus(newStatus);
                }
            } catch (Exception ex) {
                log.warn("WriteELabelEnable failed:{}", ex.getMessage());
            }
        }
        Params.setEnabled(enable);
    }

    public DataPacket OperateELabel(byte cmd, byte page, byte totalPage, byte[] data) throws Exception {
        int address = Params.getAddress() + DataPacket.AddressELabelStart;
        DataPacket packet = DataPacket.BuildELabelCmd((byte) address, cmd, page, totalPage, data);

        long endTime = System.currentTimeMillis() + getReadTimeout();
        synchronized (Driver.getLock()) {
            do {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
                if (packet.Content[1] == cmd) {
                    SetCommResult(true);
                    return packet;
                }
            } while (System.currentTimeMillis() <= endTime);
        }
        throw new TimeoutException("OperateELabel failed");
    }

    private int ReadELabelAsInt(byte cmd, byte page, byte totalPage) throws Exception {
        DataPacket packet = OperateELabel(cmd, page, totalPage, null);
        return ByteHelper.bytesToInt(packet.Content, 3, 4);
    }

    public int ReadELabelStatus() throws Exception {
        return ReadELabelAsInt((byte) DataPacket.EELabelCmdID.ReadStatus, (byte) 0, (byte) 1);
    }

    public void WriteELabelStatus(int status) throws Exception {
        OperateELabel((byte) DataPacket.EELabelCmdID.WriteStatus, (byte) 0, (byte) 1, ByteHelper.intToBytes(status));
    }


    void WriteELabelString(byte cmd, byte page, byte totalPage, int color, String str) throws Exception {
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
//        log.info("#{} WriteELabelString: str={}, result={}", Params.getAddress(), str, result);
        if (result != DataPacket.EResult.OK) {
            throw new Exception("WriteELabelString Failed");
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


    int _defaultLogoWidth = 48;
    int _defaultLogoHeight = 48;
    int[] _defaultLogo = new int[]{
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x80, 0xC0, 0xC0,
            0xE0, 0xE0, 0xF0, 0xF0, 0x70, 0x78, 0x78, 0x38, 0x38, 0x78, 0x78, 0x70, 0xF0, 0xF0, 0xE0, 0xE0,
            0xC0, 0xC0, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xC0, 0xF0, 0x38, 0x7C, 0xFE, 0xFF, 0xFF, 0xFF, 0xFF,
            0xCF, 0x0B, 0x93, 0x83, 0x47, 0xCE, 0x50, 0x48, 0x48, 0x50, 0xCC, 0x47, 0x83, 0x93, 0x0B, 0xCF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFE, 0x7C, 0x38, 0xF0, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0xF8, 0xDF, 0x47, 0x61, 0x20, 0x20, 0xE1, 0xF1, 0xF9, 0xFD, 0x07,
            0xCF, 0x39, 0xC2, 0x3C, 0xC9, 0x0A, 0x0C, 0x80, 0x80, 0x0C, 0x0A, 0xC9, 0x3C, 0xC6, 0x3B, 0xFF,
            0x07, 0xFD, 0xF9, 0xF1, 0xE1, 0x20, 0x20, 0x41, 0x47, 0xDF, 0xF8, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x24, 0x26, 0xB2, 0x9B, 0xDB, 0xFF, 0xFF, 0xFF, 0x3F, 0xDF, 0x26,
            0xDF, 0x60, 0x1F, 0xC6, 0x30, 0x0F, 0x06, 0x04, 0x04, 0x06, 0x1F, 0x30, 0xCE, 0x19, 0x64, 0x9B,
            0x26, 0xDF, 0x3F, 0xFF, 0xFF, 0xFF, 0xDB, 0x9B, 0xB2, 0x24, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x05, 0x0C, 0x36, 0x91, 0xE1, 0x70, 0xF0, 0x7B, 0xBC,
            0xF3, 0x06, 0xF8, 0x63, 0x0C, 0xF0, 0x00, 0x00, 0x00, 0x00, 0xF0, 0x0C, 0x63, 0xF8, 0x06, 0xF9,
            0x3C, 0xFB, 0xF0, 0x71, 0xE1, 0x11, 0x36, 0x0C, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x06, 0x03,
            0x08, 0x0F, 0x01, 0x1C, 0x07, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x07, 0x1C, 0x01, 0x0E, 0x08,
            0x03, 0x04, 0x03, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    };

    public void WriteELabelLogo(int color, int width, int height, int[] data) throws Exception {
        if (data.length > 1024) {
            data = Arrays.copyOfRange(data, 0, 1024);
        }
        int pageSize = 128;
        int pages = (data.length + pageSize - 1) / pageSize;
        for (int p = 0; p < pages; p++) {
            byte[] buf;
            int size = Math.min(pageSize, data.length - pageSize * p);
            if (p == 0) {
                buf = new byte[9 + size];
                buf[0] = (byte) DataPacket.EELabelPalette.Bpp16;
                ByteHelper.intToBytes(color, buf, 1);
                ByteHelper.intToBytes(width, buf, 5, 2);
                ByteHelper.intToBytes(height, buf, 7, 2);
                for (int pos = 0; pos < size; pos++) {
                    buf[9 + pos] = (byte) data[pageSize * p + pos];
                }
            } else {
                buf = new byte[size];
                for (int pos = 0; pos < size; pos++) {
                    buf[pos] = (byte) data[pageSize * p + pos];
                }
            }
            OperateELabel((byte) DataPacket.EELabelCmdID.WriteLogo, (byte) p, (byte) pages, buf);
            if (p + 1 < pages) {
                Thread.sleep(Group.getCommInterval());
            }
        }
    }

    public void WriteELabelDefaultLogo() throws Exception {
        WriteELabelLogo((0x006040), _defaultLogoWidth, _defaultLogoHeight, _defaultLogo);
    }

    public void WriteELabelBarcode(String value, int size) throws Exception {
        int width = size;
        int height = size;
        int[] data = QrCodeUtil.encodeToBits(value, width, height, 0);
        WriteELabelLogo(DataPacket.EELabelColor.Black, width, height, data);
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


    protected DataPacket UpgradeWriteRead(DataPacket packet, int timeout) throws Exception {
        byte packNo = packet.Content[0];
        DataPacket readPack = null;
        Driver.getConnection().getRecvBuffer().clear();
        Driver.Write(packet);
        long deadTime = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() <= deadTime) {
            readPack = Driver.Read(packet.getAddress(), DataPacket.ERecvCmd.Upgrade, timeout);
            if (readPack.Content[0] == packNo) {
                break;
            }
        }
        return readPack;
    }
//
//    protected boolean UpgradeQuery(byte[] version) {
//        version = null;
//
//        log.debug("#{} UpgradeQuery", Params.getAddress());
//        DataPacket packet = DataPacket.BuildUpgradeQuery((byte) Params.getAddress());
//        try {
//            packet = UpgradeWriteRead(packet, UpgradeReadTimeout);
//            SetCommResult(true);
//            result = (DataPacket.EResult) packet.Content[1];
//            version = ArrayHelper.SubArray(packet.Content, 2, 4);
//            Log.D($"UpgradeQuery: result={result}, protocol={version[0]}, version={version[1]}.{version[2]}.{version[3]}");
//            if (result == DataPacket.EResult.OK)
//                return true;
//            else
//                return false;
//        } catch (TimeoutException) {
//            return false;
//        } catch (IOException ex) {
//            // port is closed
//            throw ex;
//        } catch (Exception ex) {
//            Log.E($"UpgradeQuery", ex);
//
//            SetCommResult(false);
//            throw ex;
//        }
//    }
//
//    protected bool UpgradeStart(DataPacket.EDeviceType deviceType) {
//        Log.D($"#{Params.Address} UpgradeStart: DeviceType={deviceType}");
//        DataPacket packet = DataPacket.BuildUpgradeStart((byte) Params.Address, (byte) deviceType, 1000);
//        try {
//            packet = UpgradeWriteRead(packet, UpgradeReadTimeout);
//            SetCommResult(true);
//            DataPacket.EResult result = (DataPacket.EResult) packet.Content[1];
//            Log.D($"UpgradeStart: result={result}");
//            if (result == DataPacket.EResult.OK)
//                return true;
//            else
//                return false;
//        } catch (TimeoutException) {
//            return false;
//        } catch (IOException ex) {
//            // port is closed
//            throw ex;
//        } catch (Exception ex) {
//            Log.E($"UpgradeStart", ex);
//
//            SetCommResult(false);
//            throw ex;
//        }
//    }
//
//    protected bool UpgradeSendHead(uint flushAddress, int dataSize) {
//        int roundSize = dataSize;
//        while (roundSize % 4 != 0)
//            roundSize++;
//        DataPacket.EResult result = DataPacket.EResult.ErrUnknow;
//        Log.D($"#{Params.Address} UpgradeSendHead: address=0x{flushAddress:X8}, size={roundSize}({dataSize})");
//        DataPacket packet = DataPacket.BuildUpgradeHead((byte) Params.Address, flushAddress, roundSize);
//        try {
//            packet = UpgradeWriteRead(packet, EmptyFlashTimeout);
//            SetCommResult(true);
//            result = (DataPacket.EResult) packet.Content[1];
//            Log.D($"UpgradeSendHead: result={result}");
//            if (result == DataPacket.EResult.OK)
//                return true;
//            else
//                return false;
//        } catch (TimeoutException) {
//            return false;
//        } catch (IOException ex) {
//            // port is closed
//            throw ex;
//        } catch (Exception ex) {
//            Log.E($"UpgradeSendHead", ex);
//
//            SetCommResult(false);
//            throw ex;
//        }
//    }
//
//    protected bool UpgradeSendEnd() {
//        DataPacket.EResult result = DataPacket.EResult.ErrUnknow;
//        Log.D($"#{Params.Address} UpgradeSendEnd");
//        DataPacket packet = DataPacket.BuildUpgradeEnd((byte) Params.Address);
//        Driver.Write(packet);
//        Thread.Sleep(100);
//        Driver.Write(packet);
//        return true;
//        //try
//        //{
//        //    packet = UpgradeWriteRead(packet, UpgradeReadTimeout);
//        //    SetCommResult(true);
//        //    result = (DataPacket.EResult)packet.Content[1];
//        //    Log.D($"UpgradeSendEnd: result={result}");
//        //    if (result == DataPacket.EResult.OK)
//        //        return true;
//        //    else
//        //        return false;
//        //}
//        //catch (TimeoutException)
//        //{
//        //    return false;
//        //}
//        //catch (IOException ex)
//        //{
//        //    // port is closed
//        //    throw ex;
//        //}
//        //catch (Exception ex)
//        //{
//        //    Log.E($"UpgradeSendEnd", ex);
//
//        //    SetCommResult(false);
//        //    throw ex;
//        //}
//    }
//
//    protected bool UpgradeSendData(byte packNo, IEnumerable<byte> data) {
//        DataPacket.EResult result = DataPacket.EResult.ErrUnknow;
//        Log.D($"#{Params.Address} UpgradeSendData: packNo={packNo}, dataLen={data.Count()}");
//        DataPacket packet = DataPacket.BuildUpgradeData((byte) Params.Address, packNo, data);
//        try {
//            packet = UpgradeWriteRead(packet, WriteParamTimeout);
//            SetCommResult(true);
//            result = (DataPacket.EResult) packet.Content[1];
//            Log.D($"UpgradeSendData: result={result}");
//            if (result == DataPacket.EResult.OK)
//                return true;
//            else
//                return false;
//        } catch (TimeoutException) {
//            return false;
//        } catch (IOException ex) {
//            // port is closed
//            throw ex;
//        } catch (Exception ex) {
//            Log.E($"UpgradeSendData", ex);
//
//            SetCommResult(false);
//            throw ex;
//        }
//    }
//
//    private bool _upgrading = false;
//        [XmlIgnore]
//    public bool Upgrading
//
//    {
//        get =>_upgrading;
//        set =>SetValue(ref _upgrading, value, nameof(Upgrading));
//    }
//
//    public bool DoUpgrade(string firmware, DataPacket.EDeviceType deviceType) {
//        if (Upgrading) return false;
//
//        HexFileParser hex = new HexFileParser();
//        List<HexDataBlockInfo> blocks = new List<HexDataBlockInfo>();
//        try {
//            hex.OpenFile(firmware);
//            blocks = hex.ReadToDataBlocks();
//        } finally {
//            hex.CloseFile();
//        }
//
//        lock(Driver.Locker)
//        {
//            try {
//                Upgrading = true;
//                DigitalSensorItem sensor = this;
//                // check status
//                if (!sensor.UpgradeQuery(out var result, out var version)) {
//                    try {
//                        Log.D("Try start device boot");
//                        // unlock first
//                        sensor.Unlock();
//                        // start to bootloader
//                        if (sensor.UpgradeStart(deviceType))
//                            Log.D("Call boot done");
//                        else
//                            Log.D("Call boot failed, try upgrading directly");
//                    } catch (TimeoutException) {
//                        // ignore
//                        Log.D("Start boot failed, try upgrading directly");
//                    }
//                }
//                // build default sensor for upgrading
//                sensor = NewDefaultSensor(Driver, Group);
//                // handshake
//                while (Upgrading) {
//                    if (sensor.UpgradeQuery(out result, out version)) {
//                        break;
//                    }
//                }
//                bool hasError = false;
//                // send blocks
//                foreach(var block in blocks)
//                {
//                    if (!Upgrading) break;
//
//                    // send head
//                    if (sensor.UpgradeSendHead(block.Address, block.Data.Count)) {
//                        byte packNo = (byte) DataPacket.EUpgradePackNo.DataHead;
//                        int offset = 0;
//                        int size = 128;
//                        while (Upgrading) {
//                            var data = ListHelper.SubList(block.Data, offset, size);
//                            if (data.Count == 0)
//                                break;  // no more data
//                            while (data.Count % 4 != 0)
//                                data.Add(0xFF); // padding to 4 multiple
//                            Log.D($"Downloading prograss {offset * 100 / block.Data.Count}%");
//                            if (sensor.UpgradeSendData(packNo, data)) {
//                                offset += data.Count;
//                                packNo++;
//                                if (packNo > (byte) DataPacket.EUpgradePackNo.DataEnd)
//                                    packNo = (byte) DataPacket.EUpgradePackNo.DataHead;
//                            }
//                        }
//                    } else {
//                        hasError = true;
//                    }
//                }
//                if (!Upgrading) return false;
//                if (hasError) {
//                    Log.W("Upgrading Failed");
//                    return false;
//                } else {
//                    // send end
//                    if (sensor.UpgradeSendEnd()) {
//                        // finish upgrading
//                    }
//                    Log.N("Upgrading Finished");
//                    return true;
//                }
//            } finally {
//                Upgrading = false;
//            }
//        }
//    }
//
//    public void AbortUpgrading() {
//        Upgrading = false;
//    }

}
