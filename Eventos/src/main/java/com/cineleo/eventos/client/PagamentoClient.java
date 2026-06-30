package com.cineleo.eventos.client;

import com.cineleo.eventos.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PagamentoClient {

    private static final Logger log = LoggerFactory.getLogger(PagamentoClient.class);

    private final RestTemplate restTemplate;

    @Value("${services.pagamento.url}")
    private String pagamentoUrl;

    // Sem retry: criar cliente não é idempotente (evita duplicar cadastro no gateway).
    @CircuitBreaker(name = "pagamento", fallbackMethod = "criarCustomerFallback")
    public String criarCustomer(String nome, String email, String cpf) {
        try {
            CustomerRequest request = new CustomerRequest(nome, email, cpf);
            CustomerResponse response = restTemplate.postForObject(
                    pagamentoUrl + "/customers", request, CustomerResponse.class);
            if (response == null || response.getCustomerId() == null) {
                throw new BusinessException("Falha ao registrar cliente no gateway de pagamento");
            }
            return response.getCustomerId();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Serviço de pagamento indisponível: " + ex.getMessage());
        }
    }

    // Sem retry: pagamento NÃO é idempotente — reenviar arriscaria cobrança dupla.
    @CircuitBreaker(name = "pagamento", fallbackMethod = "processarPagamentoFallback")
    public String processarPagamento(String customerId, BigDecimal valor, String descricao, CartaoDTO cartao) {
        try {
            PagamentoRequest request = new PagamentoRequest(
                    customerId, "CREDIT_CARD", valor, descricao
            );
            PagamentoResponse response = restTemplate.postForObject(
                    pagamentoUrl + "/payments/card", request, PagamentoResponse.class);
            if (response == null || response.getId() == null) {
                throw new BusinessException("Falha ao processar pagamento");
            }
            return response.getId();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Serviço de pagamento indisponível: " + ex.getMessage());
        }
    }

    // Com retry: consultar status é idempotente (GET), seguro reexecutar em falha transitória.
    @CircuitBreaker(name = "pagamento", fallbackMethod = "verificarPagamentoFallback")
    @Retry(name = "pagamento")
    public boolean verificarPagamento(String pagamentoId) {
        try {
            StatusResponse response = restTemplate.getForObject(
                    pagamentoUrl + "/payments/" + pagamentoId + "/status", StatusResponse.class);
            return response != null && response.isApproved();
        } catch (Exception ex) {
            throw new BusinessException("Falha ao verificar status do pagamento");
        }
    }

    // --- Fallbacks: acionados quando o circuito abre ou a chamada falha ---

    @SuppressWarnings("unused")
    private String criarCustomerFallback(String nome, String email, String cpf, Throwable t) {
        log.warn("Fallback criarCustomer (serviço de pagamento indisponível): {}", t.getMessage());
        throw new BusinessException("Serviço de pagamento indisponível no momento. Tente novamente.");
    }

    @SuppressWarnings("unused")
    private String processarPagamentoFallback(String customerId, BigDecimal valor, String descricao,
                                              CartaoDTO cartao, Throwable t) {
        log.warn("Fallback processarPagamento (serviço de pagamento indisponível): {}", t.getMessage());
        throw new BusinessException("Serviço de pagamento indisponível no momento. Tente novamente.");
    }

    @SuppressWarnings("unused")
    private boolean verificarPagamentoFallback(String pagamentoId, Throwable t) {
        log.warn("Fallback verificarPagamento (serviço de pagamento indisponível): {}", t.getMessage());
        throw new BusinessException("Não foi possível confirmar o status do pagamento. Tente novamente.");
    }

    @Data
    public static class CartaoDTO {
        private String numero;
        private String nomeTitular;
        private String mesExpiracao;
        private String anoExpiracao;
        private String cvv;
    }

    @Data
    static class CustomerRequest {
        private final String name;
        private final String email;
        private final String cpf;
    }

    @Data
    static class CustomerResponse {
        private String customerId;
    }

    @Data
    static class PagamentoRequest {
        private final String customerId;
        private final String billingType;
        private final BigDecimal value;
        private final String description;
    }

    @Data
    static class PagamentoResponse {
        private String id;
        private String status;
    }

    @Data
    static class StatusResponse {
        private boolean approved;
    }
}