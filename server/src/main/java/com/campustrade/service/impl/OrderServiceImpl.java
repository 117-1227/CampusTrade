package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.Order;
import com.campustrade.entity.Product;
import com.campustrade.mapper.OrderMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @Override
    public void create(Long buyerId, Long productId) {
        Product p = productMapper.selectById(productId);
        if (p == null) throw new BusinessException("商品不存在");
        if (!"active".equals(p.getStatus())) throw new BusinessException("商品已售出或已下架");
        if (buyerId.equals(p.getSellerId())) throw new BusinessException("不能购买自己的商品");

        Long pending = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getProductId, productId)
                .eq(Order::getStatus, "pending"));
        if (pending > 0) throw new BusinessException("该商品已有待处理订单");

        p.setStatus("reserved");
        productMapper.updateById(p);

        Order order = new Order();
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(p.getSellerId());
        order.setStatus("pending");
        orderMapper.insert(order);
    }

    @Override
    public void updateStatus(Long orderId, Long userId, String newStatus) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (!"pending".equals(order.getStatus())) throw new BusinessException("订单状态不允许操作");

        boolean isBuyer = userId.equals(order.getBuyerId());
        boolean isSeller = userId.equals(order.getSellerId());

        if ("completed".equals(newStatus) && isSeller) {
            order.setStatus("completed");
            Product p = productMapper.selectById(order.getProductId());
            if (p != null) { p.setStatus("sold"); productMapper.updateById(p); }
        } else if ("cancelled".equals(newStatus) && (isBuyer || isSeller)) {
            order.setStatus("cancelled");
            Product p = productMapper.selectById(order.getProductId());
            if (p != null) { p.setStatus("active"); productMapper.updateById(p); }
        } else {
            throw new BusinessException("无权操作此订单");
        }

        orderMapper.updateById(order);
    }

    @Override
    public List<Order> listByBuyer(Long buyerId) {
        return orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerId, buyerId)
                .orderByDesc(Order::getCreatedAt));
    }

    @Override
    public List<Order> listBySeller(Long sellerId) {
        return orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getSellerId, sellerId)
                .orderByDesc(Order::getCreatedAt));
    }
}
