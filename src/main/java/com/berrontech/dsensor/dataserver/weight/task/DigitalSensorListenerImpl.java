package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorItem;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorListener;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorValues;
import com.berrontech.dsensor.dataserver.weight.holder.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

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
            MemoryWeightSensor s = DigitalSensorUtils.tryLookupMemorySensor(sensor, weightDataHolder);
            if (s == null) {
                log.warn("#{} Can not find memory weight sensor", sensor.getParams().getAddress());
            } else {
                int state = toState(sensor);
                s.setState(state);
                Collection<MemoryWeightSensor> sensors = Collections.singleton(s);
                weightNotifier.sensorStateChanged(sensors);
            }
            return true;
        } catch (Exception ex) {
            log.warn("notify onSensorStateChanged error", ex);
            return false;
        }
    }

    @Override
    public boolean onPieceCountChanged(DigitalSensorItem sensor) {
        if (!sensor.getValues().isDynamic()) {
            log.info("#{} Notify onPieceCountChanged", sensor.getParams().getAddress());
        } else {
            log.debug("#{} Notify onPieceCountChanged, but not stable", sensor.getParams().getAddress());
            return false;
        }

        try {
            final MemorySlot slot = DigitalSensorUtils.tryLookupMemorySlot(sensor, weightDataHolder);
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
            slotData.setTolerance((int)Math.round(sensor.getCountError() * 1000));
            slotData.setToleranceState(sensor.isCountInAccuracy() ? MemoryWeightData.TOLERANCE_STATE_CREDIBLE : MemoryWeightData.TOLERANCE_STATE_INCREDIBLE);
            final Collection<MemorySlot> slots = Collections.singleton(slot);
            weightNotifier.countChange(slots);
            return true;
        } catch (Exception ex) {
            log.warn("notify onPieceCountChanged error", ex);
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
            final MemorySlot slot = DigitalSensorUtils.tryLookupMemorySlot(sensor, weightDataHolder);
            if (slot == null) {
                log.warn("#{} Could not found slot({})", sensor.getParams().getAddress(), sensor.getSubGroup());
            } else {
                if (slot.getData() == null) {
                    slot.setData(new MemoryWeightData());
                }
                val data = slot.getData();
                data.setWeight(sensor.getValues().getNetWeight().multiply(BigDecimal.valueOf(1000)).intValue());
                data.setWeightState(sensor.getValues().isStable() ? MemoryWeightData.WEIGHT_STATE_STABLE : MemoryWeightData.WEIGHT_STATE_DYNAMIC);
                slot.setState(toState(sensor));
            }
            return true;
        } catch (Exception ex) {
            log.warn("notify onPieceCountChanged error", ex);
            return false;
        }
    }

    @Override
    public void onNotifySaveZeroOffset(DigitalSensorItem sensor) {
        final int id = sensor.getParams().getId();
        if (id <= 0) {
            final int address = sensor.getParams().getAddress();
            log.debug("Skip save zeroOffset form sensor [{}], id=[{}]", address, id);
            return;
        }
        sensorService.setZeroReference(id, (double) sensor.getValues().getZeroOffset());
    }

    @Override
    public boolean onNotifyXSensorTempHumi(DigitalSensorItem sensor) {
        try {
            final MemoryTemperatureHumiditySensor slot = weightDataHolder.getTemperatureHumiditySensorTable().get(sensor.getParams().getId());
            if (slot == null) {
                log.warn("#{} Could not found slot({})", sensor.getParams().getAddress(), sensor.getSubGroup());
            } else {
                if (slot.getData() == null) {
                    slot.setData(new MemoryTemperatureHumidityData());
                }
                slot.setState(toState(sensor));

                val data = slot.getData();
                data.setTemperature(sensor.getValues().getXSensors()[0].doubleValue());
                data.setHumidity(sensor.getValues().getXSensors()[1].doubleValue());
                data.setTemperatureState(toState(DigitalSensorItem.toFlatStatus(sensor.isOnline(), sensor.getParams().isDisabled(), sensor.getValues().getXSensorStatus()[0])));
                data.setHumidityState(toState(DigitalSensorItem.toFlatStatus(sensor.isOnline(), sensor.getParams().isDisabled(), sensor.getValues().getXSensorStatus()[1])));
            }
            return true;
        } catch (Exception ex) {
            log.warn("notify OnNotifyXSensorTempHumi error", ex);
            return false;
        }
    }

    @Override
    public boolean onNotifySensorSnChanged(DigitalSensorItem sensor, String sn) {
        return sensorService.updateSensorSn(sensor.getParams().getId(), sn);
    }

    @Override
    public boolean onNotifyELabelSnChanged(DigitalSensorItem sensor, String sn) {
        return sensorService.updateElabelSn(sensor.getParams().getId(), sn);
    }


    private static int toState(DigitalSensorItem sensor) {
        return toState(sensor.getFlatStatus());
    }

    private static int toState(DigitalSensorItem.EFlatStatus flatStatus) {
        int state;
        switch (flatStatus) {
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
