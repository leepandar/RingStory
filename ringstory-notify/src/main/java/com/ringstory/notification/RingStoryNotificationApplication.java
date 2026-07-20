package com.ringstory.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 通知服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RingStoryNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingStoryNotificationApplication.class, args);
    }
}
