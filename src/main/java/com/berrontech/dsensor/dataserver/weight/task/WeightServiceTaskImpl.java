package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorDriver;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorGroup;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorManager;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryWeightData;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:47
 * Class Name: WeightServiceTaskImpl
 * Author: Levent8421
 * Description:
 * 称重服务
 *
 * @author Levent8421
 */
@Component
@Slf4j
@Data
public class WeightServiceTaskImpl implements WeightServiceTask {

    /**
     * 称重数据临时保存于此
     */
    private final WeightDataHolder weightDataHolder;
    /**
     * TCP API Client
     */
    private final ApiClient apiClient;

    final
    WeightNotifier weightNotifier;
    /**
     * Digital Sensor Manager
     */
    private final DigitalSensorManager sensorManager;

    public WeightServiceTaskImpl(WeightDataHolder weightDataHolder,
                                 ApiClient apiClient,
                                 WeightNotifier weightNotifier,
                                 DigitalSensorManager sensorManager) {
        this.weightDataHolder = weightDataHolder;
        this.apiClient = apiClient;
        this.weightNotifier = weightNotifier;
        this.sensorManager = sensorManager;
    }

    @Override
    public void setup() {
        //TODO 初始换传感器控制组件

        buildDigitalSensors(sensorManager, weightDataHolder);
        sensorManager.open();
        sensorManager.startReading();
    }

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
                    default: {
                        log.info("Unknow connection type: {}", conn.getType());
                        break;
                    }
                    case DeviceConnection.TYPE_SERIAL: {
                        log.debug( "Add group on serial: {}", conn.getTarget());
                        group.setCommMode(DigitalSensorGroup.ECommMode.Com);
                        group.setCommSerial(conn.getTarget());
                        break;
                    }
                    case DeviceConnection.TYPE_NET: {
                        log.debug("Add group on tcp: {}", conn.getTarget());
                        String[] parts = conn.getTarget().split(":");
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
                log.debug("Build sensors: {}", count);
                group.BuildSensors(count);

                int pos = 0;
                for (WeightSensor sen : weightDataHolder.getWeightSensors()) {
                    if (sen.getConnectionId().equals(conn.getId())) {
                        log.debug("Config sensor: conn={}, sen={}", conn, sen);
                        val sensor = group.getSensors().get(pos++);
                        val params = sensor.getParams();
                        params.setAddress(sen.getAddress());
                        params.setDeviceSn(sen.getDeviceSn());

                        var slot = weightDataHolder.getSlots().stream().filter(a -> a.getId().equals(sen.getSlotId())).findFirst().get();
                        sensor.setSubGroup(slot.getSlotNo());
                    }
                }
            } catch (Exception ex)
            {
                log.error("buildDigitalSensors error: connId={}, target={}", conn.getId(), conn.getTarget(), ex);
            }
        }
    }

    /**
     * 主循环
     *
     * @return return false表示不再进行下一次循环
     */
    @Override
    public boolean loop() {
        if (sensorManager != null) {
            if (sensorManager.isOpened()) {
                try {
                    for (val g : sensorManager.getGroups()) {
                        for (val s : g.getSensors()) {
                            if (weightDataHolder.getSlotTable().containsKey(s.getSubGroup())) {
                                val slot = weightDataHolder.getSlotTable().get(s.getSubGroup());
                                var data = slot.getData();
                                if (data == null) {
                                    data = new MemoryWeightData();
                                }
                                slot.setData(data);
                                val weightInGram = (int) (s.getValues().getNetWeight().doubleValue() * 1000);
                                data.setWeight(weightInGram);
                            }
                        }
                    }
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void beforeStop() {
        if (sensorManager != null) {
            sensorManager.StopReading();
            sensorManager.close();
        }
    }

    @Override
    public void afterStop() {

    }
}
