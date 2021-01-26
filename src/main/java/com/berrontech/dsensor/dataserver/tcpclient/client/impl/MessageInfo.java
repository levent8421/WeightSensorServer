package com.berrontech.dsensor.dataserver.tcpclient.client.impl;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 10:23
 * Class Name: MessageInfo
 * Author: Levent8421
 * Description:
 * Message Information
 *
 * @author Levent8421
 */
@Data
public class MessageInfo {
    public MessageInfo(ApiClient apiClient, Message message, MessageListener messageListener, int timeout, int maxRetry) {
        this.apiClient = apiClient;
        this.message = message;
        this.messageListener = messageListener;
        this.timeout = timeout;
        this.sendTime = System.currentTimeMillis();
        this.retry = 0;
        this.maxRetry = maxRetry;
    }

    private final ApiClient apiClient;
    private final Message message;
    private final MessageListener messageListener;
    private final int timeout;
    private long sendTime;
    private int retry;
    private final int maxRetry;
    private final Lock lock = new ReentrantLock();
}
