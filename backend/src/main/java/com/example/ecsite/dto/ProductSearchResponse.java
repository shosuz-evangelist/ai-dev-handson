package com.example.ecsite.dto;

import com.example.ecsite.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductSearchResponse {
    private final List<Product> items;
    private final long totalCount;
    private final int page;
    private final int size;

    public ProductSearchResponse(Page<Product> found) {
        this.items = found.getContent();
        this.totalCount = found.getTotalElements();
        this.page = found.getNumber();
        this.size = found.getSize();
    }

    public List<Product> getItems() { return items; }
    public long getTotalCount() { return totalCount; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}
