package com.example.springbatch.minela;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchByMinelaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchByMinelaApplication.class, args);
    }
}
