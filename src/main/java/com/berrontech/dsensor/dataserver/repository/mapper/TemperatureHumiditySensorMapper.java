package com.berrontech.dsensor.dataserver.repository.mapper;

import com.berrontech.dsensor.dataserver.common.entity.TemperatureHumiditySensor;
import com.berrontech.dsensor.dataserver.repository.AbstractMapper;
import org.springframework.stereotype.Repository;

/**
 * Create By Levent8421
 * Create Time: 2020/9/17 17:27
 * Class Name: TemperatureHumiditySensorMapper
 * Author: Levent8421
 * Description:
 * 温湿度传感器元数据相关数据库访问组件
 *
 * @author Levent8421
 */
@Repository
public interface TemperatureHumiditySensorMapper extends AbstractMapper<TemperatureHumiditySensor> {
}
