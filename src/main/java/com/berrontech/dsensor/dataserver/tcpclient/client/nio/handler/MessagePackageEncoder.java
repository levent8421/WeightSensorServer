package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.alibaba.fastjson.JSON;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageMetadata;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 16:19
 * Class Name: MessagePackageEncoder
 * Author: Levent8421
 * Description:
 * Message Package Encoder messageObject -> bytes
 *
 * @author Levent8421
 */
@ChannelHandler.Sharable
public class MessagePackageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf out) throws Exception {
        if (message == null) {
            return;
        }
        final byte[] bytes = JSON.toJSONBytes(message);
        out.writeBytes(MessageMetadata.PROTOCOL_START_BYTES);
        out.writeBytes(bytes);
        out.writeBytes(MessageMetadata.PROTOCOL_END_BYTES);
    }
}
