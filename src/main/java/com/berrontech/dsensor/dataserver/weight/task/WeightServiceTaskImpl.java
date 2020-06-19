package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
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
    /**
     * 称重数据临时保存于此
     */
    private final WeightDataHolder weightDataHolder;
    /**
     * TCP API Client
     */
    private final ApiClient apiClient;

    public WeightServiceTaskImpl(WeightDataHolder weightDataHolder, ApiClient apiClient) {
        this.weightDataHolder = weightDataHolder;
        this.apiClient = apiClient;
    }

    @Override
    public void setup() {

    }

    /**
     * 主循环
     *
     * @return return false表示不再进行下一次循环
     */
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
