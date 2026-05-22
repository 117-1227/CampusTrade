package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.Category;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.CategoryMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.ProductService;
import com.campustrade.vo.ProductDetailVO;
import com.campustrade.vo.ProductVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public ProductVO create(Long sellerId, String title, String description,
                            BigDecimal price, BigDecimal originalPrice,
                            String condition, List<String> images, Long categoryId) {
        Product p = new Product();
        p.setTitle(title);
        p.setDescription(description);
        p.setPrice(price);
        p.setOriginalPrice(originalPrice);
        p.setCondition(condition);
        p.setImages(toJson(images != null ? images : Collections.emptyList()));
        p.setCategoryId(categoryId);
        p.setSellerId(sellerId);
        p.setStatus("active");
        p.setAuditStatus("pending");
        p.setViewCount(0);
        p.setFavCount(0);
        productMapper.insert(p);
        return toVO(p);
    }

    @Override
    public ProductVO update(Long productId, Long userId, String title, String description,
                            BigDecimal price, BigDecimal originalPrice,
                            String condition, List<String> images, Long categoryId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");
        if (!p.getSellerId().equals(userId)) throw new BusinessException("无权操作");
        if (title != null) p.setTitle(title);
        if (description != null) p.setDescription(description);
        if (price != null) p.setPrice(price);
        if (originalPrice != null) p.setOriginalPrice(originalPrice);
        if (condition != null) p.setCondition(condition);
        if (images != null) p.setImages(toJson(images));
        if (categoryId != null) p.setCategoryId(categoryId);
        productMapper.updateById(p);
        return toVO(p);
    }

    @Override
    public void delete(Long productId, Long userId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");
        if (!p.getSellerId().equals(userId)) throw new BusinessException("无权操作");
        p.setStatus("hidden");
        productMapper.updateById(p);
    }

    @Override
    public ProductDetailVO getById(Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");
        p.setViewCount((p.getViewCount() != null ? p.getViewCount() : 0) + 1);
        productMapper.updateById(p);

        ProductDetailVO vo = new ProductDetailVO();
        copy(p, vo);
        User seller = userMapper.selectById(p.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }
        Category cat = categoryMapper.selectById(p.getCategoryId());
        if (cat != null) vo.setCategoryName(cat.getName());
        return vo;
    }

    @Override
    public List<ProductVO> listBySeller(Long sellerId) {
        return productMapper.selectList(
            new LambdaQueryWrapper<Product>()
                .eq(Product::getSellerId, sellerId)
                .ne(Product::getStatus, "hidden")
                .orderByDesc(Product::getCreatedAt))
            .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<ProductVO> listByKeyword(String keyword, Long categoryId, int page, int pageSize) {
        LambdaQueryWrapper<Product> q = new LambdaQueryWrapper<Product>()
            .eq(Product::getStatus, "active")
            .eq(Product::getAuditStatus, "approved");
        if (categoryId != null) {
            List<Long> ids = new java.util.ArrayList<>();
            ids.add(categoryId);
            categoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                    .eq(Category::getParentId, categoryId))
                .forEach(c -> ids.add(c.getId()));
            q.in(Product::getCategoryId, ids);
        }
        if (keyword != null && !keyword.isBlank()) {
            q.and(w -> w.like(Product::getTitle, keyword).or().like(Product::getDescription, keyword));
        }
        q.orderByDesc(Product::getCreatedAt);
        q.last("LIMIT " + pageSize + " OFFSET " + ((page - 1) * pageSize));

        return productMapper.selectList(q).stream().map(this::toVO).collect(Collectors.toList());
    }

    private ProductVO toVO(Product p) {
        ProductVO vo = new ProductVO();
        copy(p, vo);
        User seller = userMapper.selectById(p.getSellerId());
        if (seller != null) vo.setSellerName(seller.getNickname());
        return vo;
    }

    private void copy(Product p, ProductVO vo) {
        vo.setId(p.getId());
        vo.setTitle(p.getTitle());
        vo.setDescription(p.getDescription());
        vo.setPrice(p.getPrice());
        vo.setOriginalPrice(p.getOriginalPrice());
        vo.setCondition(p.getCondition());
        vo.setImages(p.getImages());
        vo.setCategoryId(p.getCategoryId());
        vo.setSellerId(p.getSellerId());
        vo.setStatus(p.getStatus());
        vo.setAuditStatus(p.getAuditStatus());
        vo.setViewCount(p.getViewCount());
        vo.setFavCount(p.getFavCount());
        vo.setCreatedAt(p.getCreatedAt());
    }

    private String toJson(List<String> list) {
        try { return jsonMapper.writeValueAsString(list); }
        catch (JsonProcessingException e) { return "[]"; }
    }
}
