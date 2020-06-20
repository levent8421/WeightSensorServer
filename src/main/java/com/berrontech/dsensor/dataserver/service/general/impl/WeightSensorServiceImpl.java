package com.berrontech.dsensor.dataserver.service.general.impl;

import com.berrontech.dsensor.dataserver.common.entity.WeightSensor;
import com.berrontech.dsensor.dataserver.repository.mapper.WeightSensorMapper;
import com.berrontech.dsensor.dataserver.service.basic.impl.AbstractServiceImpl;
import com.berrontech.dsensor.dataserver.service.general.WeightSensorService;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create By Levent8421
 * Create Time: 2020/6/17 11:04
 * Class Name: WeightSensorServiceImpl
 * Author: Levent8421           `
 * Description:
 * 重力传感器相关业务行为实现
 *
 * @author Levent8421
 */
@Service
public class WeightSensorServiceImpl extends AbstractServiceImpl<WeightSensor> implements WeightSensorService {
    private final WeightSensorMapper weightSensorMapper;

    public WeightSensorServiceImpl(WeightSensorMapper weightSensorMapper) {
        super(weightSensorMapper);
        this.weightSensorMapper = weightSensorMapper;
    }

    @Override
    public void deleteByConnection(Integer connectionId) {
        val query = new WeightSensor();
        query.setConnectionId(connectionId);
        weightSensorMapper.delete(query);
    }

    @Override
    public List<WeightSensor> findByConnection(Integer connectionId) {
        val query = new WeightSensor();
        query.setConnectionId(connectionId);
        return weightSensorMapper.select(query);
    }
}
