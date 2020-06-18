package com.berrontech.dsensor.dataserver.weight;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Create By Levent8421
 * Create Time: 2020/6/18 17:10
 * Class Name: WeightApplicationRunner
 * Author: Levent8421
 * Description:
 * 启动Weight线程
 *
 * @author Levent8421
 */
@Slf4j
@Component
public class WeightApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Weight Runner");
    }
}
