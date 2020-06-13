package com.berrontech.dsensor.dataserver.weight.serial.util;

import com.berrontech.dsensor.dataserver.weight.serial.SerialException;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 18:19
 * Class Name: SerialUtils
 * Author: Levent8421
 * Description:
 * Serial Utils
 *
 * @author Levent8421
 */
@Slf4j
public class SerialUtils {
    /**
     * Timeout in ms for serial open
     */
    private static final int OPEN_TIMEOUT = 1000;

    public static List<String> scan() {
        val ports = CommPortIdentifier.getPortIdentifiers();
        val portList = new ArrayList<CommPortIdentifier>();
        while (ports.hasMoreElements()) {
            portList.add((CommPortIdentifier) ports.nextElement());
        }
        return portList.stream().map(CommPortIdentifier::getName).collect(Collectors.toList());
    }

    public static CommPortIdentifier getSerialPortIdentifier(String name) {
        try {
            val id = CommPortIdentifier.getPortIdentifier(name);
            if (id.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                return null;
            }
            return id;
        } catch (NoSuchPortException e) {
            log.warn("Port {} not exists", name);
            return null;
        }
    }

    public static SerialPort openSerial(String name) throws SerialException {
        val identifier = getSerialPortIdentifier(name);
        if (identifier == null) {
            return null;
        }
        try {
            final CommPort port = identifier.open(name, OPEN_TIMEOUT);
            if (port instanceof SerialPort) {
                return (SerialPort) port;
            } else {
                port.close();
                return null;
            }
        } catch (PortInUseException e) {
            val msg = String.format("Port [%s] in use!", name);
            throw new SerialException(msg, e);
        }
    }
}
