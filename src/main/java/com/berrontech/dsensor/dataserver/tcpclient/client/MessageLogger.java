package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/8/1 14:08
 * Class Name: MessageLogger
 * Author: Levent8421
 * Description:
 * 消息记录器
 *
 * @author Levent8421
 */
public interface MessageLogger {
    /**
     * Push Message Into Logger
     *
     * @param message message
     */
    void pushMessage(Message message);

    /**
     * 获取所有日志
     */
    List<Message> messageList();
}
