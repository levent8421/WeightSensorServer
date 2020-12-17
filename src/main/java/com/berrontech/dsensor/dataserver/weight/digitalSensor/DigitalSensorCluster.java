package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import com.berrontech.dsensor.dataserver.weight.utils.helper.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create By Levent at 2020/8/11 13:58
 * DigitalSensorCluster
 * DigitalSensorCluster 货道组合
 *
 * @author levent
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class DigitalSensorCluster extends DigitalSensorItem {

    private List<DigitalSensorItem> Children = new ArrayList<>();

    public DigitalSensorItem getFirstChild() {
        if (Children == null || Children.size() <= 0) {
            return null;
        }
        return Children.stream().sorted(Comparator.comparing(DigitalSensorItem::getSubGroupPosition)).collect(Collectors.toList()).get(0);
    }

    public void init() {
        DigitalSensorItem firstSensor = getFirstChild();

        setGroup(firstSensor.getGroup());
        setSubGroup(firstSensor.getSubGroup());
        setPassenger(firstSensor.getPassenger());
        setSubGroupId(firstSensor.getSubGroupId());
        buildClusterParams();
    }

    public void buildClusterParams()
    {
        DigitalSensorItem firstSensor = getFirstChild();
        BigDecimal dSum = BigDecimal.ZERO;
        for (DigitalSensorItem c : Children) {
            dSum = dSum.add(c.getParams().getCapacity());
        }
        getParams().setCapacity(dSum);
        getParams().setGeoFactor(firstSensor.getParams().getGeoFactor());
        getParams().setIncrement(firstSensor.getParams().getIncrement());
        getParams().setZeroCapture(firstSensor.getParams().getZeroCapture());
        getParams().setCreepCorrect(firstSensor.getParams().getCreepCorrect());
        getParams().setStableRange(firstSensor.getParams().getStableRange());
        getParams().setStableSpeed(firstSensor.getParams().getStableSpeed());
    }

    public void calc() {
        if (Children.size() <= 0) {
            return;
        }
        DigitalSensorItem firstSensor = getFirstChild();

        getValues().setAPW(firstSensor.getValues().getAPW());
        float fSum;
        BigDecimal sum;
        getValues().setUnit(firstSensor.getValues().getUnit());
        fSum = 0f;
        for (DigitalSensorItem c : Children) {
            fSum += c.getValues().getHighGross();
        }
        getValues().setHighGross(fSum);
//        fSum = 0f;
//        for (DigitalSensorItem c : Children) {
//            fSum += c.getValues().getHighTare();
//        }
//        getValues().setHighTare(fSum);
        sum = BigDecimal.ZERO;
        for (DigitalSensorItem c : Children) {
            sum = sum.add(c.getValues().getGrossWeight());
        }
        getValues().setGrossWeight(sum);
        {
            byte stableMark = Children.stream().allMatch(s -> s.getValues().isRoughlyStable()) ? DigitalSensorValues.StableMark : DigitalSensorValues.DynamicMark;
            getValues().CheckStatus(stableMark, getParams().getCapacity(), getParams().getIncrement());
        }

        fSum = 0f;
        for (DigitalSensorItem c : Children) {
            fSum += c.getTotalSuccess();
        }
        setTotalSuccess((int) fSum);
        setOnlineAndNotify(Children.stream().filter(s -> !s.isOnline()).count() <= 0);
        if (getValues().isPieceCounting()) {
            setCountInAccuracy(calcCountAccuracy(getPassenger().getMaterial().getTolerance(), getValues()));
            setCountError(calcCountError(getValues()));
        } else {
            setCountInAccuracy(true);
            setCountError(0);
        }
        getParams().setEnabled(firstSensor.getParams().isEnabled());
        SubGroup = firstSensor.getSubGroup();
    }

    @Override
    public void UpdateELabel() throws Exception {
        if (Children.size() <= 0) {
            return;
        }
        DigitalSensorItem firstSensor = getFirstChild();
        if (!firstSensor.getParams().hasELabel()) {
            return;
        }

        String number;
        String name;
        String bin;
        String wgt;
        String pcs;
        boolean acc;
        if (firstSensor.getParams().isEnabled()) {
            number = getPassenger().getMaterial().Number;
            name = getPassenger().getMaterial().Name;
            bin = getSubGroup();
            wgt = getValues().getNetWeight() + " " + getValues().getUnit();
            //pcs = String.valueOf(getValues().getPieceCount());
            pcs = String.format("%d", LastNotifyPCS);
            acc = LastNotifyAccuracy;
        } else {
            number = " ";
            name = " ";
            bin = getSubGroup();
            wgt = getValues().getNetWeight() + " " + getValues().getUnit();
            pcs = null;
            acc = true;
        }

        firstSensor.UpdateELabel(number, name, bin, wgt, pcs, acc);
        getParams().setEnabled(firstSensor.getParams().isEnabled());
        number = " ";
        name = " ";
        bin = getSubGroup();
        wgt = " ";
        pcs = " ";
        acc = true;
        for (DigitalSensorItem child : Children) {
            if (child != firstSensor) {
                child.UpdateELabel(number, name, bin, wgt, pcs, acc);
            }
        }
    }

    @Override
    public void DoZero(boolean save) throws Exception {
        log.info("Cluster DoZero: save={}", save);
        for (DigitalSensorItem s : Children) {
            s.DoZero(save);
        }
    }

    @Override
    public void SetCapacity(BigDecimal value) throws Exception {
        log.info("Cluster SetCapacity: total={}", value);
        BigDecimal single = value.divide(BigDecimal.valueOf(Children.size()));
        log.info("ClusterSetCapacity: single={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetCapacity(single);
        }
    }

    @Override
    public BigDecimal GetCapacity() throws Exception {
        log.info("Cluster GetCapacity");
        BigDecimal dSum = BigDecimal.ZERO;
        for (DigitalSensorItem c : Children) {
            dSum = dSum.add(c.GetCapacity());
        }
        return dSum;
    }

    @Override
    public void SetGeoFactor(double value) throws Exception {
        log.info("Cluster SetGeoFactor: value={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetGeoFactor(value);
        }
    }

    @Override
    public float GetGeoFactor() throws Exception {
        log.info("Cluster GetGeoFactor");
        return getFirstChild().GetGeoFactor();
    }

    @Override
    public void SetIncrement(BigDecimal value) throws Exception {
        log.info("Cluster SetIncrement: value={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetIncrement(value);
        }
    }

    @Override
    public BigDecimal GetIncrement() throws Exception {
        log.info("Cluster GetIncrement");
        return getFirstChild().GetIncrement();
    }

    @Override
    public void SetZeroCapture(double value) throws Exception {
        log.info("Cluster SetZeroCapture: value={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetZeroCapture(value);
        }
    }

    @Override
    public float GetZeroCapture() throws Exception {
        log.info("Cluster GetZeroCapture");
        return getFirstChild().GetZeroCapture();
    }

    @Override
    public void SetCreepCorrect(double value) throws Exception {
        log.info("Cluster SetCreepCorrect: value={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetCreepCorrect(value);
        }
    }

    @Override
    public float GetCreepCorrect() throws Exception {
        log.info("Cluster GetCreepCorrect");
        return getFirstChild().GetCreepCorrect();
    }

    @Override
    public void SetStableRange(double value) throws Exception {
        log.info("Cluster SetStableRange: value={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetStableRange(value);
        }
    }

    @Override
    public float GetStableRange() throws Exception {
        log.info("Cluster GetStableRange");
        return getFirstChild().GetStableRange();
    }

    @Override
    public void SetStableSpeed(double value) throws Exception {
        log.info("Cluster SetStableSpeed: value={}", value);
        for (DigitalSensorItem s : Children) {
            s.SetStableSpeed(value);
        }
    }

    @Override
    public float GetStableSpeed() throws Exception {
        log.info("Cluster GetStableSpeed");
        return getFirstChild().GetStableSpeed();
    }

    @Override
    public void UpdateCalibParams() throws Exception {
        log.info("Cluster UpdateCalibParams");
        for (DigitalSensorItem s : Children) {
            s.UpdateCalibParams();
        }
        buildClusterParams();
    }

    @Override
    public void UpdateDevInfoParams() throws Exception {
        log.info("Cluster UpdateDevInfoParams");
        for (DigitalSensorItem s : Children) {
            s.UpdateDevInfoParams();
        }
    }

    @Override
    public void UpdateELabelParams() throws Exception {
        log.info("Cluster UpdateELabelParams");
        for (DigitalSensorItem s : Children) {
            s.UpdateELabelParams();
        }
    }
}
