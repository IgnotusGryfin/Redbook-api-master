package com.itcast.product.vo;

import com.itcast.product.pojo.CustomAttribute;
import com.itcast.product.pojo.Product;
import com.itcast.product.pojo.Shop;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductVo extends Product {

    /**
     * 店铺
     */
    private Shop shop;

    /**
     * 商品属性
     */
    private List<CustomAttribute> customAttributes;
}
