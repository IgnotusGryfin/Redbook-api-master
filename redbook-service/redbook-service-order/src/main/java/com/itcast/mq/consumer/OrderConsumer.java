package com.itcast.mq.consumer;

import com.itcast.constant.MqConstant;
import com.itcast.enums.OrderStatusEnum;
import com.itcast.mapper.OrderAttributeMapper;
import com.itcast.mapper.OrderMapper;
import com.itcast.order.dto.OrderDto;
import com.itcast.order.pojo.Order;
import com.itcast.order.pojo.OrderAttribute;
import com.itcast.product.pojo.CustomAttribute;
import com.itcast.thread.UserThreadLocal;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class OrderConsumer {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderAttributeMapper orderAttributeMapper;

    @RabbitListener(queues = MqConstant.SAVE_ORDER_QUEUE)
    public void onMessage(OrderDto orderDto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        try {
            log.info("第一个消息消费者监听，处理消息：" + orderDto);

            // 保存订单
            Order order = new Order();
            order.setProductId(orderDto.getProductId());
            order.setQuantity(orderDto.getQuantity());
            order.setCouponId(orderDto.getCouponId());
            order.setUserId(UserThreadLocal.getUserId());
            order.setStatus(OrderStatusEnum.DUE.getCode());

            BigDecimal finalPrice = new BigDecimal(0);
            finalPrice = finalPrice.add(orderDto.getPrice()).multiply(BigDecimal.valueOf(orderDto.getQuantity())).subtract(orderDto.getCouponPrice());
            order.setFinalPrice(finalPrice);

            orderMapper.insert(order);

            // 保存订单属性
            Long orderId = order.getId();
            List<CustomAttribute> selectAttributes = orderDto.getSelectAttributes();
            for (CustomAttribute selectAttribute : selectAttributes) {
                OrderAttribute orderAttribute = new OrderAttribute();
                orderAttribute.setOrderId(orderId);
                orderAttribute.setLabel(selectAttribute.getLabel());
                orderAttribute.setValue(selectAttribute.getValue().get(0));
                orderAttributeMapper.insert(orderAttribute);
            }

            channel.basicAck(deliveryTag, false);
            log.info("消息已确认");
        } catch (Exception e) {
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
