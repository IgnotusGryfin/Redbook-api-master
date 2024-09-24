package com.itcast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.constant.ExceptionConstant;
import com.itcast.constant.MqConstant;
import com.itcast.constant.RedisConstant;
import com.itcast.enums.MessageTypeEnum;
import com.itcast.exception.NoteNoExistException;
import com.itcast.mapper.CollectionMapper;
import com.itcast.mapper.NoteMapper;
import com.itcast.message.pojo.Message;
import com.itcast.note.pojo.Collection;
import com.itcast.note.pojo.Note;
import com.itcast.result.Result;
import com.itcast.service.CollectionService;
import com.itcast.thread.UserThreadLocal;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    public Result<Boolean> isCollection(Long noteId) {
        Integer userId = UserThreadLocal.getUserId();
        Boolean isCollection = redisTemplate.opsForValue().getBit(
                RedisConstant.COLLECTION_SET_CACHE + noteId, userId);
        return Result.success(isCollection);
    }

    @Override
    public Result<Void> collection(Integer noteId) {
        Integer userId = UserThreadLocal.getUserId();
        // 1.判断redis中的本用户是否存在这个id
        Boolean isCollection = redisTemplate.opsForValue().getBit(RedisConstant.COLLECTION_SET_CACHE + noteId, userId);
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new NoteNoExistException(ExceptionConstant.NOTE_NO_EXIST);
        }
        if (Boolean.TRUE.equals(isCollection)) {
            // 删除收藏
            LambdaQueryWrapper<Collection> queryWrapper = new LambdaQueryWrapper<Collection>()
                    .eq(Collection::getNoteId, noteId);
            collectionMapper.delete(queryWrapper);
            // 更新redis
            redisTemplate.opsForValue().setBit(RedisConstant.COLLECTION_SET_CACHE + noteId, userId, false);
            // 更新收藏数
            note.setCollection(note.getCollection() - 1);
        } else {
            // 添加收藏
            Collection collection = new Collection();
            collection.setNoteId(noteId);
            collection.setUserId(userId);
            collectionMapper.insert(collection);
            // 更新redis
            redisTemplate.opsForValue().setBit(RedisConstant.COLLECTION_SET_CACHE + noteId, userId, true);
            // 更新收藏数
            note.setCollection(note.getCollection() + 1);
            // 用户收藏，消息发送
            Message collectionMessage = new Message();
            collectionMessage.setType(MessageTypeEnum.COLLECTION.getCode());
            collectionMessage.setNoticeId(note.getUserId());
            collectionMessage.setObj(collection);
            rabbitTemplate.convertAndSend(MqConstant.MESSAGE_NOTICE_EXCHANGE, MqConstant.COLLECTION_KEY, collectionMessage);
        }
        // 2.更新收藏数
        noteMapper.updateById(note);
        // 3.删除笔记缓存
        redisTemplate.delete(RedisConstant.NOTE_DETAIL_CACHE + noteId);
        return Result.success(null);
    }
}
