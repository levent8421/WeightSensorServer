package com.berrontech.dsensor.dataserver.weight.serial.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Create By Levent8421
 * Create Time: 2020/12/17 20:11
 * Class Name: SerialDeviceUtils
 * Author: Levent8421
 * Description:
 * 串口设备工具类
 *
 * @author Levent8421
 */
public class SerialDeviceUtils {
    private static final File DEVICE_ID_PATH_FILE = new File("/dev/serial/by-id");

    private static boolean hasUsbTtyDevice() {
        return DEVICE_ID_PATH_FILE.exists();
    }

    /**
     * 获取USB串口设备ID
     *
     * @param deviceFile device file
     * @return id
     * @throws IOException ioe
     */
    public static String getUsbTtyDeviceId(String deviceFile) throws IOException {
        if (!hasUsbTtyDevice()) {
            return null;
        }
        final File[] ids = DEVICE_ID_PATH_FILE.listFiles();
        if (ids == null || ids.length <= 0) {
            return null;
        }
        final File device = new File(deviceFile);
        if (!device.exists()) {
            return null;
        }
        final String filename = device.getName();
        for (File linkFile : ids) {
            final Path readPath = linkFile.toPath().toRealPath();
            final String sourceName = readPath.toFile().getName();
            if (filename.equalsIgnoreCase(sourceName)) {
                return linkFile.getName();
            }
        }
        return null;
    }

    /**
     * 将USBid转换为USB设备连接的路径
     *
     * @param usbId usb id
     * @return path
     */
    public static String asUsbDeviceIdTarget(String usbId) {
        return new File(DEVICE_ID_PATH_FILE, usbId).getAbsolutePath();
    }
}
