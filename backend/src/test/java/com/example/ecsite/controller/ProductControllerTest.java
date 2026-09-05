package com.example.ecsite.controller;

import com.example.ecsite.entity.Product;
import com.example.ecsite.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 既存の商品 API のテスト。受け入れ基準をテストで固定する形の見本になる。 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product(Long id, String name, int price, String category, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setImageUrl("images/dummy.jpg");
        p.setCategory(category);
        p.setStock(stock);
        return p;
    }

    @Test
    @DisplayName("GET /api/products は 200 と一覧を返す")
    void getAll_returns200() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(product(1L, "ボールペン", 120, "writing", 120)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ボールペン"))
                .andExpect(jsonPath("$[0].category").value("writing"));
    }

    @Test
    @DisplayName("存在しない id は 404 を返す")
    void getById_returns404() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
