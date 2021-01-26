package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.HandlerAutoMapping;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListenerManager;
import com.berrontech.dsensor.dataserver.tcpclient.client.nio.HandlerFactory;
import io.netty.channel.ChannelHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 21:14
 * Class Name: MessageDispatcherHandlerFactory
 * Author: Levent8421
 * Description:
 * (Message Dispatcher)Handler Factory
 *
 * @author Levent8421
 */
@ChannelHandlerMapping
public class MessageDispatcherHandlerFactory implements HandlerFactory, ApplicationContextAware {
    private MessageDispatcherHandler handler;
    private final MessageListenerManager messageListenerManager;
    private ApplicationContext applicationContext;

    public MessageDispatcherHandlerFactory(MessageListenerManager messageListenerManager) {
        this.messageListenerManager = messageListenerManager;
    }
    
    @Override
    public int getOrder() {
        return 130;
    }

    @Override
    public ChannelHandler createHandler() {
        if (handler == null) {
            synchronized (this) {
                if (handler == null) {
                    final HandlerAutoMapping autoMapping = applicationContext.getBean(HandlerAutoMapping.class);
                    handler = new MessageDispatcherHandler(messageListenerManager, autoMapping);
                }
            }
        }
        return handler;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
