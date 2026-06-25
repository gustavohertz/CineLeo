package com.infnet.microservicesKafka.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    // Construído a partir do RestTemplateBuilder autoconfigurado para que o
    // Micrometer instrumente as chamadas HTTP e propague o header traceparent (W3C).
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}