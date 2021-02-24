package com.berrontech.dsensor.dataserver.weight.digitalSensor;


/**
 * Create By Lastnika
 * Create Time: 2020/7/2 14:48
 * Class Name: DigitalSensorListener
 * Author: Lastnika
 * Description:
 * 传感器监听器
 *
 * @author Lastnika
 */
public interface DigitalSensorListener {
    /**
     * 通知状态变化
     *
     * @param sensor sensor
     * @return success
     */
    boolean onSensorStateChanged(DigitalSensorItem sensor);

    /**
     * 通知数量改变
     *
     * @param sensor sensor
     * @param force  是否强制
     * @return success
     */
    boolean onPieceCountChanged(DigitalSensorItem sensor, boolean force);

    /**
     * 通知货道状态改变
     *
     * @param sensor sensor
     * @return success
     */
    boolean onSlotStateChanged(DigitalSensorItem sensor);

    /**
     * 通知重量改变
     *
     * @param sensor sensor
     * @return success
     */
    boolean onWeightChanged(DigitalSensorItem sensor);

    /**
     * 通知保存零点修正值
     *
     * @param sensor sensor
     */
    void onNotifySaveZeroOffset(DigitalSensorItem sensor);


    /**
     * 通知温度,湿度变化
     *
     * @param sensor 传感器
     * @return 是否通知成功
     */
    boolean onNotifyXSensorTempHumi(DigitalSensorItem sensor);

    /**
     * 通知传感器SN变化
     *
     * @param sn     序列号
     * @param sensor 传感器
     * @return 是否通知成功
     */
    boolean onNotifySensorSnChanged(DigitalSensorItem sensor, String sn);

    /**
     * 通知ELabel SN变化
     *
     * @param sn     序列号
     * @param sensor 传感器
     * @return 是否通知成功
     */
    boolean onNotifyELabelSnChanged(DigitalSensorItem sensor, String sn);

}
