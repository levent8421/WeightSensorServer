package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import com.berrontech.dsensor.dataserver.common.context.ApplicationConstants;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.HandlerAutoMapping;
import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 17:00
 * Class Name: TcpApiClient
 * Author: Levent8421
 * Description:
 * Tcp Api Client
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class TcpApiClient implements ApiClient, DisposableBean,
        MessageReadingTask.MessageReadListener,
        MessageSendingTask.MessageSendListener,
        ApplicationContextAware {
    private Socket socket;
    private final ConnectionConfiguration connectionConfiguration;
    private boolean isConnected = false;
    private final ExecutorService threadPool;
    private final MessageReadingTask messageReadingTask;
    private final MessageSendingTask messageSendingTask;
    private final MessageQueue messageQueue = new MessageQueue();
    private final MessageManager messageManager;
    private HandlerAutoMapping handlerAutoMapping;
    private ApplicationContext applicationContext;

    public TcpApiClient(ConnectionConfiguration connectionConfiguration,
                        PackageSplitter packageSplitter,
                        MessageSerializer messageSerializer) {
        this.connectionConfiguration = connectionConfiguration;
        threadPool = createThreadPool();
        this.messageReadingTask = createMessageReadingTask(packageSplitter, messageSerializer);
        initReadingTask();
        this.messageSendingTask = createMessageSendingTask(messageSerializer);
        initSendingTask();
        messageManager = new MessageManager(messageQueue);
    }

    private MessageSendingTask createMessageSendingTask(MessageSerializer messageSerializer) {
        return new MessageSendingTask(messageSerializer, messageQueue);
    }

    private MessageReadingTask createMessageReadingTask(PackageSplitter packageSplitter, MessageSerializer messageSerializer) {
        return new MessageReadingTask(packageSplitter, messageSerializer);
    }

    private ExecutorService createThreadPool() {
        return new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(), r -> new Thread(r, "TCPAPIClient"));
    }


    private void initReadingTask() {
        messageReadingTask.setListener(this);
    }

    private void initSendingTask() {
        messageSendingTask.setListener(this);
    }

    @Override
    public boolean isConnected() {
        return isConnected && socketStatus();
    }

    private boolean socketStatus() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void connect() throws TcpConnectionException {
        val ip = connectionConfiguration.getIp();
        val port = connectionConfiguration.getPort();
        log.debug("TCPAPI: Connecting [{}:{}]", ip, port);
        try {
            socket = new Socket(ip, port);
            isConnected = true;
            startReadTask();
            startSendTask();
            onConnectSuccess();
        } catch (IOException e) {
            log.error("Error On Create TCP Connection[{}:{}]", ip, port, e);
            throw new TcpConnectionException("Error On Create TCP Connection!", e);
        }
    }

    private void startSendTask() throws TcpConnectionException {
        messageSendingTask.setup(this);
        threadPool.execute(messageSendingTask);
    }

    private void startReadTask() throws TcpConnectionException {
        messageReadingTask.setup(this);
        threadPool.execute(messageReadingTask);
    }

    @Override
    public void send(Message message, int timeout, MessageListener listener) throws MessageException {
        if (!messageQueue.offer(message, timeout, ApplicationConstants.Message.MESSAGE_MAX_RETRY, listener)) {
            throw new MessageException("Offer message into queue failed!");
        }
    }

    @Override
    public void disconnect() {
        if (isConnected) {
            messageReadingTask.stop();
            messageSendingTask.stop();
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Error On Disconnect Socket", e);
            }
        }
    }

    @Override
    public InputStream getInputStream() throws TcpConnectionException {
        if (isConnected()) {
            try {
                return socket.getInputStream();
            } catch (IOException e) {
                throw new TcpConnectionException("Error on Get SocketInputStream!", e);
            }
        }
        throw new TcpConnectionException("TCP Disconnected!");
    }

    @Override
    public OutputStream getOutputStream() throws TcpConnectionException {
        if (isConnected()) {
            try {
                return socket.getOutputStream();
            } catch (IOException e) {
                throw new TcpConnectionException("Error on Get SocketOutputStream!", e);
            }
        }
        throw new TcpConnectionException("TCP Disconnected!");
    }

    @Override
    public void checkTimeout() {
        messageManager.checkTimeout();
    }

    @Override
    public void destroy() {
        disconnect();
    }

    @Override
    public void onNewMessage(Message message) {
        val type = message.getType();
        switch (type) {
            case Message.TYPE_REQUEST:
                handleRequestMessage(message);
                break;
            case Message.TYPE_RESPONSE:
                messageManager.reply(message);
                break;
            default:
                log.warn("Invalidate Message Type: [{}],msg=[{}]", type, message);
        }
    }

    private void handleRequestMessage(Message message) {
        if (handlerAutoMapping == null) {
            handlerAutoMapping = applicationContext.getBean(HandlerAutoMapping.class);
        }
        final Message reply;
        try {
            reply = handlerAutoMapping.handle(message);
        } catch (Exception e) {
            sendServerErrorReply(message, e);
            return;
        }
        if (!Objects.equals(reply.getType(), Message.TYPE_RESPONSE)) {
            throw new IllegalArgumentException("Must Reply a Response Message!" + reply);
        }
        try {
            send(reply, 0, null);
        } catch (MessageException e) {
            log.warn("Error On Send Reply![{}]", reply);
        }
    }

    private void sendServerErrorReply(Message message, Throwable error) {
        val errorString = String.format("Error: type=[%s], msg=[%s]", error.getClass().getName(), error.getMessage());
        val payload = Payload.error(errorString);
        val reply = MessageUtils.replyMessage(message, payload);
        try {
            send(reply, 0, null);
        } catch (MessageException e) {
            log.warn("Error On Send Error Reply", e);
        }
    }

    @Override
    public void onReadError(Throwable error) {
        log.error("Error On Read Message!", error);
        if (error instanceof IOException) {
            isConnected = false;
        }
    }

    @Override
    public void afterSend(MessageInfo messageInfo) {
        val msgType = messageInfo.getMessage().getType();
        if (Objects.equals(msgType, Message.TYPE_REQUEST)) {
            messageManager.addMessage(messageInfo);
        }
    }

    @Override
    public void onSendError(MessageInfo messageInfo, Throwable error) {
        log.error("Error On Send Message!", error);
        if (error instanceof IOException) {
            isConnected = false;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void onConnectSuccess() {

    }
}
