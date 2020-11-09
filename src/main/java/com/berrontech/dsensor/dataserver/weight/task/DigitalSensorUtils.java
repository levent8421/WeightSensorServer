package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.*;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DigitalSensorUtils {
    public static void buildDigitalSensors(DigitalSensorManager sensorManager, WeightDataHolder weightDataHolder) {
        log.debug("start buildDigitalSensors");
        sensorManager.shutdown();
        sensorManager.getGroups().clear();
        log.debug("connection count={}", weightDataHolder.getConnections().size());
        for (DeviceConnection conn : weightDataHolder.getConnections()) {
            try {
                int count1 = (int) weightDataHolder.getWeightSensors().stream().filter((a) -> a.getConnectionId().equals(conn.getId())).count();
                log.debug("{} WeightSensors in Group({})", count1, conn.getId());
                int count2 = (int) weightDataHolder.getTemperatureHumiditySensors().stream().filter((a) -> a.getConnectionId().equals(conn.getId())).count();
                log.debug("{} TemperatureHumiditySensors in Group({})", count2, conn.getId());
                int count = count1 + count2;
                if (count <= 0) {
                    continue;
                }

                DigitalSensorGroup group = sensorManager.NewGroup();
                switch (conn.getType()) {
                    //switch (2) {
                    default: {
                        log.info("Unknow connection type: {}", conn.getType());
                        break;
                    }
                    case DeviceConnection.TYPE_SERIAL: {
                        log.debug("Add group on serial: {}", conn.getTarget());
                        group.setConnectionId(conn.getId());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Com);
                        group.setCommSerial(conn.getTarget());
                        break;
                    }
                    case DeviceConnection.TYPE_NET: {
                        String target = conn.getTarget();
                        //String target = "127.0.0.1:8200";
                        log.debug("Add group on tcp: {}", target);
                        String[] parts = target.split(":");
                        group.setConnectionId(conn.getId());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Net);
                        group.setCommAddress(parts[0]);
                        if (parts.length > 1) {
                            group.setCommPort(Integer.parseInt(parts[1]));
                        } else {
                            final int defaultPort = 10086;
                            log.info("Use default net port: {}", defaultPort);
                            group.setCommPort(defaultPort);
                        }
                        group.setReadTimeout(300);
                        break;
                    }
                }
                log.debug("Build sensors: {}", count);
                group.BuildSensors(count);

                // reg slot info
                int pos = 0;
                for (WeightSensor sen : weightDataHolder.getWeightSensors()) {
                    if (Objects.equals(sen.getConnectionId(), conn.getId())) {
                        log.debug("Config sensor: conn={}, sen={}", conn, sen);
                        val sensor = group.getSensors().get(pos++);
                        val params = sensor.getParams();
                        params.setId(sen.getId());
                        params.setAddress(sen.getAddress());
                        params.setDeviceSn(sen.getDeviceSn());
                        params.setBackupSensorSn(sen.getSensorSn());
                        params.setBackupELabelSn(sen.getElabelSn());
                        params.setNegativeMode(StringUtils.isNotBlank(sen.getConfigStr()));
                        sensor.getValues().setZeroOffset(sen.getZeroReference() == null ? 0 : sen.getZeroReference().floatValue());
                        if (sen.getHasElabel()) {
//                            params.setELabelModel(DigitalSensorParams.EELabelModel.V3);
                            params.setELabelModel(DigitalSensorParams.EELabelModel.V4);
                        }
                        sensor.getValues().setFilterDepth(weightDataHolder.getSoftFilterLevel());

                        val ms = DigitalSensorUtils.tryLookupMemorySlot(sen.getSlotId(), weightDataHolder);
                        if (ms != null) {
                            sensor.setSubGroupId(ms.getId());
                            sensor.setSubGroup(ms.getSlotNo());
                            if (ms.getSensors().size() > 1) {
                                // reg combine slot info
                                val ss = ms.getSensors().stream().sorted(Comparator.comparing(MemoryWeightSensor::getAddress485)).collect(Collectors.toList());
                                for (int idx = 0; idx < ss.size(); idx++) {
                                    if (ss.get(idx).getAddress485() == sensor.getParams().getAddress()) {
                                        // sub group position of a combined slot always start from 1
                                        sensor.setSubGroupPosition(idx + 1);
                                        break;
                                    }
                                }
                            }
                            setSkuToSensor(ms.getSku(), sensor.getPassenger().getMaterial());
                        }
                    }
                }
                for (val sen : weightDataHolder.getTemperatureHumiditySensors()) {
                    if (Objects.equals(sen.getConnectionId(), conn.getId())) {
                        log.debug("Config XSensor: conn={}, sen={}", conn, sen);
                        val sensor = group.getSensors().get(pos++);
                        val params = sensor.getParams();
                        params.setId(sen.getId());
                        params.setAddress(sen.getAddress());
                        params.setDeviceSn(sen.getDeviceSn());
                        params.setDeviceType(DigitalSensorParams.EDeviceType.TempHumi);
                        params.getXSensorLowers()[0] = sen.getMinTemperature();
                        params.getXSensorLowers()[1] = sen.getMinHumidity();
                        params.getXSensorUppers()[0] = sen.getMaxTemperature();
                        params.getXSensorUppers()[1] = sen.getMaxHumidity();
                        params.setIncrement(new BigDecimal("0.1"));
                        //sensor.setSubGroupId(sen.getSlotId());
                        sensor.setSubGroup(sen.getNo());
                    }
                }


            } catch (Exception ex) {
                log.error("buildDigitalSensors error: connId={}, target={}", conn.getId(), conn.getTarget(), ex);
            }
        }
    }

    public static void buildDigitalSensorGroups(DigitalSensorManager sensorManager, Collection<DeviceConnection> connections) {
        sensorManager.shutdown();
        sensorManager.getGroups().clear();
        for (DeviceConnection conn : connections) {
            try {
                DigitalSensorGroup group = sensorManager.NewGroup();
                switch (conn.getType()) {
                    default: {
                        log.info("Unknow connection type: {}", conn.getType());
                        break;
                    }
                    case DeviceConnection.TYPE_SERIAL: {
                        log.debug("Add group on serial: {}", conn.getTarget());
                        group.setConnectionId(conn.getId());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Com);
                        group.setCommSerial(conn.getTarget());
                        break;
                    }
                    case DeviceConnection.TYPE_NET: {
                        log.debug("Add group on tcp: {}", conn.getTarget());
                        String[] parts = conn.getTarget().split(":");
                        group.setConnectionId(conn.getId());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Net);
                        group.setCommAddress(parts[0]);
                        if (parts.length > 1) {
                            group.setCommPort(Integer.parseInt(parts[1]));
                        } else {
                            final int defaultPort = 10086;
                            log.info("Use default net port: {}", defaultPort);
                            group.setCommPort(defaultPort);
                        }
                        break;
                    }
                }
                log.debug("Build single default sensor");
                group.BuildSingleDefaultSensors();
            } catch (Exception ex) {
                log.error("buildDigitalSensors error: connId={}, target={}", conn.getId(), conn.getTarget(), ex);
            }
        }
    }


    public static void setSkuToSensor(MemorySku sku, MaterialInfo mat) {
        if (sku != null) {
            mat.setNumber(sku.getSkuNo());
            mat.setName(sku.getName());
            mat.setAPW(sku.getApw() == null ? 0 : sku.getApw() / 1000.0);
            mat.setToleranceInGram(sku.getTolerance() == null ? 0 : sku.getTolerance());
            mat.setShelfLifeDays(sku.getShelfLifeOpenDays() == null ? 0 : sku.getShelfLifeOpenDays());
        }
    }


    public static MemorySlot tryLookupMemorySlot(int slotId, WeightDataHolder weightDataHolder) {
        MemorySlot slot = weightDataHolder.getSlotTable().values().stream()
                .filter(s -> s.getId() == slotId)
                .findFirst()
                .orElse(null);
        return slot;
    }

    public static MemorySlot tryLookupMemorySlot(DigitalSensorItem sensor, WeightDataHolder weightDataHolder) {
        return tryLookupMemorySlot(sensor.getSubGroupId(), weightDataHolder);
    }

    public static MemoryWeightSensor tryLookupMemorySensor(DigitalSensorItem sensor, WeightDataHolder weightDataHolder) {
        MemorySlot slot = tryLookupMemorySlot(sensor, weightDataHolder);
        if (slot != null) {
            return slot.getSensors().stream()
                    .filter(s -> s.getId() == sensor.getParams().getId())
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public static DigitalSensorGroup tryLookupGroup(DigitalSensorManager sensorManager, int connectionId) {
        for (DigitalSensorGroup group : sensorManager.getGroups()) {
            if (group.getId() == connectionId) {
                return group;
            }
        }
        return null;
    }

    public static DigitalSensorItem tryLookupSensor(DigitalSensorManager sensorManager, int connectionId, int address) {
        DigitalSensorGroup group = tryLookupGroup(sensorManager, connectionId);
        if (group != null) {
            for (DigitalSensorItem sensor : group.getSensors()) {
                if (sensor.getParams().getAddress() == address) {
                    return sensor;
                }
            }
        }
        return null;
    }

}
