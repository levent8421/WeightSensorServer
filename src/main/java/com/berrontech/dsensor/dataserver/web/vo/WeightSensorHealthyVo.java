package com.berrontech.dsensor.dataserver.web.vo;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.weight.dto.SensorPackageCounter;
import lombok.Data;

/**
 * Create By Levent8421
 * Create Time: 2020/8/28 11:30
 * Class Name: WeightSensorHealthyVo
 * Author: Levent8421
 * Description:
 * 传感器将抗状况数据对象
 *
 * @author Levent8421
 */
@Data
public class WeightSensorHealthyVo {
    /**
     * 包计数器
     */
    private SensorPackageCounter packageCounter;
    /**
     * 传感器
     */
    private WeightSensor sensor;
}
