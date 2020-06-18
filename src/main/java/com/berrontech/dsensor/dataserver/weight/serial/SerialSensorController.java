package com.berrontech.dsensor.dataserver.weight.serial;

import com.berrontech.dsensor.dataserver.common.io.AbstractPackageReadConnection;
import com.berrontech.dsensor.dataserver.weight.*;
import com.berrontech.dsensor.dataserver.weight.serial.util.SerialUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create By Levent8421
 * Create Time: 2020/6/13 10:10
 * Class Name: SerialSensorController
 * Author: Levent8421
 * Description:
 * Serial Port Weight Sensor Controller
 *
 * @author Levent8421
 */
@Slf4j
public class SerialSensorController extends AbstractPackageReadConnection implements SensorController, DisposableBean {
    /**
     * 4 K Byte buffer
     */
    private static final int READ_BUFFER_SIZE = 1024 * 4;
    private static final boolean IS_READ_BLOCKING = false;
    private final SerialPort serialPort;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public SerialSensorController(String portName, int baudRate) throws SerialException {
        serialPort = SerialUtils.openSerial(portName, baudRate);
        try {
            this.inputStream = serialPort.getInputStream();
            this.outputStream = serialPort.getOutputStream();
        } catch (Exception e) {
            throw new SerialException("Error On Get Stream From Serial", e);
        }
    }

    @Override
    protected PackageSplitter getSplitter() {
        return new WeightSensorPackageSplitter();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return outputStream;
    }


    @Override
    protected boolean isInputReadBlocking() {
        return IS_READ_BLOCKING;
    }

    @Override
    protected int getReadBufferSize() {
        return READ_BUFFER_SIZE;
    }

    @Override
    public void onNewPackage(byte[] packet) {
        final DataPacket dataPacket;
        try {
            dataPacket = PacketUtils.asPacket(packet);
        } catch (PacketException e) {
            log.warn("Invalidate Packet", e);
            return;
        }
        log.debug("New Packet: {}", dataPacket);
    }

    @Override
    public void send(DataPacket packet) throws IOException {
        packet.calcDataLength();
        packet.doCRC();

        final byte[] bytes = PacketUtils.asBytes(packet);
        log.debug("Send Packet Size=[{}]", bytes.length);
        outputStream.write(bytes);
    }

    @Override
    public void destroy() {
        if (serialPort != null) {
            serialPort.close();
        }
    }
}
