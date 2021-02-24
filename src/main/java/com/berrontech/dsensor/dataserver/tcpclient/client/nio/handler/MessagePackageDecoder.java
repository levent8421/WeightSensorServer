package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.alibaba.fastjson.JSON;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageMetadata;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 16:09
 * Class Name: MessagePackageDecoder
 * Author: Levent8421
 * Description:
 * Byte to  messageObject decoder
 *
 * @author Levent8421
 */
@Slf4j
public class MessagePackageDecoder extends ByteToMessageDecoder {
    private final byte[] buffer = new byte[MessageMetadata.MESSAGE_FRAME_MAS_LENGTH];

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) {
        final int length = byteBuf.readableBytes();
        log.debug("Chanel Read: [{}]", length);
        byteBuf.readBytes(buffer, 0, length);
        final Message message = JSON.parseObject(buffer, 0, length, MessageMetadata.MESSAGE_CHARSET, Message.class);
        if (message != null) {
            out.add(message);
            log.info("Receiver message [{}/{}:{}]:[{}]",
                    message.getType(), message.getAction(), message.getSeqNo(), new String(buffer, 0, length));
        }
    }
}
