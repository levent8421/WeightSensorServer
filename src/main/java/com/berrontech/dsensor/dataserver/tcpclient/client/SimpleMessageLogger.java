package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/8/1 14:40
 * Class Name: SimpleMessageLogger
 * Author: Levent8421
 * Description:
 * 消息日志实现
 *
 * @author Levent8421
 */
@Component
public class SimpleMessageLogger implements MessageLogger {
    private final static int MAX_LOG_SIZE = 20;
    private final LinkedList<Message> messageList = new LinkedList<>();

    @Override
    public void pushMessage(Message message) {
        while (messageList.size() >= MAX_LOG_SIZE) {
            messageList.pollLast();
        }
        messageList.addFirst(message);
    }

    @Override
    public List<Message> messageList() {
        return messageList;
    }
}
