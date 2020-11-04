package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/11/4 18:52
 * Class Name: TemperatureCalibrationParam
 * Author: Levent8421
 * Description:
 * 温度标定参数
 *
 * @author Levent8421
 */
@Data
public class TemperatureCalibrationParam {
    /**
     * 当前温度
     */
    private String currentTemperature;
}
