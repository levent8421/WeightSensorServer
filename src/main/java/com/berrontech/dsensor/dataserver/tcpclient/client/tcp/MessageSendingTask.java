package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 11:37
 * Class Name: MessageSendingTask
 * Author: Levent8421
 * Description:
 * Message Send Task
 *
 * @author Levent8421
 */
@Slf4j
public class MessageSendingTask implements Runnable {
    private ApiClient apiClient;
    private boolean running = false;
    private final MessageQueue messageQueue;
    private OutputStream outputStream;
    private final MessageSerializer messageSerializer;
    @Setter
    private MessageSendListener listener;

    public MessageSendingTask(MessageSerializer messageSerializer, MessageQueue messageQueue) {
        this.messageSerializer = messageSerializer;
        this.messageQueue = messageQueue;
    }

    public void setup(ApiClient apiClient) throws TcpConnectionException {
        this.apiClient = apiClient;
        outputStream = apiClient.getOutputStream();
        running = true;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        log.info("Message Sending Task Start!");
        while (apiClient.isConnected() && running) {
            try {
                val message = messageQueue.poll();
                sendMessageSync(message);
            } catch (MessageException e) {
                log.error("Error On Take Message From Queue!", e);
            }
        }
        log.info("Message Sending Task Exit!");
    }

    private void sendMessageSync(MessageInfo messageInfo) {
        final byte[] bytes = messageSerializer.serialize(messageInfo.getMessage());
        logSend(messageInfo, bytes);
        try {
            outputStream.write(bytes);
            this.listener.afterSend(messageInfo);
        } catch (IOException e) {
            this.listener.onSendError(messageInfo, e);
        }
    }

    private void logSend(MessageInfo messageInfo, byte[] bytes) {
        final String messageStr = MessageUtils.messageBytes2String(bytes);
        log.debug("Sending Message [{}/{}/{}], package:\r\n{}\r\n",
                messageInfo.getMessage().getType(),
                messageInfo.getMessage().getAction(),
                messageInfo.getMessage().getSeqNo(),
                messageStr);
    }

    /**
     * Message Send Listener
     */
    public interface MessageSendListener {
        /**
         * callback At After Message Send
         *
         * @param messageInfo message Information
         */
        void afterSend(MessageInfo messageInfo);

        /**
         * Callback At Error
         *
         * @param messageInfo message Information
         * @param error       error
         */
        void onSendError(MessageInfo messageInfo, Throwable error);
    }
}
