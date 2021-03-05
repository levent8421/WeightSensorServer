package com.berrontech.dsensor.dataserver.tcpclient.client.nio;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListenerManager;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageMetadata;
import com.berrontech.dsensor.dataserver.tcpclient.client.impl.MessageInfo;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create By Levent8421
 * Create Time: 2021/1/25 11:18
 * Class Name: ChannelBasedApiClient
 * Author: Levent8421
 * Description:
 * Channel Based(Netty) ApiClient
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class ChannelBasedApiClient implements ApiClient {
    private EventLoopGroup eventLoopGroup;
    private final ClientChannelInitializer channelInitializer;
    private final ChannelStatus channelStatus;
    private final MessageListenerManager messageListenerManager;

    public ChannelBasedApiClient(ClientChannelInitializer channelInitializer,
                                 ChannelStatus channelStatus,
                                 MessageListenerManager messageListenerManager) {
        this.channelInitializer = channelInitializer;
        this.channelStatus = channelStatus;
        this.messageListenerManager = messageListenerManager;
    }

    private void nettyShutdown() {
        final ChannelHandlerContext context = channelStatus.getChannelHandlerContext();
        if (context != null) {
            try {
                context.disconnect().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException("Error on disconnect apiClient!", e);
            }
        }
        if (eventLoopGroup != null) {
            try {
                eventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException("Error on shutting down eventLoopGroup!", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return channelStatus.isConnected();
    }

    @Override
    public void connect() {
        nettyShutdown();
        final Bootstrap bootstrap = buildServerBootstrap();
        try {
            bootstrap.connect(channelStatus.getHost(), channelStatus.getPort()).sync();
        } catch (InterruptedException e) {
            log.error("Error on create socket connection!", e);
        }
    }

    private Bootstrap buildServerBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer);
        return bootstrap;
    }

    @Override
    public void send(Message message, int timeout, MessageListener listener) throws MessageException {
        final ChannelHandlerContext context = channelStatus.getChannelHandlerContext();
        if (context == null) {
            log.warn("Ignore message [{}/{}/{}]", message.getType(), message.getAction(), message.getSeqNo());
            final MessageInfo messageInfo = new MessageInfo(this, message, listener, timeout, MessageMetadata.MAX_RETRY);
            listener.onError(messageInfo, new IllegalStateException("Connection not ready!"));
            return;
        }
        context.writeAndFlush(message);
        messageListenerManager.register(this, message, listener, timeout, MessageMetadata.MAX_RETRY);
    }

    @Override
    public void disconnect() {
        nettyShutdown();
    }
}
