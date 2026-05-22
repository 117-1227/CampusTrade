package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.service.CategoryService;
import com.campustrade.service.CategoryService.CategoryTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryTreeNode>> getAll() {
        return Result.success(categoryService.getAll());
    }
}
