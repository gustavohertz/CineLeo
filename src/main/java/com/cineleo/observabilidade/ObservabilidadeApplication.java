package com.cineleo.observabilidade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ObservabilidadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilidadeApplication.class, args);
    }
}
