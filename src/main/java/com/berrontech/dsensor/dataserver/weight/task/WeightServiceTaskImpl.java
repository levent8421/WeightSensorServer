package com.berrontech.dsensor.dataserver.weight.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 20:47
 * Class Name: WeightServiceTaskImpl
 * Author: Levent8421
 * Description:
 * 称重服务
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class WeightServiceTaskImpl implements WeightServiceTask {
    @Override
    public void setup() {

    }

    @Override
    public boolean loop() {
        return false;
    }

    @Override
    public void beforeStop() {

    }

    @Override
    public void afterStop() {

    }
}
