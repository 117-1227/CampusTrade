package com.campustrade.service;

import com.campustrade.vo.ProductDetailVO;
import com.campustrade.vo.ProductVO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductVO create(Long sellerId, String title, String description,
                     BigDecimal price, BigDecimal originalPrice,
                     String condition, List<String> images, Long categoryId);

    ProductVO update(Long productId, Long userId, String title, String description,
                     BigDecimal price, BigDecimal originalPrice,
                     String condition, List<String> images, Long categoryId);

    void delete(Long productId, Long userId);

    ProductDetailVO getById(Long productId);

    List<ProductVO> listBySeller(Long sellerId);

    List<ProductVO> listByKeyword(String keyword, Long categoryId, int page, int pageSize);
}
