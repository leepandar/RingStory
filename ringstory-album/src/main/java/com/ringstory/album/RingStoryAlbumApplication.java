package com.ringstory.album;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 相册服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RingStoryAlbumApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingStoryAlbumApplication.class, args);
    }
}
