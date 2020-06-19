package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.service.general.SlotService;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
public class WeightApplicationRunner implements ApplicationRunner, Runnable, DisposableBean {
    private static final String THREAD_NAME = "Weight-Service";
    private static final ExecutorService EXECUTOR = ThreadUtils.createSingleThreadPool(THREAD_NAME);
    private final WeightServiceTask weightServiceTask;
    private final WeightDataHolder weightDataHolder;
    private final SlotService slotService;

    public WeightApplicationRunner(WeightServiceTask weightServiceTask,
                                   WeightDataHolder weightDataHolder, SlotService slotService) {
        this.weightServiceTask = weightServiceTask;
        this.weightDataHolder = weightDataHolder;
        this.slotService = slotService;
    }

    @Override
    public void run(ApplicationArguments args) {
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
        //TODO 从数据库把传感器元数据加载到内存中
    }
}
