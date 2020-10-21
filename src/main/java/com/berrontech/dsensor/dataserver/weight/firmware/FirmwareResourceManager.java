package com.berrontech.dsensor.dataserver.weight.firmware;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/10/21 17:10
 * Class Name: FirmwareResourceManager
 * Author: Levent8421
 * Description:
 * 固件资源管理器
 *
 * @author Levent8421
 */
@Component
public class FirmwareResourceManager {
    public static final String SENSOR_FIRMWARE_RESOURCE_NAME = "firmware/sensor_firmware.zip";
    private static final String SENSOR_FIRMWARE_TMP_FILE_NAME = "sensor_firmware.zip";
    public static final String E_LABEL_FIRMWARE_RESOURCE_NAME = "firmware/elabel_firmware.zip";
    private static final String E_LABEL_FIRMWARE_TMP_FILE_NAME = "elabel_firmware.zip";
    private static final String ZIP_PASSWORD = "monolithiot";
    private final Map<String, FirmwareLoader> firmwareLoaderTable;

    public FirmwareResourceManager() {
        firmwareLoaderTable = new HashMap<>(16);
        firmwareLoaderTable.put(SENSOR_FIRMWARE_RESOURCE_NAME,
                new FirmwareLoader(SENSOR_FIRMWARE_RESOURCE_NAME, ZIP_PASSWORD, SENSOR_FIRMWARE_TMP_FILE_NAME));
        firmwareLoaderTable.put(E_LABEL_FIRMWARE_RESOURCE_NAME,
                new FirmwareLoader(E_LABEL_FIRMWARE_RESOURCE_NAME, ZIP_PASSWORD, E_LABEL_FIRMWARE_TMP_FILE_NAME));
    }

    public FirmwareResource getFirmwareResource(String resourceName) {
        if (!firmwareLoaderTable.containsKey(resourceName)) {
            throw new InternalServerErrorException("Can not find firmware resource for name: [" + resourceName + "]");
        }
        return firmwareLoaderTable.get(resourceName).loadResource().getFirmwareResource();
    }
}
