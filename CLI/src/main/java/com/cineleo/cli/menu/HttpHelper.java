package com.cineleo.cli.menu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class HttpHelper {

    private static final RestTemplate rest = new RestTemplate();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.findAndRegisterModules();
    }

    public static JsonNode get(String url) {
        try {
            ResponseEntity<String> response = rest.getForEntity(url, String.class);
            return mapper.readTree(response.getBody());
        } catch (HttpClientErrorException e) {
            return erro(e.getResponseBodyAsString());
        } catch (Exception e) {
            return erro(e.getMessage());
        }
    }

    public static JsonNode post(String url, Object body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = mapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = rest.postForEntity(url, entity, String.class);
            return mapper.readTree(response.getBody());
        } catch (HttpClientErrorException e) {
            return erro(e.getResponseBodyAsString());
        } catch (Exception e) {
            return erro(e.getMessage());
        }
    }

    public static JsonNode put(String url, Object body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = mapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = rest.exchange(url, HttpMethod.PUT, entity, String.class);
            return mapper.readTree(response.getBody());
        } catch (HttpClientErrorException e) {
            return erro(e.getResponseBodyAsString());
        } catch (Exception e) {
            return erro(e.getMessage());
        }
    }

    public static JsonNode patch(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = rest.exchange(url, HttpMethod.PATCH, entity, String.class);
            return mapper.readTree(response.getBody());
        } catch (HttpClientErrorException e) {
            return erro(e.getResponseBodyAsString());
        } catch (Exception e) {
            return erro(e.getMessage());
        }
    }

    public static void delete(String url) {
        try {
            rest.delete(url);
        } catch (HttpClientErrorException e) {
            System.out.println("  Erro: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("  Erro: " + e.getMessage());
        }
    }

    private static JsonNode erro(String msg) {
        try {
            return mapper.readTree("{\"erro\":\"" + msg.replace("\"", "'") + "\"}");
        } catch (Exception e) {
            return mapper.createObjectNode();
        }
    }
}
