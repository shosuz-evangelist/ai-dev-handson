package com.example.ecsite.service;

import com.example.ecsite.entity.Product;
import com.example.ecsite.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 受け入れ基準 4 / 5 / 6 / 7 / 8 を固定する。 */
class ProductSearchServiceTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final ProductService service = new ProductService(repository);

    private void stub() {
        when(repository.search(any(), any(), anyBoolean(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Product())));
    }

    @Test
    @DisplayName("空文字は条件にしない（受け入れ基準 4）")
    void blankBecomesNull() {
        stub();
        service.search("  ", "", false, null, null);

        ArgumentCaptor<String> kw = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cat = ArgumentCaptor.forClass(String.class);
        verify(repository).search(kw.capture(), cat.capture(), eq(false), any(Pageable.class));
        assertThat(kw.getValue()).isNull();
        assertThat(cat.getValue()).isNull();
    }

    @Test
    @DisplayName("在庫切れは既定で含める（受け入れ基準 5・6）")
    void includesOutOfStockByDefault() {
        stub();
        service.search("ペン", null, false, null, null);
        verify(repository).search(eq("ペン"), isNull(), eq(false), any(Pageable.class));

        service.search("ペン", null, true, null, null);
        verify(repository).search(eq("ペン"), isNull(), eq(true), any(Pageable.class));
    }

    @Test
    @DisplayName("既定は商品名の昇順、1 ページ 20 件、0 ページ目（受け入れ基準 7・8）")
    void defaultPaging() {
        stub();
        service.search(null, null, false, null, null);

        ArgumentCaptor<Pageable> p = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).search(isNull(), isNull(), eq(false), p.capture());
        assertThat(p.getValue().getPageNumber()).isZero();
        assertThat(p.getValue().getPageSize()).isEqualTo(20);
        assertThat(p.getValue().getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("page と size を指定できる（受け入れ基準 8）")
    void explicitPaging() {
        stub();
        service.search(null, "tool", false, 2, 5);

        ArgumentCaptor<Pageable> p = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).search(isNull(), eq("tool"), eq(false), p.capture());
        assertThat(p.getValue().getPageNumber()).isEqualTo(2);
        assertThat(p.getValue().getPageSize()).isEqualTo(5);
    }
}
