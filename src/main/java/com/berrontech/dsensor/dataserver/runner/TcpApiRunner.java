package com.berrontech.dsensor.dataserver.runner;

import com.berrontech.dsensor.dataserver.tcpclient.client.nio.ChannelBasedApiClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2021/1/26 下午2:07
 * Class Name: TcpApiRunner
 * Author: Levent8421
 * Description:
 * TcpApiRunner
 * TCP API Runner
 *
 * @author Levent8421
 */
@Component
public class TcpApiRunner implements ApplicationRunner {
    private final ChannelBasedApiClient apiClient;

    public TcpApiRunner(ChannelBasedApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        apiClient.connect();
    }
}
