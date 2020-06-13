package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 14:58
 * Class Name: MessageTimeoutCheckerTask
 * Author: Levent8421
 * Description:
 * Message Timeout Checker Task
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class MessageTimeoutCheckerTask {
    private final ApiClient apiClient;

    public MessageTimeoutCheckerTask(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Scheduled(fixedRate = ApplicationConstants.Message.TIMEOUT_CHECK_INTERVAL)
    public void check() {
        apiClient.checkTimeout();
    }
}
