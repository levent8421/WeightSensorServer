package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.util.HeartbeatHelper;
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
    private final HeartbeatHelper heartbeatHelper;

    public HeartbeatTask(ApiClient apiClient, HeartbeatHelper heartbeatHelper) {
        this.apiClient = apiClient;
        this.heartbeatHelper = heartbeatHelper;
    }

    @Scheduled(fixedRate = ApplicationConstants.Message.HEARTBEAT_INTERVAL,
            initialDelay = ApplicationConstants.Message.HEARTBEAT_INTERVAL)
    public void heartbeat() {
        if (apiClient.isConnected()) {
            heartbeatHelper.sendHeartbeat();
        }
    }
}
