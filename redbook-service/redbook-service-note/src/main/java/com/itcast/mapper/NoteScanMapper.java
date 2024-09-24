package com.itcast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.annotation.AutoTime;
import com.itcast.note.pojo.NoteBrowse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteScanMapper extends BaseMapper<NoteBrowse> {

    @AutoTime
    int insert(NoteBrowse entity);
}
