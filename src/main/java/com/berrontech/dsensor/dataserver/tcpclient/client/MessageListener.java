package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.client.tcp.MessageInfo;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 16:55
 * Class Name: MessageListener
 * Author: Levent8421
 * Description:
 * Message Listener
 *
 * @author Levent8421
 */
public interface MessageListener {
    /**
     * 发送前调用
     *
     * @param messageInfo messageInfo
     */
    void beforeSend(MessageInfo messageInfo);

    /**
     * 超时时调用
     *
     * @param messageInfo messageInformation
     */
    void onTimeout(MessageInfo messageInfo);

    /**
     * 回复时调用
     *
     * @param reply       replyMessage
     * @param messageInfo messageInfo
     */
    void onReply(MessageInfo messageInfo, Message reply);

    /**
     * 出错时调用
     *
     * @param messageInfo messageInfo
     * @param error       error
     */
    void onError(MessageInfo messageInfo, Throwable error);
}
