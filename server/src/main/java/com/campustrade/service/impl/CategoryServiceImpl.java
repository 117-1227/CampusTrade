package com.campustrade.service.impl;

import com.campustrade.entity.Category;
import com.campustrade.mapper.CategoryMapper;
import com.campustrade.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryTreeNode> getAll() {
        List<Category> categories = categoryMapper.selectList(null);
        Map<Long, List<Category>> grouped = categories.stream()
            .collect(Collectors.groupingBy(c ->
                c.getParentId() != null ? c.getParentId() : 0L));

        return categories.stream()
            .filter(c -> c.getParentId() == null)
            .sorted(Comparator.comparing(Category::getSortOrder))
            .map(root -> buildNode(root, grouped))
            .collect(Collectors.toList());
    }

    private CategoryTreeNode buildNode(Category category, Map<Long, List<Category>> grouped) {
        CategoryTreeNode node = new CategoryTreeNode();
        node.setId(category.getId());
        node.setName(category.getName());
        node.setIcon(category.getIcon());

        List<Category> children = grouped.getOrDefault(category.getId(), List.of());
        node.setChildren(children.stream()
            .sorted(Comparator.comparing(Category::getSortOrder))
            .map(child -> buildNode(child, grouped))
            .collect(Collectors.toList()));

        return node;
    }
}
