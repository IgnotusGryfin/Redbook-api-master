package com.itcast.service;

import com.itcast.product.vo.CouponVo;
import com.itcast.result.Result;

import java.util.List;

public interface CouponService {
    Result<List<CouponVo>> getCouponsByUserId();

    Result<Void> useCoupon(Integer couponId);
}
