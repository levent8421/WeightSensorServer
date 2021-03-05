package com.berrontech.dsensor.dataserver.schedule;

import com.berrontech.dsensor.dataserver.service.general.TempHumidityLogService;
import com.berrontech.dsensor.dataserver.weight.holder.MemoryTemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TemperatureHumidityLogTask {
    private final WeightDataHolder weightDataHolder;
    private final TempHumidityLogService tempHumidityLogService;

    public TemperatureHumidityLogTask(WeightDataHolder weightDataHolder,
                                      TempHumidityLogService tempHumidityLogService) {
        this.weightDataHolder = weightDataHolder;
        this.tempHumidityLogService = tempHumidityLogService;
    }

    /**
     * Log th data
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void log() {
        log.info(" Start TempHumidity log");
        final Set<Map.Entry<Integer, MemoryTemperatureHumiditySensor>> sensors = weightDataHolder.getTemperatureHumiditySensorTable().entrySet();
        for (Map.Entry<Integer, MemoryTemperatureHumiditySensor> sensorEntry : sensors) {
            final MemoryTemperatureHumiditySensor sensor = sensorEntry.getValue();
            tempHumidityLogService.log(sensor, sensor.getData());
        }
    }

    /**
     * Clean log at 00:00:00 evey days
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void cleanLogTask() {
        log.info("Clean tempHumidity logs");
        tempHumidityLogService.cleanLog();
    }
}
