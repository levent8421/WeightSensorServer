package com.berrontech.dsensor.dataserver.weight.serial.util;

import com.berrontech.dsensor.dataserver.weight.serial.SerialException;
import com.berrontech.dsensor.dataserver.weight.serial.SerialPort;
import com.berrontech.dsensor.dataserver.weight.serial.SerialPortFinder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

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
    private static final SerialPortFinder FINDER = new SerialPortFinder();

    public static List<String> scan() {
        final String[] devicesPath = FINDER.getAllDevicesPath();
        return Arrays.asList(devicesPath);
    }

    public static SerialPort openSerial(String name, int baudRate) throws SerialException {
        try {
            val device = new File(name);
            if (!device.exists()) {
                throw new FileNotFoundException("Device File [" + device.getAbsolutePath() + "] not found!");
            }
            try {
                return new SerialPort(device, baudRate);
            } catch (Exception e) {
                throw new SerialException("Error On Open Serial Port", e);
            }
        } catch (Exception e) {
            throw new SerialException(e);
        }
    }
}
