package com.example.ecsite.service;

import com.example.ecsite.entity.Product;
import com.example.ecsite.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** 既存の商品サービスのテスト。演習の前からここが緑であることを確認する。 */
class ProductServiceTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final ProductService service = new ProductService(repository);

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
    @DisplayName("一覧はリポジトリの結果をそのまま返す")
    void getAllProducts_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(
                product(1L, "ボールペン", 120, "writing", 120),
                product(2L, "ノート", 200, "paper", 80)));

        List<Product> actual = service.getAllProducts();

        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getName()).isEqualTo("ボールペン");
        assertThat(actual.get(1).getCategory()).isEqualTo("paper");
    }

    @Test
    @DisplayName("存在しない id は空で返る")
    void getProductById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.getProductById(99L)).isEmpty();
    }

    @Test
    @DisplayName("登録はリポジトリに保存を委譲する")
    void createProduct_delegatesToRepository() {
        Product input = product(null, "定規", 180, "tool", 60);
        when(repository.save(any(Product.class))).thenReturn(product(10L, "定規", 180, "tool", 60));

        Product saved = service.createProduct(input);

        assertThat(saved.getId()).isEqualTo(10L);
        verify(repository, times(1)).save(input);
    }
}
