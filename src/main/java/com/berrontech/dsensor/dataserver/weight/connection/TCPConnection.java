package com.berrontech.dsensor.dataserver.weight.connection;


import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TCPConnection extends BasicConnection {
    private boolean isConnected = false;
    private InputStream in = null;
    private OutputStream out = null;
    private Socket socket;
    private String IP;
    private int Port;
    private Thread recvThread;

    public TCPConnection() {
    }

    public TCPConnection setParam(String ip, int port) {
        this.IP = ip;
        this.Port = port;
        return this;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void open() {
        try {
            socket = new Socket();
            SocketAddress address = new InetSocketAddress(IP, Port);
            socket.connect(address);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            isConnected = true;
            recvThread = new Thread(() -> {
                while (isConnected) {
                    try {
                        byte[] recv = new byte[2014];
                        int read = in.read(recv);
                        if (read > 0) {
                            getRecvBuffer().push(recv, 0, read);
                            notifyReceived();
                        }
                        Thread.sleep(30);
                    } catch (Exception e) {
                        close();
                        e.printStackTrace();
                    }
                }
            });
            recvThread.start();
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeBuf(byte[] buf, int offset, int count) {
        if (out == null) {
            throw new InternalServerErrorException("TCP not READY!");
        }
        try {
            out.flush();
            out.write(buf, offset, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "TCPConnection{" +
                "IP='" + IP + '\'' +
                ", Port=" + Port +
                '}';
    }
}
