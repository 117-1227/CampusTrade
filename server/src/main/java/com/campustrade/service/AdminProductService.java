package com.campustrade.service;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminProductService {

    List<ProductAuditVO> listPending(int page, int pageSize);

    void audit(Long productId, boolean approved, String reason);

    @Data
    class ProductAuditVO {
        private Long id;
        private String title;
        private String description;
        private String images;
        private String condition;
        private String sellerName;
        private String auditor;
        private LocalDateTime createdAt;
    }
}
