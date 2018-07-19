package com.example.datausersservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DataUsersServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataUsersServiceApplication.class, args);
    }
}
