package com.berrontech.dsensor.dataserver.weight.connection;


import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TCPConnection extends BasicConnection {
    private InputStream in = null;
    private OutputStream out = null;
    private Socket socket;
    private String IP;
    private int Port;
    private Thread recvThread;


    private boolean watchDogRunning = false;
    private Thread watchDogThread;
    private int watchDogInterval = 300;
    private int reopenCounter = 0;
    private int reopenInvertal = 3000;
    private int connectTimeout = 5000;

    public TCPConnection() {
    }

    public TCPConnection setParam(String ip, int port) {
        this.IP = ip;
        this.Port = port;
        return this;
    }

    @Override
    public void open() {
        try {
            socket = new Socket();
            SocketAddress address = new InetSocketAddress(IP, Port);
            socket.connect(address, connectTimeout);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            setConnected(true);
            recvThread = new Thread(() -> {
                while (isConnected()) {
                    try {
                        byte[] recv = new byte[2014];
                        int read = in.read(recv);
                        if (read > 0) {
                            getRecvBuffer().push(recv, 0, read);
                            notifyReceived();
                        } else if (read < 0) {
                            throw new IOException("Connection is closed");
                        }
                        Thread.sleep(30);
                    } catch (Exception e) {
                        cleanup();
                        e.printStackTrace();
                    }
                }
            });
            recvThread.start();
        } catch (IOException e) {
            cleanup();
            e.printStackTrace();
        }
    }

    public void openWithWatchDog() {
        if (!watchDogRunning) {
            watchDogRunning = true;
            watchDogThread = new Thread(() -> {
                long ms = System.currentTimeMillis();
                while (watchDogRunning) {
                    if (!isConnected()) {
                        if (ms == 0) {
                            ms = System.currentTimeMillis() + reopenInvertal;
                        } else if (ms <= System.currentTimeMillis()) {
                            ms = 0;
                            reopenCounter++;
                            open();
                        }
                    } else {
                        // connected do nothing
                    }
                    try {
                        Thread.sleep(watchDogInterval);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            watchDogThread.start();
        }
    }

    private void cleanup() {
        try {
            setConnected(false);
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        watchDogRunning = false;
        if (watchDogThread != null) {
            try {
                watchDogThread.join(watchDogInterval + 3000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            watchDogThread = null;
        }
        cleanup();
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

    public int getReopenCounter() {
        return reopenCounter;
    }
}
