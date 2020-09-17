package com.berrontech.dsensor.dataserver.web.vo;

import com.berrontech.dsensor.dataserver.weight.holder.MemorySlot;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import lombok.Data;

import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/9/17 19:04
 * Class Name: DashboardData
 * Author: Levent8421
 * Description:
 * 数据看板界面数据
 *
 * @author Levent8421
 */
@Data
public class DashboardData {
    /**
     * 重力货道数据
     */
    private Map<String, MemorySlot> slotData;
    /**
     * 温湿度传感器数据
     */
    private Map<Integer, MemoryTemperatureHumiditySensor> temperatureHumidityData;
}
