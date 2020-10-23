package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import com.berrontech.dsensor.dataserver.common.util.CollectionUtils;
import com.berrontech.dsensor.dataserver.common.util.QrCodeUtil;
import com.berrontech.dsensor.dataserver.common.util.TextUtils;
import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
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

    public int SubGroupId = 0;
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


    private long TotalErrors;
    private long TotalSuccess;
    private int ContinueErrors;
    public static final int OfflineContinueErrorThreshold = 10;
    private long ELabelTotalErrors;
    private long ELabelTotalSuccess;
    private int ELabelContinueErrors;
    private boolean Online = false;
    private boolean ELabelOnline = false;
    private int HighResCounter;
    private boolean CountInAccuracy = true;
    private long HighlightDeadTime = 0;

    public boolean setOnlineAndNotify(boolean value) {
        if (Online != value) {
            Online = value;
            TryNotifyListener();
            return true;
        }
        return false;
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

    public static EFlatStatus toFlatStatus(boolean online, boolean disable, DigitalSensorValues.EStatus status) {
        if (!online) {
            return EFlatStatus.Offline;
        } else if (disable) {
            return EFlatStatus.Disabled;
        } else if (status == DigitalSensorValues.EStatus.UnderLoad) {
            return EFlatStatus.Underload;
        } else if (status == DigitalSensorValues.EStatus.OverLoad) {
            return EFlatStatus.Overload;
        } else {
            return EFlatStatus.Normal;
        }
    }

    public EFlatStatus getFlatStatus() {
        return toFlatStatus(isOnline(), Params.isDisabled(), Values.getStatus());
    }

    private void SetCommResult(boolean ok) {
        if (ok) {
            if (setOnlineAndNotify(true)) {
                // status from offline -> online
                CheckSensorSn();
            }
            TotalSuccess++;
            ContinueErrors = 0;
        } else {
            TotalErrors++;
            ContinueErrors++;
            if (ContinueErrors > OfflineContinueErrorThreshold) {
                setOnlineAndNotify(false);
            }
        }
    }

    private void SetELabelCommResult(boolean ok) {
        if (ok) {
            if (!isELabelOnline()) {
                // status from offline -> online
                setELabelOnline(true);
                CheckELabelSn();
            }
            ELabelTotalSuccess++;
            ELabelContinueErrors = 0;
        } else {
            ELabelTotalErrors++;
            ELabelContinueErrors++;
            if (ContinueErrors > OfflineContinueErrorThreshold) {
                setELabelOnline(false);
            }
        }
    }

    private void CheckSensorSn() {
        try {
            String sn = GetDeviceSn("", 1);
            if (DigitalSensorParams.IsSnInRule(sn)) {
                if (!Objects.equals(sn, Params.getBackupSensorSn())) {
                    if (getGroup().getManager().getSensorListener().onNotifySensorSnChanged(this, sn)) {
                        Params.setBackupSensorSn(sn);
                    }
                }
            } else {
                log.warn("CheckSensorSn: incorrect sn={}", sn);
            }
        } catch (Exception ex) {
            log.warn("CheckSensorSn", ex);
        }
    }

    private void CheckELabelSn() {
        try {
            String sn = GetELabelDeviceSn("", 1);
            if (DigitalSensorParams.IsSnInRule(sn)) {
                if (!Objects.equals(sn, Params.getBackupELabelSn())) {
                    if (getGroup().getManager().getSensorListener().onNotifyELabelSnChanged(this, sn)) {
                        Params.setBackupELabelSn(sn);
                    }
                }
            } else {
                log.warn("CheckELabelSn: incorrect sn={}", sn);
            }
        } catch (Exception ex) {
            log.warn("CheckELabelSn", ex);
        }
    }

    public static DigitalSensorItem NewSensor(int address, DigitalSensorDriver driver, DigitalSensorGroup group) {
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
            DataPacket packet = DataPacket.BuildSetAddress(DataPacket.AddressDefault, Params.getAddress());
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
            int labelAddr = Params.getELabelAddress();
            log.info("#{} SetELabelAddress: {}", Params.getAddress(), labelAddr);
            DataPacket packet = DataPacket.BuildSetAddress(DataPacket.AddressDefault, labelAddr);
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

    public void ModifyAddress(int newAddress) throws Exception {
        try {
            log.info("#{} ModifyAddress: {}", Params.getAddress(), newAddress);
            DataPacket packet = DataPacket.BuildSetAddress(Params.getAddress(), newAddress);
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
            DataPacket packet = DataPacket.BuildSetAddress(Params.getAddress(), DataPacket.AddressDefault);
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
                        int tp = packet.Content[0] & 0xFF;
                        int pm = packet.Content[1] & 0xFF;
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
            int newAddress;
            if (type == DataPacket.EDeviceType.ELabel) {
                newAddress = Params.getELabelAddress();
            } else {
                newAddress = Params.getAddress();
            }
            Group.SetAddressByDeviceSn(newAddress, sn);
            SetCommResult(true);
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }


    public void UpdateRawCount() throws Exception {
        try {
            DataPacket packet = DataPacket.BuildGetRawCount(Params.getAddress());
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
            DataPacket packet = DataPacket.BuildGetWeight(Params.getAddress());
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
            DataPacket packet = DataPacket.BuildGetHighResolution(Params.getAddress());
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
            DataPacket packet = DataPacket.BuildGetHighResolution(Params.getAddress());
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
                    float highgross = Values.getHighGross();
                    byte stableMark = packet.Content[1];
                    Values.setGrossWeightStr(new String(packet.Content, 2, 8));
                    Values.setHighGross(ByteHelper.bytesToFloat(packet.Content, 10, 4));
                    Values.setZeroOffset(ByteHelper.bytesToFloat(packet.Content, 14, 4));
                    ////// valuate stable manually //////
                    if (Math.abs(highgross - Values.getHighGross()) > Params.getIncrement().floatValue() * 2) {
                        stableMark = DigitalSensorValues.DynamicMark;
                    } else {
                        stableMark = DigitalSensorValues.StableMark;
                    }
                    /////////////////////////////////////
                    Values.CheckStatus(stableMark, Params.getCapacity(), Params.getIncrement());
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
        final float netWeight = values.getNetWeight().floatValue();
        double error = Math.abs(netWeight - apw * pcs);
        if (pcs <= 0) {
            pcs = 1;
        }
        double totalTol = apwTolerance * pcs;
        double resultTol = Math.min(apw / 2, totalTol);
        return tolerance <= 0 || error < resultTol;
    }

    EFlatStatus LastNotifyStatus = null;
    int LastNotifyPCS = -999999;
    boolean LastNotifyAccuracy = false;
    double LastNotifyPCSWeight = 0;
    double LastNotifyWeight = -999999;
    boolean LastNotifyStable = false;
    long LastSaveTicks = 0;
    double LastNotifyTemp = -999999;
    double LastNotifyHumi = -999999;

    private void doTryNotifyListener() {
        if (LastSaveTicks <= 0) {
            LastSaveTicks = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - LastSaveTicks >= 5 * 60 * 1000) {
            // try save zero point every seconds
            LastSaveTicks = System.currentTimeMillis();
            getGroup().getManager().getSensorListener().onNotifySaveZeroOffset(this);
        }
//        if (isSlotChild()) {
//            // does not notify if it is a child slot
//            return;
//        }
        if (TextUtils.isTrimedEmpty(getSubGroup())) {
            // does not notify is slot is empty
            return;
        }
        {
            EFlatStatus status = getFlatStatus();
            if (LastNotifyStatus != status) {
                if (getGroup().getManager().getSensorListener().onSensorStateChanged(this)) {
                    LastNotifyStatus = status;
                }
            }
        }
        if (getParams().isXSensor()) {
            if (Values.getXSensors() != null && Values.getXSensors().length > 1) {
                if (LastNotifyTemp != Values.getXSensors()[0].doubleValue() ||
                        LastNotifyHumi != Values.getXSensors()[1].doubleValue()) {
                    if (getGroup().getManager().getSensorListener().onNotifyXSensorTempHumi(this)) {
                        LastNotifyTemp = Values.getXSensors()[0].doubleValue();
                        LastNotifyHumi = Values.getXSensors()[1].doubleValue();
                    }
                }
            }
        } else {
            if (LastNotifyWeight != Values.getNetWeight().doubleValue() ||
                    LastNotifyStable != Values.isStable()) {
                if (isSlotChild() || getGroup().getManager().getSensorListener().onWeightChanged(this)) {
                    LastNotifyWeight = Values.getNetWeight().doubleValue();
                    LastNotifyStable = Values.isStable();
                }
            }
            if (LastNotifyPCS != Values.getPieceCount() || isAccuracyChangedAndBigEnough()) {
                if (isSlotChild() || getGroup().getManager().getSensorListener().onPieceCountChanged(this)) {
                    log.debug("#{} Notified piece count changed (last/new): PCS={}/{}, Accuracy={}/{}, PCSWgt={}/{}, APW={}, Tol={}, TolWgt={}",
                            Params.getAddress(),
                            LastNotifyPCS, Values.getPieceCount(),
                            LastNotifyAccuracy, isCountInAccuracy(),
                            LastNotifyPCSWeight, Values.getHighNet(),
                            Values.getAPW(),
                            getPassenger().getMaterial().getTolerance(), Values.getAPW() * getPassenger().getMaterial().getTolerance());
                    LastNotifyPCS = Values.getPieceCount();
                    LastNotifyAccuracy = isCountInAccuracy();
                    LastNotifyPCSWeight = Values.getHighNet();
                }
            }
        }
    }

    void TryNotifyListener() {
        try {
            doTryNotifyListener();
        } catch (Throwable e) {
            log.error("Error on TryNotifyListener!", e);
        }
    }

    private boolean isAccuracyChangedAndBigEnough() {
        if (LastNotifyAccuracy) {
            // hard to enter not accuracy status
            return !isCountInAccuracy() &&
                    Math.abs(LastNotifyPCSWeight - Values.getHighNet()) > Params.getIncrement().floatValue() * 2;
        } else {
            // easy to fall in accuracy status
            return isCountInAccuracy();
        }
    }


    private String LastPartNumber;
    private String LastPartName;
    private String LastBinNo;
    private String LastWeight;
    private String LastPCS;
    private boolean LastAccuracy;

    public void UpdateELabel(String number, String name, String bin, String wgt, String pcs, boolean inAccuracy) throws Exception {
        if (!Params.hasELabel()) {
            return;
        }

        try {
            int status = GetELabelStatus();
            if (status == -1) {
                // not online
                return;
            }
            SetELabelCommResult(true);

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
                SetELabelCommResult(true);
            } else {
                if (isSlotZombieChild()) {
                    // always enable zombie slot
                    int newStatus = status | DataPacket.EELabelStatusBits.Enabled;
                    // clear long press status
                    newStatus &= (~DataPacket.EELabelStatusBits.LongPressedMark);
                    if (newStatus != status) {
                        log.debug("#{} Slot({}) is zombie, reset to enable", Params.getAddress(), getSubGroup());
                        SetELabelStatus(newStatus);
                        SetELabelCommResult(true);
                    }
                } else {
                    // inited
                    // get enable mark
                    Params.setEnabled((status & DataPacket.EELabelStatusBits.Enabled) != 0);
                    if ((status & DataPacket.EELabelStatusBits.LongPressedMark) != 0) {
                        // long pressed
                        // clear long pressed mark
                        log.info("ELabel long press, address=[{}]", getParams().getAddress());
                        if (SetLongPressedMark(false)) {
                            // do zero after mark is cleared
                            DoZero(true);
                        }
                    }
                }
            }

            if (IsHighlighting()) {
                if (IsHighlightTimeout()) {
                    DeHighlight();
                    SetELabelCommResult(true);
                }
            } else {
                if ((status | DataPacket.EELabelStatusBits.Highlight) != 0  // ELabel is highlighting
                        && Values.isNotHighlight()) // buffered status is not highlight
                {
                    // de highlight if previous operations failed
                    DeHighlight();
                    SetELabelCommResult(true);
                }
            }

            if (!Objects.equals(LastPartNumber, number)) {
                if (TextUtils.isTrimedEmpty(number)) {
                    SetELabelPartNumber(number);
                } else {
                    SetELabelPartNumber("SKU:" + number);  // add prefix: SKU
                }
                LastPartNumber = number;
                SetELabelCommResult(true);
            }
            if (!Objects.equals(LastPartName, name)) {
                SetELabelPartName(name);
                LastPartName = name;
                SetELabelCommResult(true);
            }
            if (!Objects.equals(LastBinNo, bin)) {
                SetELabelBinNo(bin);
                LastBinNo = bin;
                SetELabelCommResult(true);
            }
            if (!Objects.equals(LastWeight, wgt)) {
                SetELabelWeight(wgt);
                LastWeight = wgt;
                SetELabelCommResult(true);
            }
            if (!Objects.equals(LastPCS, pcs) || LastAccuracy != inAccuracy) {
                SetELabelPieceCount(pcs, inAccuracy);
                LastPCS = pcs;
                LastAccuracy = inAccuracy;
                SetELabelCommResult(true);
            }
//            String pcs2 = String.format("%d", LastNotifyPCS);
//            if (!Objects.equals(LastPCS, pcs2) || LastAccuracy != LastNotifyAccuracy) {
//                // use notify accuracy
//                SetELabelPieceCount(pcs2);
//                LastPCS = pcs2;
//                LastAccuracy = LastNotifyAccuracy;
//                SetELabelCommResult(true);
//            }
        } catch (Exception ex) {
            SetELabelCommResult(false);
            throw ex;
        }
    }

    public void UpdateELabel() throws Exception {
        String number;
        String name;
        String bin;
        String wgt;
        String pcs;
        boolean acc;
        if (Params.isEnabled()) {
            number = Passenger.getMaterial().getNumber();
            name = Passenger.getMaterial().getName();
            bin = getSubGroup();
            wgt = Values.getNetWeight() + " " + Values.getUnit();
            //pcs = String.valueOf(Values.getPieceCount());
            pcs = String.format("%d", LastNotifyPCS);
            acc = LastAccuracy;
        } else {
            number = null;
            name = null;
            bin = getSubGroup();
            wgt = Values.getNetWeight() + " " + Values.getUnit();
            pcs = null;
            acc = true;
        }

        if (!isSlotChild()) {
            UpdateELabel(number, name, bin, wgt, pcs, acc);
        }
    }


    public boolean UpdateXSensors() {
        try {
            //Log.D($"#{Params.Address} UpdateXSensors");
            DataPacket packet = DataPacket.BuildGetXSensors(Params.getAddress());
            synchronized (Driver.getLock()) {
                packet = Driver.WriteRead(packet, getReadTimeout(), 1);
            }
            SetCommResult(true);

            //Log.D($"#{Params.Address} Updated XSensors");
            for (int pos = 0; pos < packet.getContentLength(); pos += 4) {
                float str = ByteHelper.bytesToFloat(packet.Content, pos, 4);
                val v = BigDecimal.valueOf(str);
                int idx = pos / 4;
                if (idx < Values.getXSensors().length) {
                    Values.getXSensors()[idx] = v;
                }
            }
            Values.CheckStatus(0, Params.getXSensorLowers()[0], Params.getXSensorUppers()[0]);
            Values.CheckStatus(1, Params.getXSensorLowers()[1], Params.getXSensorUppers()[1]);

            return true;
        } catch (Exception ex) {
            log.warn("#{} Update XSensors failed", Params.getAddress(), ex);
            SetCommResult(false);
            return false;
        }
    }


    public void DoZero() throws Exception {
        DoZero(false);
    }

    public void DoZero(boolean save) throws Exception {
        try {
            log.info("#{} DoZero: save={}", Params.getAddress(), save);
            DataPacket packet = DataPacket.BuildDoZero(Params.getAddress(), save);
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
            DataPacket packet = DataPacket.BuildDoZero(Params.getAddress(), save, offset);
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
            DataPacket packet = DataPacket.BuildCalibrate(Params.getAddress(), point, weight);
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
            DataPacket packet = DataPacket.BuildCalibrate(Params.getAddress(), point, weight);
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
        DataPacket packet = DataPacket.BuildReadParam(Params.getAddress(), param);
        log.info("#{} ReadParam: name={}, retries={}", packet.getAddress(), param, retries);

        long endTime = System.currentTimeMillis() + getReadTimeout();
        synchronized (Driver.getLock()) {
            do {
                try {
                    packet = Driver.WriteRead(packet, getReadTimeout(), retries);
                    if ((packet.Content[0] & 0xFF) == param) {
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

    public DataPacket ReadELabelParam(int param, int retries) throws Exception {
        DataPacket packet = DataPacket.BuildReadParam(Params.getELabelAddress(), param);
        log.info("#{} ReadELabelParam: name={}, retries={}", packet.getAddress(), param, retries);

        long endTime = System.currentTimeMillis() + getReadTimeout();
        synchronized (Driver.getLock()) {
            do {
                try {

                    packet = Driver.WriteRead(packet, getReadTimeout(), retries);
                    if ((packet.Content[0] & 0xFF) == param) {
                        SetELabelCommResult(true);
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
            log.info("#{} ReadParamAsBytes: name={}, counts={}", Params.getAddress(), param, packet.getContentLength());
            byte[] value = new byte[packet.getContentLength() - 1];
            System.arraycopy(packet.Content, 1, value, 0, value.length);
            return value;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadParamAsBytes failed.", Params.getAddress(), ex);
            return defaultValue;
        }
    }

    public byte[] ReadELabelParamAsBytes(int param, byte[] defaultValue, int retries) throws IOException {
        try {
            DataPacket packet = ReadELabelParam(param, retries);
            log.info("#{} ReadELabelParamAsBytes: name={}, counts={}", packet.getAddress(), param, packet.getContentLength());
            byte[] value = new byte[packet.getContentLength() - 1];
            System.arraycopy(packet.Content, 1, value, 0, value.length);
            return value;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadELabelParamAsBytes failed: {}", Params.getELabelAddress(), ex.getMessage());
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
            log.info("#{} ReadParamAsString: name={}, value={}", packet.getAddress(), param, value);
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

    public String ReadELabelParamAsString(int param, String defaultValue, int retries) throws IOException {
        try {
            DataPacket packet = ReadELabelParam(param, retries);
            String value = new String(packet.Content, 1, packet.getContentLength() - 1, Charset.forName(DataPacket.DefaultCharsetName));
            log.info("#{} ReadELabelParamAsString: name={}, value={}", packet.getAddress(), param, value);
            return value;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            // read error used default
            log.debug("#{} ReadELabelParamAsString failed: {}", Params.getELabelAddress(), ex.getMessage());
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
        DataPacket packet = DataPacket.BuildWriteParam(Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteELabelParam(int param, int value) throws Exception {
        log.info("#{} WriteELabelParam: name={}, value={}", Params.getELabelAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam(Params.getELabelAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteParam(int param, BigDecimal value) throws Exception {
        log.info("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam(Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteParam(int param, float value) throws Exception {
        log.info("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam(Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteParam(int param, String value, int maxLen) throws Exception {
        log.debug("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam(Params.getAddress(), param, value, maxLen);
        return WriteParam(packet);
    }

    public int WriteParam(int param, byte[] value) throws Exception {
        log.debug("#{} WriteParam: name={}, value={}", Params.getAddress(), param, value.length);
        DataPacket packet = DataPacket.BuildWriteParam(Params.getAddress(), param, value);
        return WriteParam(packet);
    }

    public int WriteELabelParam(int param, String value, int maxLen) throws Exception {
        log.debug("#{} WriteELabelParam: name={}, value={}", Params.getELabelAddress(), param, value);
        DataPacket packet = DataPacket.BuildWriteParam(Params.getELabelAddress(), param, value, maxLen);
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
            final String versionStr = formatVersion(value);
            log.info("#{} GetFirmwareVersion: value={}", Params.getAddress(), versionStr);
            return versionStr;
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    private String formatVersion(byte[] versionBytes) {
        final List<Integer> sections = new ArrayList<>();
        for (byte b : versionBytes) {
            sections.add((int) b);
        }
        return CollectionUtils.join(sections.stream(), ".");
    }

    public String GetELabelFirmwareVersion(int retries) throws Exception {
        try {
            log.info("#{} GetELabelFirmwareVersion", Params.getELabelAddress());
            byte[] value = ReadELabelParamAsBytes(DataPacket.EParam.FirmwareVersion, new byte[4], retries);
            final String versionStr = formatVersion(value);
            log.info("#{} GetELabelFirmwareVersion: value={}", Params.getELabelAddress(), versionStr);
            return versionStr;
        } catch (Exception ex) {
            SetELabelCommResult(false);
            throw ex;
        }
    }


    public void SetPCBASn(String value) throws Exception {
        WriteParam(DataPacket.EParam.PCBASn, value, 16);
    }

    public String GetPCBASn() throws Exception {
        return ReadParamAsString(DataPacket.EParam.PCBASn, Params.getPCBASn());
    }

    public String GetELabelPCBASn(int retries) throws Exception {
        return ReadELabelParamAsString(DataPacket.EParam.PCBASn, Params.getELabelPCBASn(), retries);
    }

    public void SetDeviceSn(String value) throws Exception {
        WriteParam(DataPacket.EParam.DeviceSn, value, 16);
    }

    public void SetELabelSn(String value) throws Exception {
        WriteELabelParam(DataPacket.EParam.DeviceSn, value, 16);
    }

    public String GetDeviceSn() throws Exception {
        return GetDeviceSn(0);
    }

    public String GetDeviceSn(int retries) throws Exception {
        return GetDeviceSn(Params.getDeviceSn(), retries);
    }

    public String GetDeviceSn(String defaultSn, int retries) throws Exception {
        return ReadParamAsString(DataPacket.EParam.DeviceSn, defaultSn, retries);
    }

    public String GetELabelDeviceSn(int retries) throws Exception {
        return GetELabelDeviceSn(Params.getDeviceSn(), retries);
    }

    public String GetELabelDeviceSn(String defaultSn, int retries) throws Exception {
        return ReadELabelParamAsString(DataPacket.EParam.DeviceSn, defaultSn, retries);
    }

    public void SetDeviceModel(String value) throws Exception {
        WriteParam(DataPacket.EParam.DeviceModel, value, 16);
    }


    public String GetDeviceModel() throws Exception {
        return ReadParamAsString(DataPacket.EParam.DeviceModel, Params.getDeviceModel());
    }

    public String GetELabelDeviceModel(int retries) throws Exception {
        return ReadELabelParamAsString(DataPacket.EParam.DeviceModel, Params.getDeviceModel(), retries);
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

    public void SetELabelPieceCount(String value, boolean inAccuracy) throws Exception {
        WriteELabelPCS(value, inAccuracy);
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

    public boolean SetELabelStatus(int value) throws Exception {
        return WriteELabelStatus(value);
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
                } else {
                    newStatus &= (~(int) DataPacket.EELabelStatusBits.Enabled);
                }
                if (status != newStatus) {
                    SetELabelStatus(newStatus);
                }
            } catch (Exception ex) {
                log.warn("WriteELabelEnable failed:{}", ex.getMessage());
            }
        }
        Params.setEnabled(enable);
    }

    public boolean SetLongPressedMark(boolean mark) {
        if (getParams().hasELabel()) {
            try {
                int status = GetELabelStatus();
                if (status == -1) {
                    return false;
                }
                int newStatus = status;
                if (mark) {
                    newStatus |= DataPacket.EELabelStatusBits.LongPressedMark;
                } else {
                    newStatus &= (~DataPacket.EELabelStatusBits.LongPressedMark);
                }
                if (status != newStatus) {
                    return SetELabelStatus(newStatus);
                }
                return true;
            } catch (Exception ex) {
                log.warn("WriteELabelLongPressedMark failed:{}", ex.getMessage());
            }
        }
        return false;
    }

    public DataPacket OperateELabel(int cmd, int page, int totalPage, byte[] data) throws Exception {

        int address = Params.getELabelAddress();
        DataPacket packet = DataPacket.BuildELabelCmd(address, cmd, page, totalPage, data);

        long endTime = System.currentTimeMillis() + getReadTimeout();
        synchronized (Driver.getLock()) {
            do {
                final DataPacket res = Driver.WriteRead(packet, getReadTimeout(), 1);
                if ((res.Content[1] & 0xFF) == cmd) {
                    //SetCommResult(true);
                    return res;
                }
            } while (System.currentTimeMillis() <= endTime);
        }
        throw new TimeoutException("OperateELabel failed");
    }

    private int ReadELabelAsInt(int cmd, int page, int totalPage) throws Exception {
        DataPacket packet = OperateELabel(cmd, page, totalPage, null);
        return ByteHelper.bytesToInt(packet.Content, 3, 4);
    }

    public int ReadELabelStatus() throws Exception {
        return ReadELabelAsInt(DataPacket.EELabelCmdID.ReadStatus, 0, 1);
    }

    public boolean WriteELabelStatus(int status) throws Exception {
        DataPacket packet = OperateELabel(DataPacket.EELabelCmdID.WriteStatus, 0, 1, ByteHelper.intToBytes(status));
        return (packet.Content[0] & 0xFF) == DataPacket.EResult.OK;
    }


    void WriteELabelString(int cmd, int page, int totalPage, int color, String str) throws Exception {
        if (str == null) {
            str = "";
        }
        byte[] bts = str.getBytes(DataPacket.DefaultCharsetName);
        byte[] content = new byte[1 + 4 + bts.length];
        content[0] = DataPacket.EELabelPalette.Bpp16;
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
        WriteELabelString(DataPacket.EELabelCmdID.WritePartNumber, 0, 1, DataPacket.EELabelColor.White, value);
    }

    public void WriteELabelPartName(String value) throws Exception {
        WriteELabelString(DataPacket.EELabelCmdID.WritePartName, 0, 1, DataPacket.EELabelColor.White, value);
    }

    public void WriteELabelWeight(String value) throws Exception {
        WriteELabelString(DataPacket.EELabelCmdID.WriteWeight, 0, 1, DataPacket.EELabelColor.Black, value);
    }

    public void WriteELabelPCS(String value, boolean inAccuracy) throws Exception {
        WriteELabelString(DataPacket.EELabelCmdID.WritePCS, 0, 1, inAccuracy ? DataPacket.EELabelColor.Black : DataPacket.EELabelColor.Red, value);
    }

    public void WriteELabelBinNo(String value) throws Exception {
        WriteELabelString(DataPacket.EELabelCmdID.WriteBinNo, 0, 1, DataPacket.EELabelColor.Black, value);
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
                buf[0] = DataPacket.EELabelPalette.Bpp16;
                ByteHelper.intToBytes(color, buf, 1);
                ByteHelper.intToBytes(width, buf, 5, 2);
                ByteHelper.intToBytes(height, buf, 7, 2);
                for (int pos = 0; pos < size; pos++) {
                    buf[9 + pos] = (byte) (data[pageSize * p + pos] & 0xFF);
                }
            } else {
                buf = new byte[size];
                for (int pos = 0; pos < size; pos++) {
                    buf[pos] = (byte) (data[pageSize * p + pos] & 0xFF);
                }
            }
            OperateELabel(DataPacket.EELabelCmdID.WriteLogo, p, pages, buf);
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

                if (Params.hasELabel()) {
                    Params.setELabelFirmwareVersion(GetELabelFirmwareVersion(0));
                    Params.setELabelPCBASn(GetELabelPCBASn(0));
                    Params.setELabelDeviceSn(GetELabelDeviceSn(0));
                    Params.setELabelDeviceModel(GetELabelDeviceModel(0));
                }
            }
            log.info("#{} UpdateParams Done", Params.getAddress());
        } catch (Exception ex) {
            SetCommResult(false);
            throw ex;
        }
    }

    public void Unlock() throws Exception {
        WriteParam(DataPacket.EParam.Locker, 20200505);
    }

    public void UnlockELabel() throws Exception {
        WriteELabelParam(DataPacket.EParam.Locker, 20200505);
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

    protected boolean UpgradeQuery(byte[] version) throws Exception {
        int result = DataPacket.EResult.ErrUnknow;

        log.debug("#{} UpgradeQuery", Params.getAddress());
        DataPacket packet = DataPacket.BuildUpgradeQuery(Params.getAddress());
        try {
            packet = UpgradeWriteRead(packet, getUpgradeReadTimeout());
            SetCommResult(true);
            result = packet.Content[1] & 0xFF;
            System.arraycopy(packet.Content, 2, version, 0, version.length);
            log.debug("UpgradeQuery: result={}, protocol={}, version={}.{}.{}", result, version[0], version[1], version[2], version[3]);
            return result == DataPacket.EResult.OK;
        } catch (TimeoutException ex) {
            return false;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            log.error("UpgradeQuery", ex);

            SetCommResult(false);
            throw ex;
        }
    }

    protected boolean UpgradeStart(int deviceType) throws Exception {
        log.debug("#{} UpgradeStart: DeviceType={}", Params.getAddress(), deviceType);
        DataPacket packet = DataPacket.BuildUpgradeStart(Params.getAddress(), deviceType, 1000);
        try {
            packet = UpgradeWriteRead(packet, getUpgradeReadTimeout());
            SetCommResult(true);
            int result = packet.Content[1] & 0xFF;
            log.debug("UpgradeStart: result={}", result);
            return result == DataPacket.EResult.OK;
        } catch (TimeoutException ex) {
            return false;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            log.error("UpgradeStart", ex);

            SetCommResult(false);
            throw ex;
        }
    }

    protected boolean UpgradeSendHead(int flushAddress, int dataSize) throws Exception {
        int roundSize = dataSize;
        while (roundSize % 4 != 0) {
            roundSize++;
        }
        int result = DataPacket.EResult.ErrUnknow;
        log.debug("#{} UpgradeSendHead: address=0x{}, size={}({})", Params.getAddress(), Integer.toHexString(flushAddress), roundSize, dataSize);
        DataPacket packet = DataPacket.BuildUpgradeHead(Params.getAddress(), flushAddress, roundSize);
        try {
            packet = UpgradeWriteRead(packet, getEmptyFlashTimeout());
            SetCommResult(true);
            result = packet.Content[1] & 0xFF;
            log.debug("UpgradeSendHead: result={}", result);
            return result == DataPacket.EResult.OK;
        } catch (TimeoutException ex) {
            return false;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            log.error("UpgradeSendHead", ex);

            SetCommResult(false);
            throw ex;
        }
    }

    protected boolean UpgradeSendEnd() throws Exception {
        int result = DataPacket.EResult.ErrUnknow;
        log.debug("#{} UpgradeSendEnd", Params.getAddress());
        DataPacket packet = DataPacket.BuildUpgradeEnd(Params.getAddress());
        Driver.Write(packet);
        Thread.sleep(100);
        Driver.Write(packet);
        return true;
    }

    protected boolean UpgradeSendData(int packNo, byte[] data) throws Exception {
        int result = DataPacket.EResult.ErrUnknow;
        log.debug("#{} UpgradeSendData: packNo={}, dataLen={}", Params.getAddress(), packNo, data.length);
        DataPacket packet = DataPacket.BuildUpgradeData(Params.getAddress(), packNo, data);
        try {
            packet = UpgradeWriteRead(packet, getWriteParamTimeout());
            SetCommResult(true);
            result = packet.Content[1] & 0xFF;
            log.debug("UpgradeSendData: result={}", result);
            return result == DataPacket.EResult.OK;
        } catch (TimeoutException ex) {
            return false;
        } catch (IOException ex) {
            // port is closed
            throw ex;
        } catch (Exception ex) {
            log.error("UpgradeSendData", ex);

            SetCommResult(false);
            throw ex;
        }
    }

    public interface OnUpgradeProgress {
        void onProgress(int total, int current);
    }

    private boolean Upgrading = false;

    public boolean UpgradeSensor(byte[] hexFileContent, OnUpgradeProgress p) throws Exception {
        return DoUpgrade(hexFileContent, DataPacket.EDeviceType.DigitalSensor, p);
    }

    public boolean UpgradeELabel(byte[] hexFileContent, OnUpgradeProgress p) throws Exception {
        return DoUpgrade(hexFileContent, DataPacket.EDeviceType.ELabel, p);
    }

    public boolean DoUpgrade(byte[] hexFileContent, int deviceType, OnUpgradeProgress p) throws Exception {
        if (p != null) {
            p.onProgress(0, 0);
        }
        if (Upgrading) {
            return false;
        }

        int totalSize = 0;
        HexFileParser hex = new HexFileParser();
        List<HexFileParser.HexDataBlockInfo> blocks = new ArrayList<>();
        try {
            hex.Import(hexFileContent);
            blocks = hex.ReadToDataBlocks();
        } finally {
        }

        synchronized (Driver.getLock()) {
            try {
                Upgrading = true;
                int addr = 0;
                switch (deviceType) {
                    default:
                    case DataPacket.EDeviceType.DigitalSensor: {
                        addr = Params.getAddress();
                        break;
                    }
                    case DataPacket.EDeviceType.ELabel: {
                        addr = Params.getELabelAddress();
                        break;
                    }
                }
                DigitalSensorItem sensor = NewSensor(addr, Driver, Group);
                byte[] version = new byte[4];
                // check status
                if (!sensor.UpgradeQuery(version)) {
                    try {
                        log.debug("Try start device boot");
                        // unlock first
                        sensor.Unlock();
                        // start to bootloader
                        if (sensor.UpgradeStart(deviceType)) {
                            log.debug("Call boot done");
                        } else {
                            log.debug("Call boot failed, try upgrading directly");
                        }
                    } catch (TimeoutException ex) {
                        // ignore
                        log.debug("Start boot failed, try upgrading directly");
                    }
                }
                // build default sensor for upgrading
                sensor = NewDefaultSensor(Driver, Group);
                // handshake
                while (Upgrading) {
                    if (sensor.UpgradeQuery(version)) {
                        break;
                    }
                }
                boolean hasError = false;
                // send blocks
                for (val block : blocks) {
                    if (!Upgrading) {
                        break;
                    }

                    // send head
                    if (sensor.UpgradeSendHead(block.Address, block.Data.length)) {
                        int packNo = DataPacket.EUpgradePackNo.DataHead;
                        int offset = 0;
                        int size = 128;
                        byte[] data = new byte[size];

                        totalSize += block.Data.length;
                        while (Upgrading) {
                            int realSize = Math.min(size, block.Data.length - offset);
                            if (realSize <= 0) {
                                // no more data
                                break;
                            }
                            // padding to 4 multiple
                            int padSize = ((realSize + 3) / 4) * 4;
                            byte[] tbs = data;
                            if (data.length != padSize) {
                                tbs = new byte[padSize];
                            }
                            try {
                                System.arraycopy(block.Data, offset, tbs, 0, realSize);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            for (int pos = realSize; pos < tbs.length; pos++) {
                                tbs[pos] = (byte) 0xFF;
                            }
                            log.debug("Downloading progress {}%", offset * 100 / block.Data.length);
                            if (p != null) {
                                p.onProgress(block.Data.length, offset);
                            }

                            if (sensor.UpgradeSendData(packNo, tbs)) {
                                offset += tbs.length;
                                packNo++;
                                if (packNo > DataPacket.EUpgradePackNo.DataEnd) {
                                    packNo = DataPacket.EUpgradePackNo.DataHead;
                                }
                            }
                        }
                    } else {
                        hasError = true;
                    }
                }
                if (!Upgrading) {
                    return false;
                }
                if (hasError) {
                    log.warn("Upgrading Failed");
                    return false;
                } else {
                    // send end
                    if (sensor.UpgradeSendEnd()) {
                        // finish upgrading
                    }
                    log.debug("Upgrading Finished");
                    if (p != null) {
                        p.onProgress(totalSize, totalSize);
                    }
                    return true;
                }
            } finally {
                Upgrading = false;
            }
        }
    }

    public void AbortUpgrading() {
        Upgrading = false;
    }

}
