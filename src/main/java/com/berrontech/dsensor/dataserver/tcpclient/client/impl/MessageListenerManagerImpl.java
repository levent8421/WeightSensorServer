package com.berrontech.dsensor.dataserver.tcpclient.client.impl;

import com.berrontech.dsensor.dataserver.common.util.CollectionUtils;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListenerManager;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 17:52
 * Class Name: MessageListenerManagerImpl
 * Author: Levent8421
 * Description:
 * 消息管理器实现
 *
 * @author Levent8421
 */
@Slf4j
@Component
public class MessageListenerManagerImpl implements MessageListenerManager, Runnable, ThreadFactory {
    private static final int CHECK_DURATION = 5 * 1000;
    private ExecutorService threadPool;
    private final Map<String, MessageInfo> messageTable = Collections.synchronizedMap(new HashMap<>());
    private volatile boolean running;

    @Override
    public void register(ApiClient apiClient, Message message, MessageListener messageListener, int timeout, int maxRetry) {
        final MessageInfo info = new MessageInfo(apiClient, message, messageListener, timeout, maxRetry);
        messageTable.put(message.getSeqNo(), info);
    }

    @Override
    public void callReply(String seqNo, Message response) {
        final MessageInfo info = messageTable.get(seqNo);
        if (info == null) {
            log.warn("Can not find message for SeqNo[{}]", seqNo);
            return;
        }
        final Lock lock = info.getLock();
        lock.lock();
        try {
            info.getMessageListener().onReply(info, response);
            messageTable.remove(seqNo);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {
        stop();
        running = true;
        threadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), this);
        threadPool.execute(this);
    }

    @Override
    public void stop() {
        running = false;
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
        threadPool = null;
    }

    @Override
    public void run() {
        while (running) {
            checkTimeout();
            try {
                TimeUnit.MILLISECONDS.sleep(CHECK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkTimeout() {
        final long now = System.currentTimeMillis();
        final List<String> completedMessage = new ArrayList<>();
        final Map<String, MessageInfo> messageInfoMap;
        synchronized (messageTable) {
            messageInfoMap = CollectionUtils.copy(messageTable);
        }
        for (Map.Entry<String, MessageInfo> entry : messageInfoMap.entrySet()) {
            final MessageInfo message = entry.getValue();
            final String seqNo = entry.getKey();
            if (now - message.getSendTime() > message.getTimeout()) {
                if (message.getRetry() >= message.getMaxRetry()) {
                    message.getMessageListener().onTimeout(message);
                    completedMessage.add(seqNo);
                } else {
                    try {
                        retrySend(message);
                    } catch (MessageException e) {
                        message.getMessageListener().onError(message, e);
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
        messageInfo.setRetry(messageInfo.getRetry() + 1);
        messageInfo.getApiClient().send(messageInfo.getMessage(), messageInfo.getTimeout(), messageInfo.getMessageListener());
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "MessageManager");
    }
}
