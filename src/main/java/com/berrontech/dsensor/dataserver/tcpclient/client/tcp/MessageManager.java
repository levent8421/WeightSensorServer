package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.common.util.CollectionUtils;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 10:49
 * Class Name: MessageManager
 * Author: Levent8421
 * Description:
 * Message Manager
 *
 * @author Levent8421
 */
@Slf4j
public class MessageManager {
    private final Map<String, MessageInfo> messageTable = new HashMap<>();
    private MessageQueue messageQueue;

    public MessageManager(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void addMessage(MessageInfo messageInfo) {
        if (messageInfo.getMessageListener() == null) {
            log.warn("No Message Listener! ");
            return;
        }
        messageInfo.setSendTime(System.currentTimeMillis());
        val seqNo = messageInfo.getMessage().getSeqNo();
        messageTable.put(seqNo, messageInfo);
        callBeforeSend(messageInfo);
    }

    public void checkTimeout() {
        val now = System.currentTimeMillis();
        val completedMessage = new ArrayList<String>();
        final Map<String, MessageInfo> messageInfoMap;
        synchronized (messageTable) {
            messageInfoMap = CollectionUtils.copy(messageTable);
        }
        for (Map.Entry<String, MessageInfo> entry : messageInfoMap.entrySet()) {
            val message = entry.getValue();
            val seqNo = entry.getKey();
            if (now - message.getSendTime() > message.getTimeout()) {
                if (message.getRetry() >= message.getMaxRetry()) {
                    callTimeout(message);
                    completedMessage.add(seqNo);
                } else {
                    try {
                        retrySend(message);
                    } catch (MessageException e) {
                        callOnError(message, e);
                        completedMessage.add(seqNo);
                    }
                }
            }
        }
        for (String seqNo : completedMessage) {
            messageTable.remove(seqNo);
        }
    }

    private void retrySend(MessageInfo messageInfo) throws MessageException {
        val seqNo = messageInfo.getMessage().getSeqNo();
        messageInfo.setRetry(messageInfo.getRetry() + 1);
        log.debug("Retry Send Message: [{}], retry=[{}/{}]", seqNo, messageInfo.getRetry(), messageInfo.getMaxRetry());
        if (!messageQueue.offer(messageInfo)) {
            throw new MessageException("Error On Retry Send Message!");
        }
    }

    public void reply(Message reply) {
        val seqNo = reply.getSeqNo();
        if (messageTable.containsKey(seqNo)) {
            val message = messageTable.remove(seqNo);
            callReply(message, reply);
        } else {
            log.warn("Resolve A Invalidate Reply:[{}]", reply);
        }
    }

    private void callReply(MessageInfo messageInfo, Message reply) {
        try {
            messageInfo.getMessageListener().onReply(messageInfo, reply);
        } catch (Exception e) {
            log.warn("Error On Call Reply, msg=[{}], reply=[{}]", messageInfo, reply, e);
        }
    }

    private void callTimeout(MessageInfo messageInfo) {
        try {
            messageInfo.getMessageListener().onTimeout(messageInfo);
        } catch (Exception e) {
            log.warn("Error On Call Timeout, msg=[{}]", messageInfo, e);
        }
    }

    private void callBeforeSend(MessageInfo messageInfo) {
        try {
            messageInfo.getMessageListener().beforeSend(messageInfo);
        } catch (Exception e) {
            log.warn("Error On Call BeforeSend, msg=[{}]", messageInfo, e);
        }
    }

    private void callOnError(MessageInfo messageInfo, Throwable error) {
        try {
            messageInfo.getMessageListener().onError(messageInfo, error);
        } catch (Exception e) {
            log.warn("Error On Call OnError, msg=[{}], error=[{}]", messageInfo, error, e);
        }
    }
}
