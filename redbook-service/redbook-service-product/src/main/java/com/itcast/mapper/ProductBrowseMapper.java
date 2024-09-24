package com.itcast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.product.pojo.ProductBrowse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductBrowseMapper extends BaseMapper<ProductBrowse> {
}
