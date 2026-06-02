package com.infnet.microservicesKafka.Services.paymentService.controller;

import com.infnet.microservicesKafka.Services.paymentService.dto.CustomerRequestDTO;
import com.infnet.microservicesKafka.Services.paymentService.dto.CustomerResponseDTO;
import com.infnet.microservicesKafka.Services.paymentService.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO request) {
        String customerId = customerService.createCustomer(request);
        return ResponseEntity.ok(new CustomerResponseDTO(customerId));
    }
}