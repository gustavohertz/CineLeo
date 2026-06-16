package com.cineleo.eventos.client;

import com.cineleo.eventos.exception.BusinessException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PagamentoClient {

    private final RestTemplate restTemplate;

    @Value("${services.pagamento.url}")
    private String pagamentoUrl;

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

    public String processarPagamento(String customerId, Double valor, String descricao, CartaoDTO cartao) {
        try {
            PagamentoRequest request = new PagamentoRequest(
                    customerId, "CREDIT_CARD", valor, descricao,
                    new PagamentoRequest.CardDTO(
                            cartao.getNumero(),
                            cartao.getNomeTitular(),
                            cartao.getMesExpiracao(),
                            cartao.getAnoExpiracao(),
                            cartao.getCvv()
                    )
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

    public boolean verificarPagamento(String pagamentoId) {
        try {
            StatusResponse response = restTemplate.getForObject(
                    pagamentoUrl + "/payments/" + pagamentoId + "/status", StatusResponse.class);
            return response != null && response.isApproved();
        } catch (Exception ex) {
            throw new BusinessException("Falha ao verificar status do pagamento");
        }
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
        private final Double value;
        private final String description;
        private final CardDTO card;

        @Data
        static class CardDTO {
            private final String number;
            private final String holderName;
            private final String expiryMonth;
            private final String expiryYear;
            private final String ccv;
        }
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
