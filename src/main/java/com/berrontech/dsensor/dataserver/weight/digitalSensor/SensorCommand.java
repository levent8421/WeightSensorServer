package com.berrontech.dsensor.dataserver.weight.digitalSensor;

public class SensorCommand {
    public enum ECommand
    {
        None,
        ReadParams,
        DoZero,
        DoTare,
        ClearTare,
        CalibrateZero,
        CalibrateSpan,
        ReferenceN,
        ClearCounting,
        SetIncrement,
        SetZeroCapture,
        SetCreepCorrect,
        SetStableRange,
        SetPCBASn,
        SetDeviceSn,
        SetDeviceModel,

        ShowAdmin,
        SelectContainer,
        SelectPassenger,
    }

}
