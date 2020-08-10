package com.berrontech.dsensor.dataserver.conf;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Create By Levent8421
 * Create Time: 2020/8/10 13:31
 * Class Name: ApplicationConfiguration
 * Author: Levent8421
 * Description:
 * Application Configuration Properties
 *
 * @author Levent8421
 */
@Component
@ConfigurationProperties("app")
@Data
@Slf4j
public class ApplicationConfiguration {
    /**
     * AppVersion Config
     */
    private String appVersion;

    @PostConstruct
    public void showVersion() {
        log.info("Application Version [{}]", appVersion);
    }
}
