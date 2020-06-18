package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.util.HeartbeatHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 10:04
 * Class Name: ApiClientCheckerTask
 * Author: Levent8421
 * Description:
 * API Client Connection Status Checker
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class ApiClientCheckerTask {
    private final ApiClient apiClient;
    private final HeartbeatHelper heartbeatHelper;

    public ApiClientCheckerTask(ApiClient apiClient, HeartbeatHelper heartbeatHelper) {
        this.apiClient = apiClient;
        this.heartbeatHelper = heartbeatHelper;
    }

    @Scheduled(fixedRate = 3000)
    public void check() {
        if (!apiClient.isConnected()) {
            try {
                apiClient.connect();
                if (apiClient.isConnected()) {
                    heartbeatHelper.sendHeartbeat();
                }
            } catch (TcpConnectionException e) {
                log.error("Error On Connect API Server!", e);
            }
        }
    }
}
