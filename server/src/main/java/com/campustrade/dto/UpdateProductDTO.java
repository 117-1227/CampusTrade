package com.campustrade.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateProductDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String condition;
    private List<String> images;
    private Long categoryId;
}
