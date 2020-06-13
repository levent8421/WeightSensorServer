package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.Data;

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
    private Message message;
    private MessageListener messageListener;
    private int timeout;
    private long sendTime;
    private int retry;
    private int maxRetry;
}
