package com.berrontech.dsensor.dataserver.weight.holder;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.entity.Slot;
import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 19:33
 * Class Name: WeightDataHolder
 * Author: Levent8421
 * Description:
 * 称重数据内存保持器
 *
 * @author Levent8421
 */
@Data
@Component
@Scope("singleton")
public class WeightDataHolder implements Serializable {
    /**
     * 货道表
     */
    private Map<String, MemorySlot> slotTable;
    /**
     * 温湿度传感器表
     */
    private Map<Integer, MemoryTemperatureHumiditySensor> temperatureHumiditySensorTable;
    /**
     * Connections
     */
    private List<DeviceConnection> connections;
    /**
     * 传感器列表
     */
    private List<WeightSensor> weightSensors;
    /**
     * 货道列表
     */
    private List<Slot> slots;
    /**
     * 温湿度传感器列表
     */
    private List<TemperatureHumiditySensor> temperatureHumiditySensors;
}
