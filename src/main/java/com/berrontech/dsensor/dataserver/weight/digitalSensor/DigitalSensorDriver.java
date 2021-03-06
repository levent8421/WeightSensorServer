package com.berrontech.dsensor.dataserver.weight.digitalSensor;


import com.berrontech.dsensor.dataserver.weight.connection.BasicConnection;
import com.berrontech.dsensor.dataserver.weight.connection.SerialConnection;
import com.berrontech.dsensor.dataserver.weight.connection.TCPConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeoutException;

@Slf4j
@Data
public class DigitalSensorDriver {
    private Object lock = new Object();

    public int DefaultBaudrate = 115200;
    private BasicConnection connection;

    public int PackVersion = DataPacket.Version21;

    public void OpenCom(String portName) {
        OpenCom(portName, DefaultBaudrate);
    }

    public void OpenCom(String portName, int baudrate) {
        try {
            log.info("Open serial[{}:{}]", portName, baudrate);
            setConnection(new SerialConnection().setParam(portName, baudrate));
            connection.open();
        } catch (Exception ex) {
            log.error("Open serial[{}] failed", portName, ex);
        }
    }

    public void OpenNet(String address, int port) {
        try {
            log.info("Open net[{}:{}] async", address, port);
            TCPConnection conn = new TCPConnection().setParam(address, port);
            setConnection(conn);
            conn.openWithWatchDog();    // open async
        } catch (Exception ex) {
            log.error("Open net[{}:{}] failed", address, port, ex);
        }
    }

    public void OpenNetForScan(String address, int port) {
        try {
            log.info("Open net[{}:{}] at once", address, port);
            TCPConnection conn = new TCPConnection().setParam(address, port);
            setConnection(conn);
            conn.open();    // open at once
        } catch (Exception ex) {
            log.error("Open net[{}:{}] failed", address, port, ex);
        }
    }

    public void close() {
        if (getConnection() != null) {
            getConnection().close();
        }
    }

    public DataPacket Read(int timeout) throws TimeoutException {
        // 0x02 0xFD len:1 ver:1 add:1 cmd:1 content:n checksum:1
        // |heads   |len  |data
        long endTime = System.currentTimeMillis() + timeout;
        do {
            if (getConnection().waitByte(DataPacket.Head1, timeout)) {
                if (getConnection().readByte(timeout) != DataPacket.Head2) {
                    continue;
                }
                int len = getConnection().readByte(timeout) & 0xFF;
                byte[] data = getConnection().readBytes(len, timeout);
                DataPacket packet = null;
                if (data != null) {
                    packet = DataPacket.ParseData(data);
                    if (!packet.isChecksumOK()) {
                        log.debug("Checksum error, ver=[{}] recv=[{}], calc=[{}]", packet.getVersion(), packet.getChecksum(), packet.CalcChecksum());
                        return null;
                    }
                }
                return packet;
            }
        } while (System.currentTimeMillis() <= endTime);
        throw new TimeoutException("Wait packet timeout");
    }

    public DataPacket Read(int address, int cmd, int timeout) throws TimeoutException {
        long endTime = System.currentTimeMillis() + timeout;
        do {
            DataPacket packet = Read(timeout);
            if (packet != null && packet.getAddress() == address && packet.getCmd() == cmd) {
                return packet;
            } else {
                log.debug("packet error: required addr={} type={}", address, cmd);
            }
        } while (System.currentTimeMillis() <= endTime);
        throw new TimeoutException("Wait packet with address(" + address + ") cmd({" + cmd + "}) timeout");
    }

    public void Write(DataPacket packet) throws Exception {
        //log.debug("---> #{} {} dataLen={}", packet.getAddress(), (char) packet.getCmd(), packet.getContentLength());
        // send dummy byte
        getConnection().write(new byte[]{((byte) 0x00)});
        // send data
        getConnection().write(packet.ToBytes());
    }

    public DataPacket WriteRead(DataPacket packet, int timeout) throws Exception {
        return WriteRead(packet, timeout, 0);
    }

    public DataPacket WriteRead(DataPacket packet, int timeout, int retries) throws Exception {
        int sendCount = 0;
        do {
            Write(packet);
            try {
                DataPacket ans = Read(packet.getAddress(), packet.ToRecvCmd(), timeout);
                if (ans != null) {
                    return ans;
                }
            } catch (TimeoutException e) {
                // minus retry
                sendCount++;
                log.debug("Retry WriteRead({})", sendCount);
            }
        } while (sendCount <= retries);
        throw new TimeoutException("Wait packet out of " + retries + " retries");
    }

    public DataPacket SpawnNewPacket() {
        switch (PackVersion) {
            default:
            case DataPacket.Version21: {
                return new DataPacket21();
            }
            case DataPacket.Version22: {
                return new DataPacket22();
            }
        }
    }


    @Override
    public String toString() {
        return "DigitalSensorDriver{" +
                "connection=" + connection +
                '}';
    }
}
