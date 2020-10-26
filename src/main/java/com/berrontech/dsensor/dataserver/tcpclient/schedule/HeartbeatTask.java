package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 11:47
 * Class Name: HeartbeatTask
 * Author: Levent8421
 * Description:
 * Heartbeat Task
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class HeartbeatTask {
    private final ApiClient apiClient;
    private final WeightNotifier weightNotifier;

    public HeartbeatTask(ApiClient apiClient, WeightNotifier weightNotifier) {
        this.apiClient = apiClient;
        this.weightNotifier = weightNotifier;
    }

    @Scheduled(fixedRate = ApplicationConstants.Message.HEARTBEAT_INTERVAL,
            initialDelay = ApplicationConstants.Message.HEARTBEAT_INTERVAL)
    public void heartbeat() {
        if (apiClient.isConnected()) {
            try {
                log.debug("Try heartbeat!");
                weightNotifier.heartbeat();
            } catch (Exception e) {
                log.warn("Error on notify heartbeat!", e);
            }
        } else {
            log.warn("Heartbeat give up! reason=[TCP NOT CONNECTED!]");
        }
    }
}
