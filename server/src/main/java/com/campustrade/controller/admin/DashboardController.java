package com.campustrade.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.Result;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @GetMapping
    public Result<Map<String, Long>> stats() {
        long totalUsers = userMapper.selectCount(null);
        long totalProducts = productMapper.selectCount(null);
        long pendingAudits = productMapper.selectCount(
            new LambdaQueryWrapper<Product>().eq(Product::getAuditStatus, "pending"));
        long approvedProducts = productMapper.selectCount(
            new LambdaQueryWrapper<Product>().eq(Product::getAuditStatus, "approved"));

        return Result.success(Map.of(
            "totalUsers", totalUsers,
            "totalProducts", totalProducts,
            "pendingAudits", pendingAudits,
            "approvedProducts", approvedProducts
        ));
    }
}
