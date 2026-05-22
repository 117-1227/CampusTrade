package com.campustrade.service;

import com.campustrade.entity.Category;
import com.campustrade.mapper.CategoryMapper;
import com.campustrade.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryMapper);
    }

    @Test
    void shouldReturnAllActiveCategories() {
        var c1 = buildCategory(1L, "教材教辅", null, 1);
        var c2 = buildCategory(2L, "数码产品", null, 2);
        var c3 = buildCategory(3L, "其他", null, 6);
        when(categoryMapper.selectList(null)).thenReturn(Arrays.asList(c1, c2, c3));

        var result = categoryService.getAll();

        assertEquals(3, result.size());
    }

    @Test
    void shouldBuildTreeStructure() {
        var root = buildCategory(1L, "教材教辅", null, 1);
        var child = buildCategory(7L, "专业课教材", 1L, 1);
        var other = buildCategory(2L, "数码产品", null, 2);
        when(categoryMapper.selectList(null)).thenReturn(Arrays.asList(root, child, other));

        List<CategoryService.CategoryTreeNode> tree = categoryService.getAll();

        assertEquals(2, tree.size());
        var rootNode = tree.get(0);
        assertEquals("教材教辅", rootNode.getName());
        assertEquals(1, rootNode.getChildren().size());
        assertEquals("专业课教材", rootNode.getChildren().get(0).getName());
    }

    @Test
    void shouldSortBySortOrder() {
        var c1 = buildCategory(1L, "B", null, 10);
        var c2 = buildCategory(2L, "A", null, 5);
        when(categoryMapper.selectList(null)).thenReturn(Arrays.asList(c1, c2));

        var tree = categoryService.getAll();

        assertEquals("A", tree.get(0).getName());
        assertEquals("B", tree.get(1).getName());
    }

    private Category buildCategory(Long id, String name, Long parentId, int sortOrder) {
        var c = new Category();
        c.setId(id);
        c.setName(name);
        c.setParentId(parentId);
        c.setSortOrder(sortOrder);
        c.setStatus("active");
        return c;
    }
}
