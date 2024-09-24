package com.itcast.controller;

import com.itcast.product.vo.CouponVo;
import com.itcast.result.Result;
import com.itcast.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/getCouponsByUserId")
    public Result<List<CouponVo>> getCouponsByUserId() {
        return couponService.getCouponsByUserId();
    }

    @GetMapping("/useCoupon/{couponId}")
    public Result<Void> useCoupon(@PathVariable Integer couponId) {
        return couponService.useCoupon(couponId);
    }
}
