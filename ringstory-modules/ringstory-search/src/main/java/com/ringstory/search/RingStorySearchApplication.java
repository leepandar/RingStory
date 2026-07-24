package com.ringstory.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 搜索服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RingStorySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingStorySearchApplication.class, args);
    }
}
