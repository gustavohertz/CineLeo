package com.infnet.microservicesKafka.Services.paymentService.service;

import com.infnet.microservicesKafka.Services.paymentService.dto.CustomerRequestDTO;
import com.infnet.microservicesKafka.Services.paymentService.exception.PaymentProcessingException;
import com.infnet.microservicesKafka.Services.paymentService.repository.AsaasConnection;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerService {

    private final AsaasConnection asaasConnection;

    public CustomerService(AsaasConnection asaasConnection) {
        this.asaasConnection = asaasConnection;
    }

    public String createCustomer(CustomerRequestDTO request) {
        Map<String, Object> body = Map.of(
                "name", request.name(),
                "email", request.email(),
                "cpfCnpj", request.cpf()
        );
        try {
            Map<String, Object> response = asaasConnection.createCustomer(body);
            return (String) response.get("id");
        } catch (Exception e) {
            throw new PaymentProcessingException("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }
}