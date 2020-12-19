package com.berrontech.dsensor.dataserver.weight.serial.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final File TTY_DRIVERS_METADATA_FILE = new File("/proc/tty/drivers");
    private static final File DEVICE_DIR = new File("/dev");

    /**
     * 扫描LINUX串口设备
     *
     * @return 串口设备列表
     * @throws IOException IOE
     */
    public static List<String> scanLinuxPorts() throws IOException {
        final List<String> ports = new ArrayList<>();
        final List<TtyDriver> drivers = findDrivers();
        final List<String> serialDriverNodes = drivers.stream()
                .filter(TtyDriver::isSerialDriver)
                .map(TtyDriver::getDefaultDeviceNode)
                .collect(Collectors.toList());
        final File[] devices = DEVICE_DIR.listFiles();
        if (devices == null) {
            return Collections.emptyList();
        }
        for (File device : devices) {
            final String deviceName = device.getAbsolutePath();
            for (String driverNode : serialDriverNodes) {
                if (deviceName.startsWith(driverNode)) {
                    ports.add(deviceName);
                }
            }
        }
        return ports;
    }

    private static List<TtyDriver> findDrivers() throws IOException {
        final List<TtyDriver> drivers = new ArrayList<>();
        try (final BufferedReader scanner = new BufferedReader(new InputStreamReader(new FileInputStream(TTY_DRIVERS_METADATA_FILE)))) {
            String line;
            while ((line = scanner.readLine()) != null) {
                final TtyDriver driver = TtyDriver.parseFromString(line);
                drivers.add(driver);
            }
        }
        return drivers;
    }
}

/**
 * TTY 驱动描述
 */
@Data
class TtyDriver {
    private static final int ITEMS = 5;
    private static final String SERIAL_DRIVER_TYPE = "serial";

    public static TtyDriver parseFromString(String line) {
        if (StringUtils.isBlank(line)) {
            return null;
        }
        final String[] items = line.split("\\s+");
        if (items.length < ITEMS) {
            return null;
        }
        final TtyDriver driver = new TtyDriver();
        driver.driverName = items[0];
        driver.defaultDeviceNode = items[1];
        driver.majorNumber = items[2];
        driver.minorsRange = items[3];
        driver.driverType = items[4];
        return driver;
    }

    /**
     * 驱动程序名称
     */
    private String driverName;
    /**
     * 默认设备节点
     */
    private String defaultDeviceNode;
    /**
     * 驱动程序号
     */
    private String majorNumber;
    /**
     * the range of minors used by the driver
     */
    private String minorsRange;
    /**
     * 驱动类型
     */
    private String driverType;

    boolean isSerialDriver() {
        return SERIAL_DRIVER_TYPE.equalsIgnoreCase(driverType);
    }
}