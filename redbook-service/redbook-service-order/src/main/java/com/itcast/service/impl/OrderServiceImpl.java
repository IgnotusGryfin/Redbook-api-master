package com.itcast.service.impl;

import com.itcast.client.ProductClient;
import com.itcast.constant.MqConstant;
import com.itcast.enums.OrderStatusEnum;
import com.itcast.mapper.OrderMapper;
import com.itcast.order.dto.OrderDto;
import com.itcast.order.pojo.Order;
import com.itcast.product.pojo.Product;
import com.itcast.result.Result;
import com.itcast.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ProductClient productClient;

    @Override
    public Result<Void> saveOrder(OrderDto orderDto) {
        // 扣减库存--防止超卖
        RLock lock = redissonClient.getLock("saveOrder");
        try {
            boolean res = lock.tryLock(100, TimeUnit.SECONDS);
            if (res) {
                // 获取商品库存
                Product product = productClient.getProductById(orderDto.getProductId()).getData();
                Integer stock = product.getStock();
                Integer sales = product.getSales();
                if (stock > 0) {
                    log.info("扣减库存,增加销量");
                    product.setStock(stock - 1);
                    product.setSales(sales + 1);
                    productClient.updateProduct(product);

                    // 异步保存订单
                    rabbitTemplate.convertAndSend(MqConstant.SAVE_ORDER_EXCHANGE, "", orderDto);
                } else {
                    log.info("商品已经售空");
                    return Result.failure("商品已经售空");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }

        return Result.success(null);
    }

    @Override
    public Result<Void> buyOrder(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatusEnum.PAID.getCode());
        orderMapper.updateById(order);
        return Result.success(null);
    }
}
