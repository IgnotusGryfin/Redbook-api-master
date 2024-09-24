package com.itcast.product.dto;

import com.itcast.product.pojo.Product;
import com.itcast.product.pojo.ProductAttribute;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductDto extends Product {

    /**
     * 商品属性
     */
    private ProductAttribute productAttribute;
}
