package com.example.ecsite.controller;

import com.example.ecsite.entity.Product;
import com.example.ecsite.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 受け入れ基準 1 / 3 / 8 / 9 を HTTP の側から固定する。 */
@WebMvcTest(ProductController.class)
class ProductSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product(String name, String category, int stock) {
        Product p = new Product();
        p.setId(1L);
        p.setName(name);
        p.setPrice(120);
        p.setImageUrl("images/pen.jpg");
        p.setCategory(category);
        p.setStock(stock);
        return p;
    }

    @Test
    @DisplayName("未ログインで検索でき、総件数を返す（受け入れ基準 1・8・9）")
    void search_returns200() throws Exception {
        when(productService.search(any(), any(), anyBoolean(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(product("ボールペン", "writing", 120)),
                        PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/products/search").param("keyword", "ペン"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("ボールペン"))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @DisplayName("category はそのままサービスへ渡る（受け入れ基準 3）")
    void search_passesCategory() throws Exception {
        when(productService.search(any(), any(), anyBoolean(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/api/products/search")
                        .param("category", "tool").param("inStockOnly", "true"))
                .andExpect(status().isOk());

        verify(productService).search(isNull(), eq("tool"), eq(true), isNull(), isNull());
    }
}
