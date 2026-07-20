package com.ringstory.family;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication @EnableDiscoveryClient
public class RingStoryFamilyApplication {
    public static void main(String[] args) { SpringApplication.run(RingStoryFamilyApplication.class, args); }
}
