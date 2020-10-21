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
     * @return success
     */
    boolean onPieceCountChanged(DigitalSensorItem sensor);

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
     * @param sensor
     */
    boolean onNotifyXSensorTempHumi(DigitalSensorItem sensor);

    /**
     * 通知传感器SN变化
     * @param sensor
     */
    boolean onNotifySensorSnChanged(DigitalSensorItem sensor);

    /**
     * 通知ELabel SN变化
     * @param sensor
     */
    boolean onNotifyELabelSnChanged(DigitalSensorItem sensor);

}
