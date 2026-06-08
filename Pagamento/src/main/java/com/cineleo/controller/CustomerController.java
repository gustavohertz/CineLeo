package com.cineleo.controller;

import com.cineleo.dto.CustomerRequestDTO;
import com.cineleo.dto.CustomerResponseDTO;
import com.cineleo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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