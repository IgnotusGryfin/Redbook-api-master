package com.itcast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.annotation.AutoTime;
import com.itcast.user.pojo.Attention;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttentionMapper extends BaseMapper<Attention> {

    @AutoTime
    int insert(Attention attention);
}
