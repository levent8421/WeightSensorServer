package com.berrontech.dsensor.dataserver.tcpclient.action;

import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 15:12
 * Class Name: ActionHandler
 * Author: Levent8421
 * Description:
 * Action Handler
 *
 * @author Levent8421
 */
public interface ActionHandler {
    /**
     * 处理消息
     *
     * @param message message
     * @return reply message
     * @throws Exception Error
     */
    Message onMessage(Message message) throws Exception;
}
