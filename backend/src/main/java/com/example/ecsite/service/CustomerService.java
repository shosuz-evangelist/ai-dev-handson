package com.example.ecsite.service;

import com.example.ecsite.dto.CustomerRequest;
import com.example.ecsite.entity.Customer;
import com.example.ecsite.exception.DuplicateEmailException;
import com.example.ecsite.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> findAll() {
        return repository.findByDeletedAtIsNull();
    }

    public Optional<Customer> findById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id);
    }

    @Transactional
    public Customer create(CustomerRequest req) {
        if (repository.existsByEmailAndDeletedAtIsNull(req.getEmail())) {
            throw new DuplicateEmailException(req.getEmail());
        }
        Customer c = new Customer();
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        return repository.save(c);
    }

    @Transactional
    public Optional<Customer> update(Long id, CustomerRequest req) {
        return repository.findByIdAndDeletedAtIsNull(id).map(c -> {
            if (!c.getEmail().equals(req.getEmail())
                    && repository.existsByEmailAndDeletedAtIsNull(req.getEmail())) {
                throw new DuplicateEmailException(req.getEmail());
            }
            c.setName(req.getName());
            c.setEmail(req.getEmail());
            c.setPhone(req.getPhone());
            c.setUpdatedAt(LocalDateTime.now());
            return repository.save(c);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).map(c -> {
            c.setDeletedAt(LocalDateTime.now());
            repository.save(c);
            return true;
        }).orElse(false);
    }
}
