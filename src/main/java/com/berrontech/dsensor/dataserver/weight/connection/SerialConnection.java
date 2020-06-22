package com.berrontech.dsensor.dataserver.weight.connection;


import com.berrontech.dsensor.dataserver.common.util.ThreadUtils;
import com.berrontech.dsensor.dataserver.weight.serial.SerialPort;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

public class SerialConnection extends BasicConnection {
    private final static String TAG = SerialConnection.class.getSimpleName();

    private String portName;
    private int baudrate = 115200;
    private SerialPort serialPort;
    private OutputStream serialOutput;
    private InputStream serialInput;
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
                    serialPort = new SerialPort(new File(portName), baudrate, 0);
                    serialOutput = serialPort.getOutputStream();
                    serialInput = serialPort.getInputStream();
                    setConnected(true);
                    threadPool = ThreadUtils.createSingleThreadPool(TAG);
                    threadPool.execute(() -> {
                        try {
                            while (isConnected() && serialPort != null) {
                                int cnt = serialInput.available();
                                if (cnt > 0) {
                                    byte[] buf = new byte[cnt];
                                    serialInput.read(buf);
                                    getRecvBuffer().push(buf);
                                    notifyReceived();
                                }
                                Thread.sleep(10);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception ex) {
                serialOutput = null;
                serialInput = null;
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
                serialOutput.flush();
                serialOutput.close();
                serialInput.close();
                serialPort.close();
            }
            serialPort = null;
            serialOutput = null;
            serialInput = null;
            threadPool.shutdown();
        } catch (Exception ex) {
        }
    }

    @Override
    public void writeBuf(byte[] buf, int offset, int count) {
        try {
            if (serialOutput != null) {
                serialOutput.write(buf, offset, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
