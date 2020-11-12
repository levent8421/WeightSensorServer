package com.berrontech.dsensor.dataserver.weight.digitalSensor;

public interface DigitalSensorScanListener {
    void onScanStart(DigitalSensorGroup group, int startAddr, int endAddr);
    void onScanEnd(DigitalSensorGroup group);
    void onScanFailed(DigitalSensorGroup group, String msg);
    void onStartTest(DigitalSensorItem sensor);
    void onFound(DigitalSensorItem sensor, DigitalSensorParams newParam);
    void onNotFound(DigitalSensorItem sensor);
}
