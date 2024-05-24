package com.samsungcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class QueryExecutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(QueryExecutorApplication.class, args);
    }
}
