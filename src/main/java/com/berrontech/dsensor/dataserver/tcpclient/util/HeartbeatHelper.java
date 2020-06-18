package com.berrontech.dsensor.dataserver.tcpclient.util;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.MessageInfo;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.Heartbeat;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:28
 * Class Name: HeartbeatHelper
 * Author: Levent8421
 * Description:
 * 心跳相关工具类
 *
 * @author Levent8421
 */
@Slf4j
@Component
public class HeartbeatHelper implements MessageListener {
    private int numberOfHeartFailure = 0;
    private final ApiClient apiClient;

    public HeartbeatHelper(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void beforeSend(MessageInfo messageInfo) {
        // Do Nothing
    }

    @Override
    public void onTimeout(MessageInfo messageInfo) {
        log.warn("Heartbeat Message Send Timeout! seqNo=[{}]", messageInfo.getMessage().getSeqNo());
        numberOfHeartFailure++;
        checkFailureTimes();
    }

    @Override
    public void onReply(MessageInfo messageInfo, Message reply) {
        log.debug("Heartbeat Message Reply! deqNo=[{}]", messageInfo.getMessage().getSeqNo());
        numberOfHeartFailure = 0;
    }

    @Override
    public void onError(MessageInfo messageInfo, Throwable error) {
        log.warn("Heartbeat Message Send Error! seqNo=[{}]", messageInfo.getMessage().getSeqNo(), error);
        numberOfHeartFailure++;
        checkFailureTimes();
    }

    private void checkFailureTimes() {
        if (numberOfHeartFailure >= ApplicationConstants.Message.MAX_HEART_BEAT_FAILURE_TIMES) {
            apiClient.disconnect();
            this.numberOfHeartFailure = 0;
        }
    }

    public void sendHeartbeat() {
        val heartbeat = new Heartbeat();
        heartbeat.setAlive(true);
        heartbeat.setAppName(ApplicationConstants.Context.APP_NAME);
        heartbeat.setAppVersion(ApplicationConstants.Context.APP_VERSION);
        heartbeat.setDbVersion(ApplicationConstants.Context.APP_VERSION);
        val seqNo = MessageUtils.nextSeqNo();
        val message = MessageUtils.requestMessage(seqNo, ApplicationConstants.Actions.HEARTBEAT, heartbeat);
        try {
            apiClient.send(message, ApplicationConstants.Message.MESSAGE_TIMEOUT, this);
        } catch (MessageException e) {
            log.error("Error on send heart beat!", e);
        }
    }
}
