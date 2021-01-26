package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.client.MessageMetadata;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 16:05
 * Class Name: PackageFrameDecoder
 * Author: Levent8421
 * Description:
 * Channle Handler (Package Frame Decoder)
 *
 * @author Levent8421
 */
public class PackageFrameDecoder extends DelimiterBasedFrameDecoder {
    private static final ByteBuf PACKAGE_START = Unpooled.copiedBuffer(MessageMetadata.PROTOCOL_START_BYTES);
    private static final ByteBuf PACKAGE_END = Unpooled.copiedBuffer(MessageMetadata.PROTOCOL_END_BYTES);

    public PackageFrameDecoder() {
        super(MessageMetadata.MESSAGE_FRAME_MAS_LENGTH, PACKAGE_START, PACKAGE_END);
    }
}
