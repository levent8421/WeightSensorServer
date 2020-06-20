package com.berrontech.dsensor.dataserver.weight.digitalSensor;

import java.util.List;

import lombok.Data;

@Data
public class DigitalSensorSubGroup {
    public String Name;
    public List<DigitalSensorItem> Sensors;
}
