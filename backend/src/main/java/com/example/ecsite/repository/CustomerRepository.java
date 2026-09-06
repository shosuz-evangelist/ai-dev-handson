package com.example.ecsite.repository;

import com.example.ecsite.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByDeletedAtIsNull();
    Optional<Customer> findByIdAndDeletedAtIsNull(Long id);
    boolean existsByEmailAndDeletedAtIsNull(String email);
}
