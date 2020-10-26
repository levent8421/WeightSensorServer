package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 10:14
 * Class Name: MessageQueue
 * Author: Levent8421
 * Description:
 * Message Queue
 *
 * @author Levent8421
 */
@Slf4j
public class MessageQueue {
    private static final int MAX_QUEUE_SIZE = 1024;
    private static final int FULL_QUEUE_SIZE = 1000;
    private final BlockingQueue<MessageInfo> messageInfoQueue = new LinkedBlockingDeque<>(MAX_QUEUE_SIZE);

    public boolean offer(Message message, int timeout, int maxRetry, MessageListener listener) {
        final MessageInfo msgInfo = new MessageInfo();
        msgInfo.setMessage(message);
        msgInfo.setTimeout(timeout);
        msgInfo.setMaxRetry(maxRetry);
        msgInfo.setRetry(0);
        msgInfo.setMessageListener(listener);
        return offer(msgInfo);
    }

    public synchronized boolean offer(MessageInfo messageInfo) {
        makeQueueNotFull();
        try {
            messageInfoQueue.put(messageInfo);
            return true;
        } catch (InterruptedException e) {
            log.error("Error On Put Message Into Queue!", e);
            return false;
        }
    }

    private void makeQueueNotFull() {
        final long now = System.currentTimeMillis();
        while (messageInfoQueue.size() > FULL_QUEUE_SIZE) {
            try {
                final MessageInfo messageInfo = poll();
                final Message message = messageInfo.getMessage();
                final String seqNo = message.getSeqNo();
                final long before = now - messageInfo.getSendTime();
                log.warn("Give up message:[{}/{}/{}], send before [{}], msg=[{}]",
                        seqNo, message.getAction(), messageInfo.getRetry(), before, message.asJsonString());
            } catch (MessageException e) {
                log.error("Error on poll message from queue!", e);
            }
        }
    }

    public boolean hasMoreMessage() {
        return messageInfoQueue.size() > 0;
    }

    public MessageInfo poll() throws MessageException {
        try {
            return messageInfoQueue.take();
        } catch (InterruptedException e) {
            log.error("Error On Take Message Form Queue!", e);
            throw new MessageException("Error On Take Message Form Queue!", e);
        }
    }
}
