package com.berrontech.dsensor.dataserver.tcpclient.client.nio;

import io.netty.channel.ChannelHandler;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 21:10
 * Class Name: HandlerFactory
 * Author: Levent8421
 * Description:
 * Handler Factory
 *
 * @author Levent8421
 */
public interface HandlerFactory {
    /**
     * 获取排序号
     *
     * @return 排序号
     */
    int getOrder();

    /**
     * 创建处理器
     *
     * @return 处理器
     */
    ChannelHandler createHandler();
}
