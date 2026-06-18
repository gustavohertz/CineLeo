package com.cineleo.service;

import com.cineleo.dto.PaymentCardRequestDTO;
import com.cineleo.dto.PaymentResponseDTO;
import com.cineleo.exception.PaymentProcessingException;
import com.cineleo.repository.AsaasConnection;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Service
public class PaymentService {

    private final AsaasConnection asaasConnection;
    private static final Set<String> APPROVED_STATUSES = Set.of("CONFIRMED", "RECEIVED");

    public PaymentService(AsaasConnection asaasConnection) {
        this.asaasConnection = asaasConnection;
    }

    public PaymentResponseDTO createCardPayment(PaymentCardRequestDTO request) {
        // dados de cartão fake (hardcord)
        final String HOLDER_EMAIL = "cliente@asaas.com";
        final String HOLDER_CPF = "92643603010";
        final String HOLDER_POSTAL_CODE = "23045040";
        final String HOLDER_ADDRESS_NUMBER = "123";
        final String HOLDER_PHONE = "24981513930";

        Map<String, Object> body = Map.of(
                "customer", request.customerId(),
                "billingType", request.billingType(),
                "value", request.value(),
                "dueDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "description", request.description() != null ? request.description() : "",
                "creditCard", Map.of(
                        "holderName", request.card().holderName(),
                        "number", request.card().number(),
                        "expiryMonth", request.card().expiryMonth(),
                        "expiryYear", request.card().expiryYear(),
                        "ccv", request.card().ccv()
                ),
                "authorizeOnly", false,
                "creditCardHolderInfo", Map.of(
                        "name", request.card().holderName(),
                        "email", HOLDER_EMAIL,
                        "cpfCnpj", HOLDER_CPF,
                        "postalCode", HOLDER_POSTAL_CODE,
                        "addressNumber", HOLDER_ADDRESS_NUMBER,
                        "phone", HOLDER_PHONE
                )
        );

        try {
            Map<String, Object> response = asaasConnection.createPayment(body);
            String status = (String) response.get("status");
            String paymentId = (String) response.get("id");
            String mappedStatus = APPROVED_STATUSES.contains(status) ? "aprovado" : "rejeitado";
            return new PaymentResponseDTO(mappedStatus, paymentId);
        } catch (Exception e) {
            throw new PaymentProcessingException("Falha ao processar pagamento: " + e.getMessage());
        }
    }

    public boolean checkPaymentStatus(String paymentId) {
        try {
            Map<String, Object> response = asaasConnection.getPayment(paymentId);
            String status = (String) response.get("status");
            return APPROVED_STATUSES.contains(status);
        } catch (Exception e) {
            throw new PaymentProcessingException("Erro ao consultar pagamento: " + e.getMessage());
        }
    }
}
