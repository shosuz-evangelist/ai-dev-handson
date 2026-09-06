package com.example.ecsite.controller;

import com.example.ecsite.dto.CustomerRequest;
import com.example.ecsite.dto.CustomerResponse;
import com.example.ecsite.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<CustomerResponse> list() {
        return service.findAll().stream().map(CustomerResponse::new).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable Long id) {
        return service.findById(id).map(c -> ResponseEntity.ok(new CustomerResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomerResponse(service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest req) {
        return service.update(id, req).map(c -> ResponseEntity.ok(new CustomerResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
