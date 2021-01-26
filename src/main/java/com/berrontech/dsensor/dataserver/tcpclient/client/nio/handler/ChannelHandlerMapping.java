package com.berrontech.dsensor.dataserver.tcpclient.client.nio.handler;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 下午3:24
 * Class Name: ChannelHandlerMapping
 * Author: Levent8421
 * Description:
 * ChannelHandlerMapping
 * Channel Hanler Mapper
 *
 * @author Levent8421
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelHandlerMapping {
}
