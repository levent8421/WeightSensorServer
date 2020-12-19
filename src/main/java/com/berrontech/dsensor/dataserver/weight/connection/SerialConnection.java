package com.berrontech.dsensor.dataserver.weight.connection;


import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.weight.serial.SerialPort;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;

/**
 * Create By Lastnika
 * Create Time: 2020/6/24 14:51
 * Class Name: SerialConnection
 * Author: Lastnika
 * Description:
 * 串口连接
 *
 * @author Lastnika
 */
public class SerialConnection extends BasicConnection {
    private final static String TAG = SerialConnection.class.getSimpleName();

    private String portName;
    private int baudrate = 115200;
    private SerialPort serialPort;
    private ExecutorService threadPool;

    public SerialConnection() {
    }

    public SerialConnection setParam(String portName, int baudrate) {
        this.portName = portName;
        this.baudrate = baudrate;
        return this;
    }

    @Override
    public void open() throws Exception {
        if (serialPort == null) {
            try {
                if (!StringUtils.isBlank(portName)) {
                    serialPort = new SerialPort(portName, baudrate);
                    serialPort.open();
                    setConnected(true);
                    threadPool = ThreadUtils.createSingleThreadPool(TAG);
                    threadPool.execute(() -> {
                        try {
                            byte[] buf = new byte[1024];
                            while (isConnected() && serialPort != null) {
                                try {
                                    int cnt = serialPort.read(buf, 0, buf.length);
                                    if (cnt > 0) {
                                        getRecvBuffer().push(buf, 0, cnt);
                                        notifyReceived();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Thread.sleep(5);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            } catch (Exception ex) {
                serialPort = null;
                setConnected(false);
                throw ex;
            }
        }
    }

    @Override
    public void close() {
        setConnected(false);
        try {
            if (serialPort != null) {
                serialPort.close();
                serialPort = null;
            }
            threadPool.shutdown();
        } catch (Exception ex) {
            // Do Nothing
        }
    }

    @Override
    public void writeBuf(byte[] buf, int offset, int count) {
        try {
            if (serialPort != null) {
                serialPort.write(buf, offset, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "SerialConnection{" +
                "portName='" + portName + '\'' +
                ", baudrate=" + baudrate +
                '}';
    }
}
