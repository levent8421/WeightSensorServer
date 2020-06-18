package com.berrontech.dsensor.dataserver.weight.task;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 19:50
 * Class Name: WeightServiceTask
 * Author: Levent8421
 * Description:
 * 称重服务任务
 *
 * @author Levent8421
 */
public interface WeightServiceTask {
    /**
     * Task Setup
     */
    void setup();

    /**
     * Task Main Loop
     *
     * @return Need Next Loop
     */
    boolean loop();

    /**
     * Stop Task
     */
    void beforeStop();

    /**
     * Callback after stop
     */
    void afterStop();
}
