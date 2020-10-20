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
    /**
     * 重量变化通知启动延时
     */
    private static final long WEIGHT_CHANGED_NOTIFY_DELAY = 30 * 1000;
    /**
     * 重量变化通知频率
     */
    private static final long WEIGHT_CHANGED_NOTIFY_INTERVAL = 2 * 60 * 1000;
    /**
     * 状态变化通知启动延时
     */
    private static final long STATE_CHANGED_NOTIFY_DELAY = 15 * 1000;
    /**
     * 状态变化通知频率
     */
    private static final long STATE_CHANGED_NOTIFY_INTERVAL = 30 * 1000;

    private final WeightNotifier weightNotifier;

    public MessageNotifyTask(WeightNotifier weightNotifier) {
        this.weightNotifier = weightNotifier;
    }

    /**
     * 重量变化调度任务
     */
    @Scheduled(initialDelay = WEIGHT_CHANGED_NOTIFY_DELAY, fixedRate = WEIGHT_CHANGED_NOTIFY_INTERVAL)
    public void notifyWeightChanged() {
        final long start = System.currentTimeMillis();
        weightNotifier.checkForWeightChangedNotify();
        log.debug("Check for weightChangedNotify complete, time=[{}]", (System.currentTimeMillis() - start));
    }

    /**
     * 状态变化调度任务
     */
    @Scheduled(initialDelay = STATE_CHANGED_NOTIFY_DELAY, fixedRate = STATE_CHANGED_NOTIFY_INTERVAL)
    public void notifyStateChanged() {
        final long start = System.currentTimeMillis();
        weightNotifier.checkForStateChangedNotify();
        log.debug("Check for stateChangedNotify complete, time=[{}]", (System.currentTimeMillis() - start));
    }
}
