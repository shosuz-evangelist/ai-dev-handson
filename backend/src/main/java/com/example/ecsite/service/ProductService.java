package com.example.ecsite.service;

import com.example.ecsite.entity.Product;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * 商品を検索する。keyword / category は空なら条件にしない。
     * 並び順の既定は商品名の昇順、ページは 0 始まりで 1 ページ 20 件。
     */
    public Page<Product> search(String keyword, String category, boolean inStockOnly,
                                Integer page, Integer size) {
        String k = (keyword == null || keyword.isBlank()) ? null : keyword;
        String c = (category == null || category.isBlank()) ? null : category;
        PageRequest pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.ASC, "name"));
        return productRepository.search(k, c, inStockOnly, pageable);
    }
}
