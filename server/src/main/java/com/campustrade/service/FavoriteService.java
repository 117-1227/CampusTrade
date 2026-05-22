package com.campustrade.service;

import com.campustrade.entity.Product;
import java.util.List;

public interface FavoriteService {

    /** 收藏/取消收藏，返回 true=已收藏, false=已取消 */
    boolean toggle(Long userId, Long productId);

    /** 用户的收藏商品列表 */
    List<Product> listByUser(Long userId);

    /** 是否已收藏 */
    boolean isFavorited(Long userId, Long productId);
}
