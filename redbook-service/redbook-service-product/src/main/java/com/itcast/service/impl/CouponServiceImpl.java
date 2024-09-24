package com.itcast.service.impl;

import com.itcast.mapper.CouponMapper;
import com.itcast.mapper.UserCouponMapper;
import com.itcast.product.pojo.Coupon;
import com.itcast.product.vo.CouponVo;
import com.itcast.result.Result;
import com.itcast.service.CouponService;
import com.itcast.thread.UserThreadLocal;
import com.itcast.util.IsExpireUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public Result<List<CouponVo>> getCouponsByUserId() {
        // 1.获取登录用户id
        Integer userId = UserThreadLocal.getUserId();

        // 2.根据用户id查找到用户可用的优惠券
        List<Coupon> couponsByUserId = userCouponMapper.getCouponsByUserId(userId);

        List<CouponVo> couponVoList = couponsByUserId.stream().map(coupon -> {
            CouponVo couponVo = new CouponVo();
            BeanUtils.copyProperties(coupon, couponVo);

            // 判断是否可用（是否过期，是否有库存）
            boolean isUsable = coupon.getStock() > 0 && !IsExpireUtil.isExpire(coupon.getTime());
            couponVo.setIsUsable(isUsable);
            return couponVo;
        }).collect(Collectors.toList());

        return Result.success(couponVoList);
    }

    @Override
    public Result<Void> useCoupon(Integer couponId) {
        RLock lock = redissonClient.getLock("useCoupon");
        try {
            boolean res = lock.tryLock(100, TimeUnit.SECONDS);
            if (res) {
                // 获取数据库优惠券的库存
                Coupon coupon = couponMapper.selectById(couponId);
                Integer stock = coupon.getStock();
                if (stock > 0) {
                    log.info("扣减库存...");
                    coupon.setStock(stock - 1);
                    couponMapper.updateById(coupon);
                }
            }
        } catch (InterruptedException e) {
            log.info("获取锁时发生异常");
        } finally {
            lock.unlock();
        }
        return Result.success(null);
    }
}
