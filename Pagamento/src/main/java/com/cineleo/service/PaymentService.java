package com.cineleo.service;

import com.cineleo.dto.PaymentCardRequestDTO;
import com.cineleo.dto.PaymentResponseDTO;
import com.cineleo.exception.PaymentProcessingException;
import com.cineleo.repository.AsaasConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final AsaasConnection asaasConnection;
    private final NotificationClientService notificationClientService;
    private static final Set<String> APPROVED_STATUSES = Set.of("CONFIRMED", "RECEIVED");
    private static final String HOLDER_EMAIL = "cliente@asaas.com";

    private static final String CARD_NUMBER = "4111111111111111";
    private static final String CARD_HOLDER_NAME = "João da Silva";
    private static final String CARD_EXPIRY_MONTH = "12";
    private static final String CARD_EXPIRY_YEAR = "2027";
    private static final String CARD_CCV = "123";

    private static final String HOLDER_CPF = "92643603010";
    private static final String HOLDER_POSTAL_CODE = "23045040";
    private static final String HOLDER_ADDRESS_NUMBER = "123";
    private static final String HOLDER_PHONE = "24981513930";

    public PaymentService(AsaasConnection asaasConnection,
            NotificationClientService notificationClientService) {
        this.asaasConnection = asaasConnection;
        this.notificationClientService = notificationClientService;
    }

    public PaymentResponseDTO createCardPayment(PaymentCardRequestDTO request) {
        Map<String, Object> body = Map.of(
                "customer", request.customerId(),
                "billingType", request.billingType(),
                "value", request.value(),
                "dueDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "description", request.description() != null ? request.description() : "",
                "creditCard", Map.of(
                        "holderName", CARD_HOLDER_NAME,
                        "number", CARD_NUMBER,
                        "expiryMonth", CARD_EXPIRY_MONTH,
                        "expiryYear", CARD_EXPIRY_YEAR,
                        "ccv", CARD_CCV),
                "authorizeOnly", false,
                "creditCardHolderInfo", Map.of(
                        "name", CARD_HOLDER_NAME,
                        "email", HOLDER_EMAIL,
                        "cpfCnpj", HOLDER_CPF,
                        "postalCode", HOLDER_POSTAL_CODE,
                        "addressNumber", HOLDER_ADDRESS_NUMBER,
                        "phone", HOLDER_PHONE));

        try {
            Map<String, Object> response = asaasConnection.createPayment(body);
            String status = (String) response.get("status");
            String paymentId = (String) response.get("id");
            String mappedStatus = APPROVED_STATUSES.contains(status) ? "aprovado" : "rejeitado";

            // Notificação (não bloqueante)
            try {
                String mensagem = String.format(
                        "Pagamento processado: id=%s, status=%s, valor=%.2f, descrição=%s",
                        paymentId, mappedStatus, request.value(),
                        request.description() != null ? request.description() : "");
                notificationClientService.createAndSendEmail(
                        request.customerId(),
                        HOLDER_EMAIL,
                        mensagem,
                        OffsetDateTime.now());
            } catch (Exception e) {
                log.error("Falha ao notificar: {}", e.getMessage());
            }

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