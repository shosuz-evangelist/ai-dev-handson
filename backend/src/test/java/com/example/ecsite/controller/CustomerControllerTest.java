package com.example.ecsite.controller;

import com.example.ecsite.dto.CustomerRequest;
import com.example.ecsite.entity.Customer;
import com.example.ecsite.exception.ApiExceptionHandler;
import com.example.ecsite.exception.DuplicateEmailException;
import com.example.ecsite.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 受け入れ基準 1 / 2 / 3 / 5 / 7 を HTTP の側から固定する。 */
@WebMvcTest(CustomerController.class)
@Import(ApiExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService service;

    private CustomerRequest req(String name, String email) {
        CustomerRequest r = new CustomerRequest();
        r.setName(name);
        r.setEmail(email);
        return r;
    }

    @Test
    @DisplayName("登録は 201（受け入れ基準 1）")
    void create_returns201() throws Exception {
        Customer c = new Customer();
        c.setId(1L);
        c.setName("山田");
        c.setEmail("a@example.com");
        when(service.create(any())).thenReturn(c);

        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req("山田", "a@example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("a@example.com"));
    }

    @Test
    @DisplayName("email 重複は 409（受け入れ基準 2）")
    void create_returns409() throws Exception {
        when(service.create(any())).thenThrow(new DuplicateEmailException("a@example.com"));

        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req("山田", "a@example.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("存在しない id は 404（受け入れ基準 3）")
    void get_returns404() throws Exception {
        when(service.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("削除は 204（受け入れ基準 5）")
    void delete_returns204() throws Exception {
        when(service.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/customers/1")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("name が空なら 400（受け入れ基準 7）")
    void create_returns400() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req("", "a@example.com"))))
                .andExpect(status().isBadRequest());
        verify(service, never()).create(any());
    }
}
