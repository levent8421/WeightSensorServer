package com.berrontech.dsensor.dataserver.runner;

import com.berrontech.dsensor.dataserver.common.entity.ApplicationConfig;
import com.berrontech.dsensor.dataserver.service.general.ApplicationConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Create By Levent8421
 * Create Time: 2020/12/25 18:23
 * Class Name: StationIdInitializeRunner
 * Author: Levent8421
 * Description:
 * Station ID Initialize Runner
 *
 * @author Levent8421
 */
@Slf4j
@Component
public class StationIdInitializeRunner implements ApplicationRunner {
    private final ApplicationConfigService applicationConfigService;

    public StationIdInitializeRunner(ApplicationConfigService applicationConfigService) {
        this.applicationConfigService = applicationConfigService;
    }

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationConfig config = applicationConfigService.getConfig(ApplicationConfig.STATION_ID);
        if (config != null) {
            log.info("Skip initialize stationId! stationId=[{}]", config.getValue());
            return;
        }
        final UUID uuid = UUID.randomUUID();
        final ApplicationConfig stationConf = applicationConfigService.setConfig(ApplicationConfig.STATION_ID, uuid.toString());
        log.info("Generated StationID=[{}]", stationConf);
    }
}
