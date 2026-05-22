package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.Favorite;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.FavoriteMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    public boolean toggle(Long userId, Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");

        Long count = favoriteMapper.selectCount(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));

        if (count > 0) {
            favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
            p.setFavCount(Math.max(0, (p.getFavCount() != null ? p.getFavCount() : 1) - 1));
            productMapper.updateById(p);
            return false;
        }

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setProductId(productId);
        favoriteMapper.insert(fav);
        p.setFavCount((p.getFavCount() != null ? p.getFavCount() : 0) + 1);
        productMapper.updateById(p);
        return true;
    }

    @Override
    public List<Product> listByUser(Long userId) {
        List<Favorite> favs = favoriteMapper.selectList(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt));

        List<Long> productIds = favs.stream().map(Favorite::getProductId).toList();
        if (productIds.isEmpty()) return List.of();

        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, String> sellerNames = loadSellerNames(products);
        products.forEach(p -> p.setSellerId(null)); // reuse void field to hold name temporarily
        return products;
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        return favoriteMapper.selectCount(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)) > 0;
    }

    private Map<Long, String> loadSellerNames(List<Product> products) {
        var ids = products.stream().map(Product::getSellerId).collect(Collectors.toSet());
        if (ids.isEmpty()) return Map.of();
        return userMapper.selectBatchIds(ids).stream()
            .collect(Collectors.toMap(User::getId, u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));
    }
}
