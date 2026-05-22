package com.campustrade.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductVO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String condition;
    private String images;
    private Long categoryId;
    private Long sellerId;
    private String sellerName;
    private String status;
    private String auditStatus;
    private Integer viewCount;
    private Integer favCount;
    private LocalDateTime createdAt;
}
