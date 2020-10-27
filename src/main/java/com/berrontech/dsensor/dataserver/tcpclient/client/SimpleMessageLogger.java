package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
@Slf4j
public class SimpleMessageLogger implements MessageLogger {
    private final static int MAX_LOG_SIZE = 20;
    private final Queue<Message> messageList = new LinkedList<>();

    @Override
    public void pushMessage(Message message) {
        cleanExpiredMessage();
        messageList.add(message);
    }

    @Override
    public List<Message> messageList() {
        return new ArrayList<>(messageList);
    }

    @Override
    public void cleanExpiredMessage() {
        final int count2Remove = messageList.size() - MAX_LOG_SIZE + 1;
        if (count2Remove <= 0) {
            return;
        }
        if (count2Remove != 1) {
            log.error("Warning!!!!!!!!!: Remove [{}] messages from log queue, at Thread [{}]",
                    count2Remove,
                    Thread.currentThread().getName());
        }
        for (int i = 0; i < count2Remove; i++) {
            try {
                messageList.poll();
            } catch (Exception e) {
                log.error("Error on poll message from message log queue!", e);
            }
        }
    }
}
