package com.ninj.philipotimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhilipoTimerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhilipoTimerApplication.class, args);
    }
}