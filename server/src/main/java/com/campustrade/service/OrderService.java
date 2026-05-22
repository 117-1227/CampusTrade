package com.campustrade.service;

import com.campustrade.entity.Order;
import java.util.List;

public interface OrderService {
    void create(Long buyerId, Long productId);
    void updateStatus(Long orderId, Long userId, String newStatus);
    List<Order> listByBuyer(Long buyerId);
    List<Order> listBySeller(Long sellerId);
}
