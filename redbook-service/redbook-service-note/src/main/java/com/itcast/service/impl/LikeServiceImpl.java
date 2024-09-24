package com.itcast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.constant.ExceptionConstant;
import com.itcast.constant.MqConstant;
import com.itcast.constant.RedisConstant;
import com.itcast.enums.MessageTypeEnum;
import com.itcast.exception.NoteNoExistException;
import com.itcast.mapper.LikeMapper;
import com.itcast.mapper.NoteMapper;
import com.itcast.message.pojo.Message;
import com.itcast.note.pojo.Like;
import com.itcast.note.pojo.Note;
import com.itcast.result.Result;
import com.itcast.service.LikeService;
import com.itcast.thread.UserThreadLocal;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public Result<Void> like(Integer noteId) {
        Integer userId = UserThreadLocal.getUserId();
        // 1.判断redis中的本用户是否存在这个id
        Boolean isLike = redisTemplate.opsForValue().getBit(RedisConstant.LIKE_SET_CACHE + noteId, userId);
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new NoteNoExistException(ExceptionConstant.NOTE_NO_EXIST);
        }
        if (Boolean.TRUE.equals(isLike)) {
            // 删除点赞
            LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Like::getNoteId, noteId);
            likeMapper.delete(queryWrapper);
            // 更新redis
            redisTemplate.opsForValue().setBit(RedisConstant.LIKE_SET_CACHE + noteId, userId, false);
            // 更新点赞数
            note.setLike(note.getLike() - 1);
        } else {
            // 添加点赞
            Like like = new Like();
            like.setNoteId(noteId);
            like.setUserId(userId);
            likeMapper.insert(like);
            // 更新redis
            redisTemplate.opsForValue().setBit(RedisConstant.LIKE_SET_CACHE + noteId, userId, true);
            // 更新点赞数
            note.setLike(note.getLike() + 1);
            // 用户点赞，消息发送
            Message likeMessage = new Message();
            likeMessage.setType(MessageTypeEnum.LIKE.getCode());
            likeMessage.setNoticeId(note.getUserId());
            likeMessage.setObj(like);
            rabbitTemplate.convertAndSend(MqConstant.MESSAGE_NOTICE_EXCHANGE, MqConstant.LIKE_KEY, likeMessage);
        }
        // 2.更新点赞数
        noteMapper.updateById(note);
        // 3.删除笔记缓存
        redisTemplate.delete(RedisConstant.NOTE_DETAIL_CACHE + noteId);
        return Result.success(null);
    }

    @Override
    public Result<Boolean> isLike(Integer noteId) {
        Integer userId = UserThreadLocal.getUserId();
        Boolean isLike = redisTemplate.opsForValue().getBit(
                RedisConstant.LIKE_SET_CACHE + noteId, userId);
        return Result.success(isLike);
    }
}
