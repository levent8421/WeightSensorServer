package com.berrontech.dsensor.dataserver.weight.digitalSensor;

/**
 * @author lastn
 */
public interface DigitalSensorListener {
    void onSensorStateChanged(DigitalSensorItem sensor);
    void onPieceCountChanged(DigitalSensorItem sensor);
    void onSlotStateChanged(DigitalSensorItem sensor);

}
