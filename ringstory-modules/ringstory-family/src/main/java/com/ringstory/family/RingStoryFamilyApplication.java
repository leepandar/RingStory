package com.ringstory.family;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 家庭服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class RingStoryFamilyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingStoryFamilyApplication.class, args);
    }
}
