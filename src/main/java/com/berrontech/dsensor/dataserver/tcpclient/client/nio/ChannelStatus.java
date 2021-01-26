package com.berrontech.dsensor.dataserver.tcpclient.client.nio;

import com.berrontech.dsensor.dataserver.tcpclient.client.ConnectionConfiguration;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 下午3:18
 * Class Name: ChannelStatus
 * Author: Levent8421
 * Description:
 * ChannelStatus
 * Api Channel Status data
 *
 * @author Levent8421
 */
@Component
@Scope("singleton")

public class ChannelStatus {
    private final ConnectionConfiguration connectionConfiguration;

    public ChannelStatus(ConnectionConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    @Getter
    @Setter
    private boolean connected;
    @Getter
    @Setter
    private ChannelHandlerContext channelHandlerContext;

    public String getHost() {
        return connectionConfiguration.getIp();
    }

    public int getPort() {
        return connectionConfiguration.getPort();
    }

}
