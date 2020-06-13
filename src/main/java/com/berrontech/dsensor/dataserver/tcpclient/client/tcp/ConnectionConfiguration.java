package com.berrontech.dsensor.dataserver.tcpclient.client.tcp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/10 16:10
 * Class Name: ConnectionConfiguration
 * Author: Levent8421
 * Description:
 * Tcp Api Configuration
 *
 * @author Levent8421
 */
@Data
@Component
@ConfigurationProperties(prefix = "tcp-api")
public class ConnectionConfiguration {
    /**
     * IP Addr
     */
    private String ip;
    /**
     * TCP Port
     */
    private int port;
}
