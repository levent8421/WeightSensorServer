package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorDriver;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorGroup;
import com.berrontech.dsensor.dataserver.weight.digitalSensor.DigitalSensorManager;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

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
public class WeightServiceTaskImpl implements WeightServiceTask {
    private String TAG = WeightServiceTaskImpl.class.getName();

    /**
     * 称重数据临时保存于此
     */
    private final WeightDataHolder weightDataHolder;
    /**
     * TCP API Client
     */
    private final ApiClient apiClient;
    /**
     * Digital Sensor Manager
     */
    private final DigitalSensorManager sensorManager;

    public WeightServiceTaskImpl(WeightDataHolder weightDataHolder,
                                 ApiClient apiClient,
                                 DigitalSensorManager sensorManager) {
        this.weightDataHolder = weightDataHolder;
        this.apiClient = apiClient;
        this.sensorManager = sensorManager;
    }

    @Override
    public void setup() {
        //TODO 初始换传感器控制组件

        buildDigitalSensors();
        sensorManager.startReading();
    }

    private void buildDigitalSensors() {
        for (DeviceConnection conn : weightDataHolder.getConnections()) {
            int count = (int) weightDataHolder.getWeightSensors().stream().filter((a) -> a.getConnectionId().equals(conn.getId())).count();
            if (count <= 0) {
                continue;
            }
            DigitalSensorGroup group = sensorManager.NewGroup();
            switch (conn.getType().intValue()) {
                default: {
                    log.info(TAG, "Unknow connection type: " + conn.getType());
                    break;
                }
                case DeviceConnection.TYPE_SERIAL: {
                    log.debug(TAG, "Add group on serial: " + conn.getTarget());
                    group.setCommMode(DigitalSensorGroup.ECommMode.Com);
                    group.setCommSerial(conn.getTarget());
                    break;
                }
                case DeviceConnection.TYPE_NET: {
                    log.debug(TAG, "Add group on tcp: " + conn.getTarget());
                    String[] parts = conn.getTarget().split(":");
                    group.setCommMode(DigitalSensorGroup.ECommMode.Net);
                    group.setCommAddress(parts[0]);
                    if (parts.length > 1) {
                        group.setCommPort(Integer.parseInt(parts[1]));
                    } else {
                        final int defaultPort = 10086;
                        log.info(TAG, "Use default net port: " + defaultPort);
                        group.setCommPort(defaultPort);
                    }
                    break;
                }
            }
            log.debug(TAG, "Build sensors: " + count);
            group.BuildSensors(count);

            int pos = 0;
            for (WeightSensor sen : weightDataHolder.getWeightSensors()) {
                if (sen.getConnectionId().equals(conn.getId())) {
                    log.debug(TAG, "Config sensor: conn=" + conn + ", sen=" + sen);
                    val sensor = group.getSensors().get(pos++);
                    val params = sensor.getParams();
                    params.setAddress(sen.getAddress());
                    params.setDeviceSn(sen.getDeviceSn());
                }
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
            return sensorManager.isOpened();
        } else {
            return false;
        }
    }

    @Override
    public void beforeStop() {
        if (sensorManager != null) {
            sensorManager.close();
        }
    }

    @Override
    public void afterStop() {

    }
}
