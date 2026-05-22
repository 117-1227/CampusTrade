package com.campustrade.service;

import lombok.Data;

import java.util.List;

public interface CategoryService {

    List<CategoryTreeNode> getAll();

    @Data
    class CategoryTreeNode {
        private Long id;
        private String name;
        private String icon;
        private List<CategoryTreeNode> children;
    }
}
