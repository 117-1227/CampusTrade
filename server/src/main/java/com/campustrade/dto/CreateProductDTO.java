package com.campustrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductDTO {
    @NotBlank @Size(min = 2, max = 200)
    private String title;
    @NotBlank @Size(min = 10, max = 5000)
    private String description;
    @NotNull
    private BigDecimal price;
    private BigDecimal originalPrice;
    @NotBlank
    private String condition;
    private List<String> images;
    @NotNull
    private Long categoryId;
}
