package com.berrontech.dsensor.dataserver.weight.firmware;

/**
 * Create By Levent8421
 * Create Time: 2020/9/11 15:53
 * Class Name: UpgradeFirmwareListener
 * Author: Levent8421
 * Description:
 * 固件升级进度监听器
 *
 * @author Levent8421
 */
public interface UpgradeFirmwareListener {
    /**
     * 升级进度更新回调
     *
     * @param totalLen   总长度
     * @param currentPos 当前进度
     */
    void onUpdate(int totalLen, int currentPos);

    /**
     * 升级成功回调
     *
     * @param connectionId 连接ID
     * @param address      地址
     */
    void onSuccess(Integer connectionId, Integer address);

    /**
     * 升级失败时调用
     *
     * @param connectionId 连接ID
     * @param address      物理地址
     * @param error        错误信息
     */
    void onError(Integer connectionId, Integer address, Throwable error);
}
