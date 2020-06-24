package com.berrontech.dsensor.dataserver.weight.digitalSensor;

/**
 * @author lastn
 */
public interface DigitalSensorListener {
    boolean onSensorStateChanged(DigitalSensorItem sensor);
    boolean onPieceCountChanged(DigitalSensorItem sensor);
    boolean onSlotStateChanged(DigitalSensorItem sensor);

    boolean onWeightChanged(DigitalSensorItem sensor);

}
