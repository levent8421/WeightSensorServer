package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/8/13 20:57
 * Class Name: MessageNotifyTask
 * Author: Levent8421
 * Description:
 * 消息通知调度任务
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class MessageNotifyTask {
    private static final int TASK_INITIAL_DELAY = 30 * 1000;
    private static final int TASK_INTERVAL = 2 * 60 * 1000;
    private final WeightNotifier weightNotifier;

    public MessageNotifyTask(WeightNotifier weightNotifier) {
        this.weightNotifier = weightNotifier;
    }

    /**
     * 调度方法
     */
    @Scheduled(fixedRate = TASK_INTERVAL, initialDelay = TASK_INITIAL_DELAY)
    public void checkForNotify() {
        final long startTime = System.currentTimeMillis();
        log.info("Check notify start......");
        weightNotifier.checkForNotify();
        final long useTime = System.currentTimeMillis() - startTime;
        log.info("Check notify end! use time [{}]", useTime);
    }
}
