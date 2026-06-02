package com.infnet.microservicesKafka.Services.paymentService.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
public class AsaasConnection {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public AsaasConnection(RestTemplate restTemplate,
                           @Value("${asaas.api.url}") String apiUrl,
                           @Value("${asaas.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("access_token", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Map<String, Object> createCustomer(Map<String, Object> customerData) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(customerData, headers());
        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiUrl + "/customers", request, Map.class);
        return response.getBody();
    }

    public Map<String, Object> createPayment(Map<String, Object> paymentData) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentData, headers());
        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiUrl + "/payments", request, Map.class);
        return response.getBody();
    }

    public Map<String, Object> getPayment(String paymentId) {
        HttpEntity<Void> request = new HttpEntity<>(headers());
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl + "/payments/" + paymentId, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }
}
