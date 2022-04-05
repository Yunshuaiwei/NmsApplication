package com.xust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ysw
 * EnableScheduling基于注解的定时任务
 */
@EnableScheduling
@SpringBootApplication
public class NmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NmsApplication.class, args);
    }
}
