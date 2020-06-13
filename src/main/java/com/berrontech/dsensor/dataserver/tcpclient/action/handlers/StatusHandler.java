package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.RuokVo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.lang.management.ManagementFactory;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 15:36
 * Class Name: StatusHandler
 * Author: Levent8421
 * Description:
 * Service Status Action Handler
 *
 * @author Levent8421
 */
@ActionHandlerMapping("ruok.get")
@Slf4j
public class StatusHandler implements ActionHandler {
    private final ApiClient apiClient;

    public StatusHandler(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Message onMessage(Message message) {
        val data = new RuokVo();
        data.setAppVersion(ApplicationConstants.Context.APP_VERSION);
        data.setDbVersion("1.2.1");
        data.setConnectionStatus(apiClient.isConnected() ? "Connected" : "disconnected");
        data.setPid(getPid());
        val payload = Payload.ok(data);
        return MessageUtils.replyMessage(message, payload);
    }

    private int getPid() {
        val runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        val pidPair = runtimeName.split("@");
        if (pidPair.length <= 0) {
            return -1;
        }
        try {
            return Integer.parseInt(pidPair[0]);
        } catch (Exception e) {
            log.warn("Invalidate pid {}", pidPair[0]);
            return -1;
        }
    }
}
