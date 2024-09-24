package com.itcast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.product.pojo.Coupon;
import com.itcast.product.pojo.UserCoupon;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
    List<Coupon> getCouponsByUserId(Integer userId);
}
