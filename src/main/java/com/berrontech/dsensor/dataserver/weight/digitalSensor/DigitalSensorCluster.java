package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class DigitalSensorCluster extends DigitalSensorItem {

    private List<DigitalSensorItem> Children = new ArrayList<>();

    public DigitalSensorItem getFirstChild()
    {
        if (Children == null || Children.size() <= 0){
            return null;
        }
        return Children.stream().sorted(Comparator.comparing(DigitalSensorItem::getSubGroupPosition)).collect(Collectors.toList()).get(0);
    }

    public void Calc() {
        if (Children.size() <= 0) {
            return;
        }
        DigitalSensorItem firstSensor = Children.stream().sorted(Comparator.comparing(DigitalSensorItem::getSubGroupPosition)).collect(Collectors.toList()).get(0);

        float fSum = 0f;
        BigDecimal sum;
        sum = BigDecimal.ZERO;
        for (DigitalSensorItem c : Children) {
            sum = sum.add(c.getValues().getGrossWeight());
        }
        getValues().setGrossWeight(sum);
        getValues().setUnit(firstSensor.getValues().getUnit());
        fSum = 0f;
        for (DigitalSensorItem c : Children) {
            fSum += c.getValues().getHighGross();
        }
        getValues().setHighGross(fSum);
        getValues().setStatus((Children.stream().filter(s -> !s.getValues().isStable()).count() > 0) ? DigitalSensorValues.EStatus.Dynamic : DigitalSensorValues.EStatus.Stable);
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
        setTotalSuccess((int) fSum);
        setOnline(Children.stream().filter(s -> !s.isOnline()).count() <= 0);
        getValues().setAPW(firstSensor.getValues().getAPW());
        if (getValues().isPieceCounting()) {
            setCountInAccuracy(Math.abs(1 - getValues().getPieceCountAccuracy()) <= getPassenger().getMaterial().getTolerance());
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
        DigitalSensorItem firstSensor = Children.stream().sorted(Comparator.comparing(DigitalSensorItem::getSubGroupPosition)).collect(Collectors.toList()).get(0);

        String number = null;
        String name = null;
        String bin = null;
        String wgt = null;
        String pcs = null;
        if (getParams().isEnabled()) {
            number = getPassenger().getMaterial().Number;
            name = getPassenger().getMaterial().Name;
            bin = getSubGroup();
            wgt = getValues().getNetWeight() + " " + getValues().getUnit();
            pcs = String.valueOf(getValues().getPieceCount());
        } else {
            number = " ";
            name = " ";
            bin = getSubGroup();
            wgt = getValues().getNetWeight() + " " + getValues().getUnit();
            pcs = null;
        }

        firstSensor.UpdateELabel(number, name, bin, wgt, pcs);
        for (int pos = 1; pos < Children.size(); pos++) {
            number = " ";
            name = " ";
            bin = getSubGroup();
            wgt = " ";
            pcs = " ";
            Children.get(pos).UpdateELabel(number, name, bin, wgt, pcs);
        }
    }
}
