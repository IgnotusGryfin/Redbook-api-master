package com.itcast.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.client.UserClient;
import com.itcast.constant.ExceptionConstant;
import com.itcast.exception.NoteNoExistException;
import com.itcast.mapper.LikeMapper;
import com.itcast.mapper.NoteMapper;
import com.itcast.note.pojo.Like;
import com.itcast.note.pojo.Note;
import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;
import com.itcast.strategy.GetNotesStrategy;
import com.itcast.thread.UserThreadLocal;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetNoteListByLike implements GetNotesStrategy {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public Result<List<NoteVo>> getNotes() {
        // 1.获取用户收藏
        Integer userId = UserThreadLocal.getUserId();
        LambdaQueryWrapper<Like> queryWrapper
                = new LambdaQueryWrapper<Like>().eq(Like::getUserId, userId);
        List<Like> likeList = likeMapper.selectList(queryWrapper);
        if (likeList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        // 2.设置vo
        List<NoteVo> noteVoList = likeList.stream().map(like -> {
            Note note = noteMapper.selectById(like.getNoteId());
            if (note == null) {
                throw new NoteNoExistException(ExceptionConstant.NOTE_NO_EXIST);
            }
            NoteVo noteVo = new NoteVo();
            BeanUtils.copyProperties(note, noteVo);
            noteVo.setUser(userClient.getUserById(note.getUserId()).getData());
            return noteVo;
        }).collect(Collectors.toList());
        return Result.success(noteVoList);
    }
}
