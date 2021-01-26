package com.berrontech.dsensor.dataserver.tcpclient.client.nio;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.tcpclient.client.MessageListener;
import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create By Levent8421
 * Create Time: 2021/1/25 11:18
 * Class Name: ChannelBasedApiClient
 * Author: Levent8421
 * Description:
 * Channel Based(Netty) ApiClient
 *
 * @author Levent8421
 */
public class ChannelBasedApiClient implements ApiClient {
    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void connect() throws TcpConnectionException {

    }

    @Override
    public void send(Message message, int timeout, MessageListener listener) throws MessageException {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public InputStream getInputStream() throws TcpConnectionException {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws TcpConnectionException {
        return null;
    }

    @Override
    public void checkTimeout() {

    }
}
