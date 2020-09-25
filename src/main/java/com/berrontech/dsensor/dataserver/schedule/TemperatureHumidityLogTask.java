package com.berrontech.dsensor.dataserver.schedule;

import com.berrontech.dsensor.dataserver.service.general.TempHumidityLogService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Create By Levent8421
 * Create Time: 2020/9/24 15:12
 * Class Name: TemperatureHumidityLogTask
 * Author: Levent8421
 * Description:
 * 温湿度日志记录任务
 *
 * @author Levent8421
 */
@Component
public class TemperatureHumidityLogTask {
    private final WeightDataHolder weightDataHolder;
    private final TempHumidityLogService tempHumidityLogService;

    public TemperatureHumidityLogTask(WeightDataHolder weightDataHolder,
                                      TempHumidityLogService tempHumidityLogService) {
        this.weightDataHolder = weightDataHolder;
        this.tempHumidityLogService = tempHumidityLogService;
    }

    @Scheduled(cron = "0 0,15,30,45 * * * ? ")
    public void log() {
        tempHumidityLogService.cleanLog();
        final Set<Map.Entry<Integer, MemoryTemperatureHumiditySensor>> sensors = weightDataHolder.getTemperatureHumiditySensorTable().entrySet();
        for (Map.Entry<Integer, MemoryTemperatureHumiditySensor> sensorEntry : sensors) {
            final MemoryTemperatureHumiditySensor sensor = sensorEntry.getValue();
            tempHumidityLogService.log(sensor, sensor.getData());
        }
    }
}
