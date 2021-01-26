package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.HandlerFactory;
import io.netty.channel.ChannelHandler;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 21:22
 * Class Name: MessagePackageEncoderFactory
 * Author: Levent8421
 * Description:
 * Handler : Message Package Encoder Factory
 *
 * @author Levent8421
 */
@ChannelHandlerMapping
public class MessagePackageEncoderFactory implements HandlerFactory {
    private final MessagePackageEncoder packageEncoder = new MessagePackageEncoder();

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public ChannelHandler createHandler() {
        return packageEncoder;
    }
}
