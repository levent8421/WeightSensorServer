package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.notify.WeightNotifier;
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
    private final WeightNotifier weightNotifier;

    public ApiClientCheckerTask(ApiClient apiClient, WeightNotifier weightNotifier) {
        this.apiClient = apiClient;
        this.weightNotifier = weightNotifier;
    }

    @Scheduled(fixedRate = 3000)
    public void check() {
        if (!apiClient.isConnected()) {
            try {
                apiClient.connect();
                if (apiClient.isConnected()) {
                    weightNotifier.heartbeat();
                }
            } catch (TcpConnectionException e) {
                log.error("API Connect Error, [{}:{}]", e.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
