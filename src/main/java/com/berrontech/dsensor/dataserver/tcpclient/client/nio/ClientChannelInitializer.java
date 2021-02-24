package com.berrontech.dsensor.dataserver.tcpclient.client.nio;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler.ChannelHandlerMapping;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 下午2:26
 * Class Name: ClientChannelInitializer
 * Author: Levent8421
 * Description:
 * ClientChannelInitializer
 * Api Client Socket Chanel Initializer
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class ClientChannelInitializer extends ChannelInitializer implements ApplicationContextAware {
    @Data
    private static class HandlerInfo {
        private int order;
        private HandlerFactory handler;
    }

    private ApplicationContext applicationContext;
    private List<HandlerFactory> handlerInfos;

    @PostConstruct
    public void initHandler() {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ChannelHandlerMapping.class);
        final List<HandlerInfo> handlerInfos = new ArrayList<>(beans.size());
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            log.debug("Registering handler [{}]", entry.getKey());
            final Object handlerObject = entry.getValue();
            if (!(handlerObject instanceof HandlerFactory)) {
                throw new IllegalArgumentException("Can not convert [" + handlerObject.getClass().getName() + "] to ChannelHandler!");
            }
            final HandlerFactory factory = (HandlerFactory) handlerObject;
            final HandlerInfo handlerInfo = new HandlerInfo();
            handlerInfo.setHandler(factory);
            final int order = factory.getOrder();
            handlerInfo.setOrder(order);
            handlerInfos.add(handlerInfo);
        }
        handlerInfos.sort(Comparator.comparingInt(HandlerInfo::getOrder));
        this.handlerInfos = handlerInfos.stream().map(HandlerInfo::getHandler).collect(Collectors.toList());
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();
        for (HandlerFactory factory : handlerInfos) {
            pipeline.addLast(factory.createHandler());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
