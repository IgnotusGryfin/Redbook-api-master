package com.itcast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.order.pojo.OrderAttribute;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderAttributeMapper extends BaseMapper<OrderAttribute> {
}
