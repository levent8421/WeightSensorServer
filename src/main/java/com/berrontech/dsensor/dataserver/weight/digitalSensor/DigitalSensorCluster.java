package com.berrontech.dsensor.dataserver.weight.digitalSensor;

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

    public void calc() {
        if (Children.size() <= 0) {
            return;
        }
        DigitalSensorItem firstSensor = getFirstChild();

        float fSum;
        BigDecimal sum;
        getValues().setUnit(firstSensor.getValues().getUnit());
        fSum = 0f;
        for (DigitalSensorItem c : Children) {
            fSum += c.getValues().getHighGross();
        }
        getValues().setHighGross(fSum);
        fSum = 0f;
        for (DigitalSensorItem c : Children) {
            fSum += c.getValues().getHighTare();
        }
        getValues().setHighTare(fSum);
        //Values.IsHighlight = firstSensor?.Values.IsHighlight ?? false;
        fSum = 0f;
        for (DigitalSensorItem c : Children) {
            fSum += c.getTotalSuccess();
        }
        sum = BigDecimal.ZERO;
        for (DigitalSensorItem c : Children) {
            sum = sum.add(c.getValues().getGrossWeight());
        }
        getValues().setGrossWeight(sum);
        getValues().setStatus((Children.stream().anyMatch(s -> s.getValues().isDynamic())) ? DigitalSensorValues.EStatus.Dynamic : DigitalSensorValues.EStatus.Stable);
        setTotalSuccess((int) fSum);
        setOnlineAndNotify(Children.stream().filter(s -> !s.isOnline()).count() <= 0);
        getValues().setAPW(firstSensor.getValues().getAPW());
        if (getValues().isPieceCounting()) {
            setCountInAccuracy(calcCountAccuracy(getPassenger().getMaterial().getTolerance(), getValues()));
        } else {
            setCountInAccuracy(true);
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

        String number;
        String name;
        String bin;
        String wgt;
        String pcs;
        if (getParams().isEnabled()) {
            number = getPassenger().getMaterial().Number;
            name = getPassenger().getMaterial().Name;
            bin = getSubGroup();
            wgt = getValues().getNetWeight() + " " + getValues().getUnit();
            //pcs = String.valueOf(getValues().getPieceCount());
            pcs = String.format("%d", LastNotifyPCS);
        } else {
            number = " ";
            name = " ";
            bin = getSubGroup();
            wgt = getValues().getNetWeight() + " " + getValues().getUnit();
            pcs = null;
        }

        firstSensor.UpdateELabel(number, name, bin, wgt, pcs);
        number = " ";
        name = " ";
        bin = getSubGroup();
        wgt = " ";
        pcs = " ";
        for (int pos = 0; pos < Children.size(); pos++) {
            if (Children.get(pos) != firstSensor) {
                Children.get(pos).UpdateELabel(number, name, bin, wgt, pcs);
            }
        }
    }
}
