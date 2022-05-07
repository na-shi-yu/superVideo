package com.hurys.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/4/27 15:17
 * @Description: 启动类
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SuperVideo {

    public static void main(String[] args) {
        SpringApplication.run(SuperVideo.class, args);
    }
}