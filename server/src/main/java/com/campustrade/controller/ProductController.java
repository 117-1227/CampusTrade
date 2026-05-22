package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.dto.CreateProductDTO;
import com.campustrade.dto.UpdateProductDTO;
import com.campustrade.security.JwtTokenProvider;
import com.campustrade.service.ProductService;
import com.campustrade.vo.ProductDetailVO;
import com.campustrade.vo.ProductVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Result<ProductVO> create(@Valid @RequestBody CreateProductDTO dto,
                                     @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        ProductVO vo = productService.create(userId, dto.getTitle(), dto.getDescription(),
            dto.getPrice(), dto.getOriginalPrice(), dto.getCondition(),
            dto.getImages(), dto.getCategoryId());
        return Result.success(vo, "发布成功，等待审核");
    }

    @PutMapping("/{id}")
    public Result<ProductVO> update(@PathVariable Long id,
                                     @Valid @RequestBody UpdateProductDTO dto,
                                     @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        ProductVO vo = productService.update(id, userId, dto.getTitle(), dto.getDescription(),
            dto.getPrice(), dto.getOriginalPrice(), dto.getCondition(),
            dto.getImages(), dto.getCategoryId());
        return Result.success(vo, "更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                                @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        productService.delete(id, userId);
        return Result.success(null, "删除成功");
    }

    @GetMapping("/{id}")
    public Result<ProductDetailVO> getById(@PathVariable Long id) {
        ProductDetailVO vo = productService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/my")
    public Result<List<ProductVO>> myProducts(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(productService.listBySeller(userId));
    }

    @GetMapping
    public Result<List<ProductVO>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(productService.listByKeyword(keyword, categoryId, page, pageSize));
    }
}
