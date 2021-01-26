package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.HandlerFactory;
import io.netty.channel.ChannelHandler;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 21:20
 * Class Name: MessagePackageDecoderFactory
 * Author: Levent8421
 * Description:
 * Message Package Decoder Factory
 *
 * @author Levent8421
 */
@ChannelHandlerMapping
public class MessagePackageDecoderFactory implements HandlerFactory {
    @Override
    public int getOrder() {
        return 120;
    }

    @Override
    public ChannelHandler createHandler() {
        return new MessagePackageDecoder();
    }
}
