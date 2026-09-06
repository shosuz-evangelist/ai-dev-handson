package com.example.ecsite.repository;

import com.example.ecsite.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * キーワード（商品名の部分一致・大文字小文字を区別しない）とカテゴリで絞り込む。
     * どちらも null なら条件に含めない。inStockOnly が true のときだけ在庫のあるものに絞る。
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category IS NULL OR p.category = :category)
              AND (:inStockOnly = false OR p.stock > 0)
            """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("category") String category,
                         @Param("inStockOnly") boolean inStockOnly,
                         Pageable pageable);
}
