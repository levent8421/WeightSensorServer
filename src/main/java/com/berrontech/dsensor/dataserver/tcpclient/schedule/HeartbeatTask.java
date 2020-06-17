package com.berrontech.dsensor.dataserver.tcpclient.schedule;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.MessageInfo;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.Heartbeat;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
public class HeartbeatTask implements MessageListener {
    private final ApiClient apiClient;

    public HeartbeatTask(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Scheduled(fixedRate = ApplicationConstants.Message.HEARTBEAT_INTERVAL)
    public void heartbeat() {
        if (apiClient.isConnected()) {
            sendHeartbeat();
        }
    }

    private void sendHeartbeat() {
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

    @Override
    public void beforeSend(MessageInfo messageInfo) {
        // Do Nothing
    }

    @Override
    public void onTimeout(MessageInfo messageInfo) {
        log.warn("Error[Timeout] On Send Heartbeat! seqNo=[{}]", messageInfo.getMessage().getSeqNo());
    }

    @Override
    public void onReply(MessageInfo messageInfo, Message reply) {
        final Payload payload = MessageUtils.asResponsePayload(reply.getData(), null);
        log.info("Heartbeat Reply [{}],reply=[{},{}]", reply.getSeqNo(), payload.getCode(), payload.getMsg());
    }

    @Override
    public void onError(MessageInfo messageInfo, Throwable error) {
        log.warn("Error On Send Heartbeat seqNo=[{}]", messageInfo.getMessage().getSeqNo(), error);
    }
}
