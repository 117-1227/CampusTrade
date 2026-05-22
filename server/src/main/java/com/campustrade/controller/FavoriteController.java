package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.entity.Product;
import com.campustrade.security.JwtTokenProvider;
import com.campustrade.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/products/{id}/favorite")
    public Result<Boolean> toggle(@PathVariable Long id,
                                   @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        boolean favorited = favoriteService.toggle(userId, id);
        return Result.success(favorited, favorited ? "已收藏" : "已取消收藏");
    }

    @GetMapping("/favorites")
    public Result<List<Product>> myFavorites(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(favoriteService.listByUser(userId));
    }

    @GetMapping("/products/{id}/favorite")
    public Result<Boolean> checkFavorited(@PathVariable Long id,
                                           @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(favoriteService.isFavorited(userId, id));
    }
}
