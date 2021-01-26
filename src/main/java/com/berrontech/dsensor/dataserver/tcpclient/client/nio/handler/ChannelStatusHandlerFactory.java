package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.ChannelStatus;
import com.berrontech.dsensor.dataserver.tcpclient.client.nio.HandlerFactory;
import io.netty.channel.ChannelHandler;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 21:12
 * Class Name: ChannelStatusHandlerFactory
 * Author: Levent8421
 * Description:
 * Channel State Handler Factory
 *
 * @author Levent8421
 */
@ChannelHandlerMapping
public class ChannelStatusHandlerFactory implements HandlerFactory {
    private final ChannelStatusHandler handler;

    public ChannelStatusHandlerFactory(ChannelStatus channelStatus) {
        this.handler = new ChannelStatusHandler(channelStatus);
    }

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public ChannelHandler createHandler() {
        return handler;
    }
}
