package com.example.ecsite.dto;

import com.example.ecsite.entity.Customer;

public class CustomerResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;

    public CustomerResponse(Customer c) {
        this.id = c.getId();
        this.name = c.getName();
        this.email = c.getEmail();
        this.phone = c.getPhone();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
