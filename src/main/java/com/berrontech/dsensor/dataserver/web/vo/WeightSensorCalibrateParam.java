package com.berrontech.dsensor.dataserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Create By Levent8421
 * Create Time: 2021/2/8 16:32
 * Class Name: WeightSensorCalibrateParam
 * Author: Levent8421
 * Description:
 * 传感器校准参数
 *
 * @author Levent8421
 */
@Data
public class WeightSensorCalibrateParam {
    /**
     * 砝码重量
     */
    private BigDecimal span;
}
