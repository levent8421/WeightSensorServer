package com.berrontech.dsensor.dataserver.runner;

import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListenerManager;
import com.berrontech.dsensor.dataserver.tcpclient.client.nio.ChannelBasedApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 下午2:07
 * Class Name: TcpApiRunner
 * Author: Levent8421
 * Description:
 * TcpApiRunner
 * TCP API Runner
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class TcpApiRunner implements ApplicationRunner, Runnable, ThreadFactory {
    /**
     * 时重节拍：30s
     */
    private static final int ONE_TICKS = 3;
    /**
     * 状态上报 1个时钟节拍
     */
    private static final int STATE_CHANGED_NOTIFY_TICKS = 10;
    /**
     * 重量变化通知 4个时钟节拍
     */
    private static final int WEIGHT_CHANGED_NOTIFY_TICKS = 40;
    /**
     * 心跳通知 4个时钟节拍
     */
    private static final int HEARTBEAT_NOTIFY_TICKS = 40;
    /**
     * 连接检查周期 1个时钟节拍
     */
    private static final int CONNECTION_CHECK_TICKS = 2;

    private final ChannelBasedApiClient apiClient;
    private final WeightNotifier weightNotifier;
    private final MessageListenerManager messageListenerManager;
    private ExecutorService threadPool;
    private long ticks = 0;
    private volatile boolean running;

    public TcpApiRunner(ChannelBasedApiClient apiClient,
                        WeightNotifier weightNotifier,
                        MessageListenerManager messageListenerManager) {
        this.apiClient = apiClient;
        this.weightNotifier = weightNotifier;
        this.messageListenerManager = messageListenerManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        running = true;
        messageListenerManager.start();
        threadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), this);
        threadPool.execute(this);
    }

    @Override
    public void run() {
        while (running) {
            ticks++;
            if (ticks % CONNECTION_CHECK_TICKS == 0) {
                checkConnection();
            }
            if (ticks % HEARTBEAT_NOTIFY_TICKS == 0) {
                doHeartbeat();
            }
            if (ticks % STATE_CHANGED_NOTIFY_TICKS == 0) {
                doNotifyStateChanged();
            }
            if (ticks % WEIGHT_CHANGED_NOTIFY_TICKS == 0) {
                doNotifyWeightChanged();
            }
            try {
                TimeUnit.SECONDS.sleep(ONE_TICKS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doNotifyWeightChanged() {
        try {
            weightNotifier.checkForWeightChangedNotify();
        } catch (Exception e) {
            log.error("Error on do Notify Weight Changed!", e);
        }
    }

    private void doNotifyStateChanged() {
        try {
            weightNotifier.checkForStateChangedNotify();
        } catch (Exception e) {
            log.error("Error on do Notify State Changed!", e);
        }
    }

    private void checkConnection() {
        try {
            if (!apiClient.isConnected()) {
                apiClient.connect();
                doHeartbeat();
            }
        } catch (Exception e) {
            final String error = ExceptionUtils.getMessage(e);
            log.error("Check and reconnect TCP! error=[{}]", error);
        }
    }

    private void doHeartbeat() {
        weightNotifier.heartbeat();
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

    @PreDestroy
    public void destroy() {
        running = false;
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
            log.info("Waiting 10s for notify ticks thread......");
        } catch (InterruptedException e) {
            log.info("Shutdown Force!");
            threadPool.shutdownNow();
        }
    }
}
