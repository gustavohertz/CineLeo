package com.cineleo.usuarios.client;

import com.cineleo.usuarios.dto.CustomerResponseDTO;
import com.cineleo.usuarios.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
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

            CustomerResponseDTO response = restTemplate.postForObject(
                    pagamentoUrl + "/customers",
                    request,
                    CustomerResponseDTO.class
            );

            if (response == null || response.getCustomerId() == null) {
                throw new BusinessException("Falha ao criar cliente no gateway de pagamento: resposta inválida");
            }
            return response.getCustomerId();
        } catch (RestClientException e) {
            throw new BusinessException("Serviço de pagamento indisponível: " + e.getMessage());
        }
    }

    private record CustomerRequest(String name, String email, String cpf) {}
}