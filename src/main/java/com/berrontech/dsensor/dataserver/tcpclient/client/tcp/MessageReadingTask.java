package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 17:18
 * Class Name: MessageReadingTask
 * Author: Levent8421
 * Description:
 * Message Reading Task
 *
 * @author Levent8421
 */
@Slf4j
public class MessageReadingTask implements Runnable, NewPackageListener {
    private static final int MIN_PACKAGE_LEN = 2;
    private InputStream inputStream;
    private ApiClient apiClient;
    private final PackageSplitter packageSplitter;
    private final MessageSerializer messageSerializer;
    @Setter
    private MessageReadListener listener;
    private final byte[] buffer = new byte[1024];
    private boolean running = false;

    public MessageReadingTask(PackageSplitter packageSplitter, MessageSerializer messageSerializer) {
        this.packageSplitter = packageSplitter;
        packageSplitter.setNewPackageListener(this);
        this.messageSerializer = messageSerializer;
    }

    public void setup(ApiClient apiClient) throws TcpConnectionException {
        running = true;
        this.inputStream = apiClient.getInputStream();
        this.apiClient = apiClient;
    }

    @Override
    public void run() {
        log.info("Message Reading Task Start!");
        while (apiClient.isConnected() && running) {
            try {
                int len = inputStream.read(buffer);
                packageSplitter.appendDate(buffer, 0, len);
            } catch (IOException e) {
                this.listener.onReadError(e);
            }
        }
        log.info("Message Reading Task Exit!");
    }

    @Override
    public void onNewPackage(byte[] packet) {
        if (packet.length <= MIN_PACKAGE_LEN) {
            log.warn("Recv a empty message, package length<=2");
        }
        final Message message;
        try {
            message = messageSerializer.deserialization(packet);
        } catch (MessageException e) {
            this.listener.onReadError(e);
            return;
        }
        if (message == null) {
            this.listener.onReadError(new MessageException("Read A null message!"));
            return;
        }
        log.debug("Resolve Message [{}/{}/{}]", message.getType(), message.getAction(), message.getSeqNo());
        listener.onNewMessage(message);
    }

    public void stop() {
        running = false;
    }

    public interface MessageReadListener {
        /**
         * 当接受到新的数据包时调用
         *
         * @param message message
         */
        void onNewMessage(Message message);

        /**
         * 发生错误时嗲用
         *
         * @param error error
         */
        void onReadError(Throwable error);
    }
}
