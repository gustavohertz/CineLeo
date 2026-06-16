package com.leocine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class notificationApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(notificationApp.class, args);
    }
}
