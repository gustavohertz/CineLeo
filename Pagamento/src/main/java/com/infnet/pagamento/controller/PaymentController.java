package com.infnet.microservicesKafka.Services.paymentService.controller;

import com.infnet.microservicesKafka.Services.paymentService.dto.PaymentCardRequestDTO;
import com.infnet.microservicesKafka.Services.paymentService.dto.PaymentResponseDTO;
import com.infnet.microservicesKafka.Services.paymentService.dto.PaymentStatusResponseDTO;
import com.infnet.microservicesKafka.Services.paymentService.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/card")
    public ResponseEntity<PaymentResponseDTO> payWithCard(@Valid @RequestBody PaymentCardRequestDTO request) {
        PaymentResponseDTO response = paymentService.createCardPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<PaymentStatusResponseDTO> checkStatus(@PathVariable String id) {
        boolean approved = paymentService.checkPaymentStatus(id);
        return ResponseEntity.ok(new PaymentStatusResponseDTO(approved));
    }
}