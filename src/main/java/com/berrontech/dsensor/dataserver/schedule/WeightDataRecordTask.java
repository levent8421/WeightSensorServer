package com.berrontech.dsensor.dataserver.schedule;

import com.berrontech.dsensor.dataserver.common.entity.WeightDataRecord;
import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.service.general.WeightDataRecordService;
import com.berrontech.dsensor.dataserver.weight.WeightController;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Create By Levent8421
 * Create Time: 2020/12/8 18:03
 * Class Name: WeightDataRecordTask
 * Author: Levent8421
 * Description:
 * 重力传感器数据记录调度组件
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class WeightDataRecordTask implements Runnable {
    private static final int RECORD_INIT_DELAY = 60 * 60 * 1000;
    private static final int RECORD_RUN_DURATION = 60 * 60 * 1000;
    private static final String RECORD_CLEANUP_CRON = "0 0 0 * * ?";
    private final ExecutorService threadPool = ThreadUtils.createSingleThreadPool("WeightDataRecord");
    private final WeightDataRecordService weightDataRecordService;
    private final WeightDataHolder weightDataHolder;
    private final WeightController weightController;

    public WeightDataRecordTask(WeightDataRecordService weightDataRecordService,
                                WeightDataHolder weightDataHolder,
                                WeightController weightController) {
        this.weightDataRecordService = weightDataRecordService;
        this.weightDataHolder = weightDataHolder;
        this.weightController = weightController;
    }

    @Scheduled(initialDelay = RECORD_INIT_DELAY, fixedRate = RECORD_RUN_DURATION)
    public void scheduleRecord() {
        log.info("Starting weight data record task......");
        try {
            threadPool.execute(this);
            log.info("Started weight data record task!");
        } catch (Exception e) {
            log.error("Error on start record task!", e);
        }
    }

    @Scheduled(cron = RECORD_CLEANUP_CRON)
    public void scheduleCleanup() {
        weightDataRecordService.cleanup();
    }

    @Override
    public void run() {
        final List<WeightSensor> sensors = weightDataHolder.getWeightSensors();
        final List<WeightDataRecord> records = new ArrayList<>();
        for (WeightSensor sensor : sensors) {
            final Integer connectionId = sensor.getConnectionId();
            final Integer address = sensor.getAddress();
            try {
                final WeightDataRecord record = weightController.getSensorRecord(connectionId, address);
                records.add(record);
            } catch (Exception e) {
                log.error("Error on fetch sensor record from weight controller!", e);
            }
        }
        if (records.size() > 0) {
            try {
                weightDataRecordService.save(records);
                log.info("Save sensor records , res=[{}]", records.size());
            } catch (Exception e) {
                log.error("Error on save weight data!", e);
            }
        }
    }
}
