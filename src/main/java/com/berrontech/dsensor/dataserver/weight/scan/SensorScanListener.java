package com.berrontech.dsensor.dataserver.weight.scan;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;

/**
 * Create By Levent8421
 * Create Time: 2020/11/12 11:04
 * Class Name: SensorScanListener
 * Author: Levent8421
 * Description:
 * 监听扫描进度
 *
 * @author Levent8421
 */
public interface SensorScanListener {
    /**
     * 开始扫描回调
     *
     * @param connection 连接
     * @param start      开始地址
     * @param end        结束地址
     */
    void onScanStart(DeviceConnection connection, int start, int end);

    /**
     * 扫描结束回调
     */
    void onScanEnd();

    /**
     * 扫描进度回调
     *
     * @param address  地址
     * @param sn       扫描到的SN
     * @param eLabelSn 电子标签SN
     */
    void onProgress(int address, String sn, String eLabelSn);

    /**
     * 扫描发生错误回调
     *
     * @param err 错误
     */
    void onScanError(Throwable err);
}
