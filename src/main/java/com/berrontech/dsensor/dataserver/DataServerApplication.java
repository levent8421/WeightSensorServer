package com.berrontech.dsensor.dataserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * <p style="color: #3171FA">Create By Levent8421 </p><br>
 *     <img src="">
 * Create Time: 2020/6/9 18:58 <br>
 * Class Name: DataServerApplication <br>
 * Author: Levent8421 <br>
 * Description: <br>
 * DataServer Application Entry <br>
 *
 * @author Levent8421
 */
@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = "com.berrontech.dsensor.dataserver.repository.mapper")
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
