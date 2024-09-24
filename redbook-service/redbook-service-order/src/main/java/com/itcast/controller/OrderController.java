package com.itcast.controller;

import com.itcast.order.dto.OrderDto;
import com.itcast.order.vo.OrderVo;
import com.itcast.result.Result;
import com.itcast.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/saveOrder")
    public Result<Void> saveOrder(@RequestBody OrderDto orderDto) {
        return orderService.saveOrder(orderDto);
    }

    @PutMapping("/buyOrder/{orderId}")
    public Result<Void> buyOrder(@PathVariable Long orderId) {
        return orderService.buyOrder(orderId);
    }

    @GetMapping("/getOrderList")
    public Result<List<OrderVo>> getOrderList() {
        return null;
    }
}
