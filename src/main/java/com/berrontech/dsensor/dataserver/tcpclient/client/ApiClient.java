package com.berrontech.dsensor.dataserver.tcpclient.client;

import com.berrontech.dsensor.dataserver.tcpclient.exception.MessageException;
import com.berrontech.dsensor.dataserver.tcpclient.exception.TcpConnectionException;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create By Levent8421
 * Create Time: 2020/6/11 16:52
 * Class Name: ApiClient
 * Author: Levent8421
 * Description:
 * Api Client
 *
 * @author Levent8421
 */
public interface ApiClient {
    /**
     * Get Connections Status
     *
     * @return status flag
     */
    boolean isConnected();

    /**
     * Connect
     *
     * @throws TcpConnectionException e
     */
    void connect() throws TcpConnectionException;

    /**
     * Send Message
     *
     * @param message  message
     * @param timeout  timeout in ms
     * @param listener listener
     * @throws MessageException e
     */
    void send(Message message, int timeout, MessageListener listener) throws MessageException;

    /**
     * Disconnect
     */
    void disconnect();

    /**
     * Get Input Stream
     *
     * @return InputStream
     * @throws TcpConnectionException e
     */
    InputStream getInputStream() throws TcpConnectionException;

    /**
     * Get Output Stream
     *
     * @return OutputStream
     * @throws TcpConnectionException e
     */
    OutputStream getOutputStream() throws TcpConnectionException;

    /**
     * Check Message Timeout
     */
    void checkTimeout();
}
