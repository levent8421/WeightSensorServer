package com.berrontech.dsensor.dataserver.weight.dto;

import java.util.HashMap;

/**
 * Create By Levent8421
 * Create Time: 2020/9/3 17:59
 * Class Name: DeviceDetails
 * Author: Levent8421
 * Description:
 * 传感器详细信息
 *
 * @author Levent8421
 */
public class DeviceDetails extends HashMap<String, Object> {
    /**
     * 物理地址
     */
    public static final String ADDRESS = "address";
    /**
     * 重量
     */
    public static final String WEIGHT = "weight";
    /**
     * 高分辨率的重量
     */
    public static final String HIGH_RESOLUTION = "highResolution";
    /**
     * 是否稳态
     */
    public static final String STABLE = "stable";
    /**
     * 零点偏移
     */
    public static final String ZERO_OFFSET = "zeroOffset";
    /**
     * 单位
     */
    public static final String UNIT = "unit";
    /**
     * raw count
     */
    public static final String RAW_COUNT = "rawCount";
    /**
     * IS Counting
     */
    public static final String COUNTING = "counting";
    /**
     * 单重
     */
    public static final String APW = "apw";
    /**
     * 数量
     */
    public static final String PIECES = "pieces";
    /**
     * 标定RawCT1
     */
    public static final String PT1_RAW_COUNT = "pt1RawCount";
    /**
     * 标定重量1
     */
    public static final String PT1_WEIGHT = "pt1Weight";
    /**
     * 标定RawCT2
     */
    public static final String PT2_RAW_COUNT = "pt2RawCount";
    /**
     * 标定重量2
     */
    public static final String PT2_WEIGHT = "pt2Weight";
    /**
     * 最大容量
     */
    public static final String CAPACITY = "capacity";
    /**
     * 分度值
     */
    public static final String INCREMENT = "increment";
    /**
     * ？？
     */
    public static final String GEO_FACTOR = "geoFactor";
    /**
     * 零点跟踪
     */
    public static final String ZERO_CAPTURE = "zeroCapture";
    /**
     * 蠕变补偿
     */
    public static final String CREEP_CORRECT = "creepCorrect";
    /**
     * ？？
     */
    public static final String STABLE_RANGE = "stableRange";
    /**
     * PCB SN
     */
    public static final String PCB_SN = "pcbSn";
    /**
     * DEVICE_SN
     */
    public static final String DEVICE_SN = "deviceSn";
    /**
     * 设备类型
     */
    public static final String DEVICE_MODEL = "deviceModel";
    /**
     * 固件版本
     */
    public static final String FIRMWARE_VERSION = "firmwareVersion";
    /**
     * 电子标签信息
     */
    public static final String E_LABEL_DETAILS = "eLabelDetails";
    /**
     * 传感器信息
     */
    public static final String SENSOR = "sensor";
    /**
     * 货道信息
     */
    public static final String SLOT = "slot";

    /**
     * 设置信息
     *
     * @param name  name
     * @param value value
     * @return this
     */
    public DeviceDetails set(String name, Object value) {
        put(name, value);
        return this;
    }
}
