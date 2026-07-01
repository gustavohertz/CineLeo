package com.cineleo.usuarios.client;

import com.cineleo.usuarios.dto.CustomerResponseDTO;
import com.cineleo.usuarios.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PagamentoClient {

    private final RestTemplate loadBalancedRestTemplate;

    private static final String GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String PAGAMENTO_PATH = "/api/pagamentos";

    public PagamentoClient(RestTemplate loadBalancedRestTemplate) {
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Retry(name = "pagamento")
    @CircuitBreaker(name = "pagamento", fallbackMethod = "fallbackCriarCustomer")
    public String criarCustomer(String nome, String email, String cpf) {
        try {
            CustomerRequest request = new CustomerRequest(nome, email, cpf);

            String url = "lb://" + GATEWAY_SERVICE_NAME + PAGAMENTO_PATH + "/customers";
            log.debug("Chamando Pagamento via Gateway: {}", url);

            CustomerResponseDTO response = loadBalancedRestTemplate.postForObject(
                    url,
                    request,
                    CustomerResponseDTO.class
            );

            if (response == null || response.getCustomerId() == null) {
                throw new BusinessException("Falha ao criar cliente no gateway de pagamento: resposta inválida");
            }
            return response.getCustomerId();
        } catch (RestClientException e) {
            log.error("Erro ao chamar serviço de pagamento: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar serviço de pagamento", e);
            throw new RuntimeException("Falha inesperada ao chamar serviço de pagamento", e);
        }
    }

    @SuppressWarnings("unused")
    private String fallbackCriarCustomer(String nome, String email, String cpf, Throwable t) {
        log.error("FALLBACK ao criar customer: nome={}, email={}, erro: {}", nome, email, t.getMessage(), t);
        throw new BusinessException("Serviço de pagamento indisponível no momento. Tente novamente.");
    }

    private record CustomerRequest(String name, String email, String cpf) {}
}