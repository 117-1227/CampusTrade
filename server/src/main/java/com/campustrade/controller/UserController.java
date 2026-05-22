package com.campustrade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.common.Result;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public Result<User> me(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        User user = userMapper.selectById(userId);
        return Result.success(sanitize(user));
    }

    @PutMapping("/me")
    public Result<User> updateMe(@RequestHeader("Authorization") String auth,
                                  @RequestBody User updates) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (updates.getNickname() != null) user.setNickname(updates.getNickname());
        if (updates.getPhone() != null) user.setPhone(updates.getPhone());
        if (updates.getAvatar() != null) user.setAvatar(updates.getAvatar());
        if (updates.getCampus() != null) user.setCampus(updates.getCampus());
        userMapper.updateById(user);
        return Result.success(sanitize(user), "更新成功");
    }

    /** 去除敏感字段：password / role / status */
    private User sanitize(User u) {
        u.setPassword(null);
        return u;
    }

    @GetMapping("/{id}/profile")
    public Result<Map<String, Object>> seller(@PathVariable Long id) {
        User seller = userMapper.selectById(id);
        if (seller == null) throw new BusinessException("用户不存在");

        // 仅暴露公开字段，不泄露 password/phone/role/status
        Map<String, Object> safeSeller = Map.of(
            "id", seller.getId(),
            "username", seller.getUsername(),
            "nickname", seller.getNickname() != null ? seller.getNickname() : "",
            "avatar", seller.getAvatar() != null ? seller.getAvatar() : "",
            "campus", seller.getCampus() != null ? seller.getCampus() : ""
        );

        List<Product> products = productMapper.selectList(
            new LambdaQueryWrapper<Product>()
                .eq(Product::getSellerId, id)
                .eq(Product::getStatus, "active")
                .eq(Product::getAuditStatus, "approved")
                .orderByDesc(Product::getCreatedAt));

        return Result.success(Map.of(
            "seller", safeSeller,
            "products", products
        ));
    }
}
