package com.ringstory.story; import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication @EnableDiscoveryClient
public class RingStoryStoryApplication {
    public static void main(String[] args) { SpringApplication.run(RingStoryStoryApplication.class, args); }
}
