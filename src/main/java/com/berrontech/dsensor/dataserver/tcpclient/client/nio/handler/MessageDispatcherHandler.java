package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.HandlerAutoMapping;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListenerManager;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 17:39
 * Class Name: MessageDispatcherHandler
 * Author: Levent8421
 * Description:
 * Due with message
 *
 * @author Levent8421
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageDispatcherHandler extends MessageToMessageDecoder<Message> {
    private final HandlerAutoMapping handlerAutoMapping;
    private final MessageListenerManager messageListenerManager;

    public MessageDispatcherHandler(MessageListenerManager messageListenerManager, HandlerAutoMapping handlerAutoMapping) {
        this.messageListenerManager = messageListenerManager;
        this.handlerAutoMapping = handlerAutoMapping;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> out) {
        final String type = message.getType();
        Message response = null;
        switch (type) {
            case Message.TYPE_REQUEST:
                response = doDispatch(message);
                break;
            case Message.TYPE_RESPONSE:
                log.debug("message response=[{}/{}]", message.getSeqNo(), message.getAction());
                messageListenerManager.callReply(message.getSeqNo(), message);
                break;
            default:
                response = MessageUtils.replyMessage(message, Payload.badRequest("Unknown message type:" + type));
        }
        if (response != null) {
            channelHandlerContext.writeAndFlush(response);
        }
    }

    private Message doDispatch(Message message) {
        return handlerAutoMapping.handle(message);
    }
}
