package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorItem;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorListener;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightData;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Create By Lastnika
 * Create Time: 2020/7/2 15:33
 * Class Name: DigitalSensorListenerImpl
 * Author: Lastnika
 * Description:
 * DigitalSensorListenerImpl
 * 传感器监听器实现
 *
 * @author Lastnika
 */
@Slf4j
public class DigitalSensorListenerImpl implements DigitalSensorListener {
    private final WeightDataHolder weightDataHolder;
    private final WeightNotifier weightNotifier;
    private final WeightSensorService sensorService;

    DigitalSensorListenerImpl(WeightDataHolder weightDataHolder,
                              WeightNotifier weightNotifier,
                              WeightSensorService sensorService) {
        this.weightDataHolder = weightDataHolder;
        this.weightNotifier = weightNotifier;
        this.sensorService = sensorService;
    }

    @Override
    public boolean onSensorStateChanged(DigitalSensorItem sensor) {
        log.debug("#{} Notify onSensorStateChanged", sensor.getParams().getAddress());
        try {
            WeightSensor s1 = weightDataHolder.getWeightSensors().stream()
                    .filter(s -> Objects.equals(s.getAddress(), sensor.getParams().getAddress()))
                    .findFirst()
                    .orElse(null);
            if (s1 != null) {
                MemoryWeightSensor s2 = MemoryWeightSensor.of(s1);
                s2.setState(toState(sensor));
                Collection<MemoryWeightSensor> sensors = Collections.singleton(s2);
                weightNotifier.sensorStateChanged(sensors);
            }
            return true;
        } catch (Exception ex) {
            log.warn("notify onSensorStateChanged error: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean onPieceCountChanged(DigitalSensorItem sensor) {
        if (sensor.getValues().isStable()) {
            log.info("#{} Notify onPieceCountChanged", sensor.getParams().getAddress());
        } else {
            log.debug("#{} Notify onPieceCountChanged, but not stable", sensor.getParams().getAddress());
            return false;
        }

        try {
            final MemorySlot slot = tryLookupMemorySlot(sensor, weightDataHolder);
            if (slot == null) {
                log.debug("#{} Could not found slot ({})", sensor.getParams().getAddress(), sensor.getSubGroup());
                return false;
            }
            if (slot.getData() == null) {
                slot.setData(new MemoryWeightData());
            }
            val slotData = slot.getData();
            slotData.setWeight(sensor.getValues().getNetWeight().multiply(BigDecimal.valueOf(1000)).intValue());
            slotData.setCount(sensor.getValues().getPieceCount());
            slotData.setTolerance((int) (sensor.getValues().getPieceCountAccuracy() * 100));
            slotData.setToleranceState(sensor.isCountInAccuracy() ? MemoryWeightData.TOLERANCE_STATE_CREDIBLE : MemoryWeightData.TOLERANCE_STATE_INCREDIBLE);
            final Collection<MemorySlot> slots = Collections.singleton(slot);
            weightNotifier.countChange(slots);
            return true;
        } catch (Exception ex) {
            log.warn("notify onPieceCountChanged error: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean onSlotStateChanged(DigitalSensorItem sensor) {
        log.debug("#{} Notify onSlotStateChanged", sensor.getParams().getAddress());
        return true;
    }

    @Override
    public boolean onWeightChanged(DigitalSensorItem sensor) {
        try {
            final MemorySlot slot = tryLookupMemorySlot(sensor, weightDataHolder);
            if (slot == null) {
                log.warn("#{} Could not found slot({})", sensor.getParams().getAddress(), sensor.getSubGroup());
            } else {
                if (slot.getData() == null) {
                    slot.setData(new MemoryWeightData());
                }
                val data = slot.getData();
                data.setWeight(sensor.getValues().getNetWeight().multiply(BigDecimal.valueOf(1000)).intValue());
                data.setWeightState(toState(sensor));
                slot.setState(toState(sensor));
            }
            return true;
        } catch (Exception ex) {
            log.warn("notify onPieceCountChanged error: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public void onNotifySaveZeroOffset(DigitalSensorItem sensor) {
        sensorService.setZeroReference(sensor.getParams().getId(), (double)sensor.getValues().getZeroOffset());
    }

    private static MemorySlot tryLookupMemorySlot(DigitalSensorItem sensor, WeightDataHolder weightDataHolder) {
        return weightDataHolder.getSlotTable().get(sensor.getSubGroup());
    }

    private static int toState(DigitalSensorItem sensor) {
        int state;
        switch (sensor.getFlatStatus()) {
            case Offline: {
                state = WeightSensor.STATE_OFFLINE;
                break;
            }
            case Disabled: {
                state = WeightSensor.STATE_DISABLE;
                break;
            }
            case Underload: {
                state = WeightSensor.STATE_UNDER_LOAD;
                break;
            }
            case Overload: {
                state = WeightSensor.STATE_OVERLOAD;
                break;
            }
            case Normal:
            default: {
                state = WeightSensor.STATE_ONLINE;
                break;
            }
        }
        return state;
    }

}
