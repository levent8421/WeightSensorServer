package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 17:50
 * Class Name: MessageListenerManager
 * Author: Levent8421
 * Description:
 * 消息管理器
 *
 * @author Levent8421
 */
public interface MessageListenerManager {
    /**
     * 注册消息到管理器
     *
     * @param message         消息
     * @param messageListener listener
     * @param timeout         timout ms
     * @param apiClient       API Client
     * @param maxRetry        max retry
     */
    void register(ApiClient apiClient, Message message, MessageListener messageListener, int timeout, int maxRetry);

    /**
     * 移除消息
     *
     * @param seqNo    序列号
     * @param response 回应
     */
    void callReply(String seqNo, Message response);

    /**
     * 开启后台监控线程
     */
    void start();

    /**
     * 关闭后台监控线程
     */
    void stop();
}
