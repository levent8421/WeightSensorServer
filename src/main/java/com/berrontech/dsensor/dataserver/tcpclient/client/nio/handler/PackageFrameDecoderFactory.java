package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.HandlerFactory;
import io.netty.channel.ChannelHandler;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 21:24
 * Class Name: PackageFrameDecoderFactory
 * Author: Levent8421
 * Description:
 * message Package decoder factory
 *
 * @author Levent8421
 */
@ChannelHandlerMapping
public class PackageFrameDecoderFactory implements HandlerFactory {
    @Override
    public int getOrder() {
        return 110;
    }

    @Override
    public ChannelHandler createHandler() {
        return new PackageFrameDecoder();
    }
}
