package com.campustrade.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.common.Result;
import com.campustrade.entity.Category;
import com.campustrade.entity.Product;
import com.campustrade.mapper.CategoryMapper;
import com.campustrade.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)));
    }

    @PostMapping
    public Result<Category> create(@RequestBody Category cat) {
        categoryMapper.insert(cat);
        return Result.success(cat, "创建成功");
    }

    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category cat) {
        cat.setId(id);
        categoryMapper.updateById(cat);
        return Result.success(cat, "更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long count = productMapper.selectCount(
            new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (count > 0) throw new BusinessException("该分类下有商品，无法删除");
        categoryMapper.deleteById(id);
        return Result.success(null, "删除成功");
    }
}
