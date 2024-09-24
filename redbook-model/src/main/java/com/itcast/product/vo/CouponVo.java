package com.itcast.product.vo;

import com.itcast.product.pojo.Coupon;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CouponVo extends Coupon {
    /**
     * 是否可用
     */
    private Boolean isUsable;
}
