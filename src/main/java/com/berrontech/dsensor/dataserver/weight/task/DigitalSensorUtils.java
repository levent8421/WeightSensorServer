package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.*;
import com.berrontech.dsensor.dataserver.weight.holder.MemorySku;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightSensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class DigitalSensorUtils {
    public static void buildDigitalSensors(DigitalSensorManager sensorManager, WeightDataHolder weightDataHolder) {
        sensorManager.shutdown();
        sensorManager.getGroups().clear();
        for (DeviceConnection conn : weightDataHolder.getConnections()) {
            try {
                int count = (int) weightDataHolder.getWeightSensors().stream().filter((a) -> a.getConnectionId().equals(conn.getId())).count();
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
                        sensor.getValues().setZeroOffset(sen.getZeroReference() == null ? 0 : sen.getZeroReference().floatValue());
                        if (sen.getHasElabel()) {
//                            params.setELabelModel(DigitalSensorParams.EELabelModel.V3);
                            params.setELabelModel(DigitalSensorParams.EELabelModel.V4);
                        }

                        Slot slot = weightDataHolder.getSlots().stream().filter(a -> a.getId().equals(sen.getSlotId())).findFirst().orElse(null);
                        if (slot != null) {
                            val ms = weightDataHolder.getSlotTable().get(slot.getSlotNo());
                            if (ms != null) {
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


}
