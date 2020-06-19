package com.berrontech.dsensor.dataserver.weight.task;

import com.berrontech.dsensor.dataserver.tcpclient.client.ApiClient;
import com.berrontech.dsensor.dataserver.weight.holder.WeightDataHolder;
import com.berrontech.dsensor.dataserver.weight.serial.SerialConfiguration;
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
    /**
     * 串口相关配置
     */
    private final SerialConfiguration serialConfiguration;

    public WeightServiceTaskImpl(WeightDataHolder weightDataHolder,
                                 ApiClient apiClient,
                                 SerialConfiguration serialConfiguration) {
        this.weightDataHolder = weightDataHolder;
        this.apiClient = apiClient;
        this.serialConfiguration = serialConfiguration;
    }

    @Override
    public void setup() {
        final int baudRate = serialConfiguration.getBaudRate();
        //TODO 初始换传感器控制组件
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
