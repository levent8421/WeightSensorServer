package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.ChannelStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 下午2:51
 * Class Name: ChannelStatusHandler
 * Author: Levent8421
 * Description:
 * ChannelStatusHandler
 * Watch the connection status
 *
 * @author Levent8421
 */
@ChannelHandlerMapping(order = 1)
@Slf4j
public class ChannelStatusHandler extends SimpleChannelInboundHandler {
    private final ChannelStatus status;

    public ChannelStatusHandler(ChannelStatus status) {
        this.status = status;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        status.setConnected(true);
        status.setChannelHandlerContext(ctx);
        log.info("TCP API Connection connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("TCP API Connection disconnected");
        status.setConnected(false);
        status.setChannelHandlerContext(null);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
