package com.itcast.service;

import com.itcast.order.dto.OrderDto;
import com.itcast.result.Result;

public interface OrderService {
    Result<Void> saveOrder(OrderDto orderDto);

    Result<Void> buyOrder(Long orderId);
}
