package com.berrontech.dsensor.dataserver.runner;

import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.weight.task.SensorMetaDataService;
import com.berrontech.dsensor.dataserver.weight.task.WeightServiceTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 17:10
 * Class Name: WeightApplicationRunner
 * Author: Levent8421
 * Description:
 * 启动Weight线程
 *
 * @author Levent8421
 */
@Slf4j
@Component
@Order(2)
public class WeightApplicationRunner implements ApplicationRunner, Runnable, DisposableBean {
    private static final String THREAD_NAME = "Weight-Service";
    private static final ExecutorService EXECUTOR = ThreadUtils.createSingleThreadPool(THREAD_NAME);
    private final WeightServiceTask weightServiceTask;
    private final SensorMetaDataService sensorMetaDataService;

    public WeightApplicationRunner(WeightServiceTask weightServiceTask,
                                   SensorMetaDataService sensorMetaDataService) {
        this.weightServiceTask = weightServiceTask;
        this.sensorMetaDataService = sensorMetaDataService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.debug("Weight Service Application Runner!");
        EXECUTOR.execute(this);
    }

    @Override
    public void run() {
        loadSensorTable();
        weightServiceTask.setup();
        while (true) {
            if (!weightServiceTask.loop()) {
                weightServiceTask.beforeStop();
                break;
            }
        }
        weightServiceTask.afterStop();
    }

    @Override
    public void destroy() {
        weightServiceTask.beforeStop();
        EXECUTOR.shutdownNow();
        weightServiceTask.afterStop();
    }

    private void loadSensorTable() {
        sensorMetaDataService.refreshSlotTable();
    }
}
