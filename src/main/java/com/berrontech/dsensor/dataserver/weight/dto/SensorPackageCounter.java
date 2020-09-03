package com.berrontech.dsensor.dataserver.weight.dto;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/8/26 12:17
 * Class Name: SensorPackageCounter
 * Author: Levent8421
 * Description:
 * 传感器通信数据包计数
 *
 * @author Levent8421
 */
@Data
public class SensorPackageCounter {
    /**
     * 成功次数
     */
    private long totalSuccess;
    /**
     * 失败次数
     */
    private long totalErrors;
    /**
     * 连续失败次数
     */
    private long continueErrors;
    /**
     * 电子标签通信成功次数
     */
    private long eLabelSuccess;
    /**
     * 电子标签通信失败次数
     */
    private long eLabelErrors;
    /**
     * 电子标签通信连续失败次数
     */
    private long eLabelContinueErrors;
}
