package com.campustrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    @TableField("`condition`")
    private String condition;
    private String images; // JSON string
    private Long categoryId;
    private Long sellerId;
    private String status;
    private String auditStatus;
    private String auditReason;
    private Integer viewCount;
    private Integer favCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
