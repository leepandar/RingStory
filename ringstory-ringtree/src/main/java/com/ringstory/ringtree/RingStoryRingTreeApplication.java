package com.ringstory.ringtree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 年轮树服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RingStoryRingTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingStoryRingTreeApplication.class, args);
    }
}
