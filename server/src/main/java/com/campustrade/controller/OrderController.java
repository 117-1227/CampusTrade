package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Order;
import com.campustrade.security.JwtTokenProvider;
import com.campustrade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Long> body,
                                @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        orderService.create(userId, body.get("productId"));
        return Result.success(null, "下单成功");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                      @RequestBody Map<String, String> body,
                                      @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        orderService.updateStatus(id, userId, body.get("status"));
        return Result.success(null, "操作成功");
    }

    @GetMapping("/buy")
    public Result<List<Order>> listAsBuyer(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(orderService.listByBuyer(userId));
    }

    @GetMapping("/sell")
    public Result<List<Order>> listAsSeller(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(orderService.listBySeller(userId));
    }
}
