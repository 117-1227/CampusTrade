package com.campustrade.controller.admin;

import com.campustrade.common.Result;
import com.campustrade.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping("/pending")
    public Result<List<AdminProductService.ProductAuditVO>> listPending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(adminProductService.listPending(page, pageSize));
    }

    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = (boolean) body.get("approved");
        String reason = (String) body.getOrDefault("reason", null);
        adminProductService.audit(id, approved, reason);
        return Result.success(null, approved ? "审核通过" : "已驳回");
    }
}
