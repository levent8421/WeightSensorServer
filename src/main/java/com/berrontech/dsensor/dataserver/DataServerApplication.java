package com.berrontech.dsensor.dataserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Create By Levent8421
 * Create Time: 2020/6/9 18:58
 * Class Name: DataServerApplication
 * Author: Levent8421
 * Description:
 * DataServer Application Entry
 *
 * @author Levent8421
 */
@SpringBootApplication
@EnableScheduling
public class DataServerApplication {
    /**
     * Main Method
     *
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DataServerApplication.class, args);
    }

}
