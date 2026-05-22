package com.campustrade.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductDetailVO extends ProductVO {
    private String categoryName;
    private String sellerAvatar;
}
