package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    public List<ProductAuditVO> listPending(int page, int pageSize) {
        var q = new LambdaQueryWrapper<Product>()
            .eq(Product::getAuditStatus, "pending")
            .orderByAsc(Product::getCreatedAt)
            .last("LIMIT " + pageSize + " OFFSET " + ((page - 1) * pageSize));
        List<Product> products = productMapper.selectList(q);

        var sellerIds = products.stream().map(Product::getSellerId).collect(Collectors.toSet());
        Map<Long, String> sellerNames = sellerIds.isEmpty()
            ? Map.of()
            : userMapper.selectBatchIds(sellerIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));

        return products.stream().map(p -> {
            var vo = new ProductAuditVO();
            vo.setId(p.getId());
            vo.setTitle(p.getTitle());
            vo.setDescription(p.getDescription());
            vo.setImages(p.getImages());
            vo.setCondition(p.getCondition());
            vo.setSellerName(sellerNames.getOrDefault(p.getSellerId(), "未知"));
            vo.setCreatedAt(p.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void audit(Long productId, boolean approved, String reason) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");
        if (!"pending".equals(p.getAuditStatus())) throw new BusinessException("商品状态不是待审核");
        p.setAuditStatus(approved ? "approved" : "rejected");
        if (!approved) {
            p.setAuditReason(reason != null ? reason : "");
        }
        productMapper.updateById(p);
    }
}
