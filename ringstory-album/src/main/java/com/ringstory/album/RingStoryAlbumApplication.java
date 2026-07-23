package com.ringstory.album;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 相册服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ringstory.album.feign")
public class RingStoryAlbumApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingStoryAlbumApplication.class, args);
    }
}
