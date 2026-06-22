package com.infnet.microservicesKafka.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    private final RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${observabilidade.url:http://localhost:8090}")
    private String observabilidadeUrl;

    public RequestLoggingInterceptor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        String nivel = status >= 400 ? "ERROR" : "INFO";
        String mensagem = String.format("%s %s → %d", method, uri, status);

        if (ex != null) {
            nivel = "ERROR";
            mensagem += " | Exceção: " + ex.getMessage();
        }

        try {
            LogEventDTO logEvent = new LogEventDTO(serviceName, nivel, mensagem);
            restTemplate.postForEntity(
                    observabilidadeUrl + "/observabilidade/logs",
                    logEvent,
                    Void.class
            );
        } catch (Exception e) {
            log.warn("Falha ao enviar log para Observabilidade: {}", e.getMessage());
        }
    }
}
