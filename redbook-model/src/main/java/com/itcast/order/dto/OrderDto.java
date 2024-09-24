package com.itcast.order.dto;

import com.itcast.product.pojo.CustomAttribute;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto implements Serializable {

    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 产品价格
     */
    private BigDecimal price;

    /**
     * 选择的属性
     */
    private List<CustomAttribute> selectAttributes;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 优惠券id
     */
    private Integer couponId;

    /**
     * 优惠券价格
     */
    private BigDecimal couponPrice;
}
