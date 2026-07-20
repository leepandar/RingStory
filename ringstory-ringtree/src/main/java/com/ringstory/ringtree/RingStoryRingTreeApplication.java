package com.ringstory.ringtree; import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication @EnableDiscoveryClient
public class RingStoryRingTreeApplication {
    public static void main(String[] args) { SpringApplication.run(RingStoryRingTreeApplication.class, args); }
}
