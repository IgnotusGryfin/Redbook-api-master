package com.itcast.service.impl;

import com.itcast.client.CommentClient;
import com.itcast.client.UserClient;
import com.itcast.constant.ExceptionConstant;
import com.itcast.constant.RedisConstant;
import com.itcast.exception.NoteNoExistException;
import com.itcast.exception.UserNoExistException;
import com.itcast.handler.NoteHandler;
import com.itcast.mapper.NoteMapper;
import com.itcast.mapper.NoteScanMapper;
import com.itcast.note.dto.NoteDto;
import com.itcast.note.pojo.Note;
import com.itcast.note.pojo.NoteBrowse;
import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;
import com.itcast.service.NoteService;
import com.itcast.strategy.GetNotesStrategy;
import com.itcast.thread.UserThreadLocal;
import com.itcast.user.pojo.User;
import com.itcast.util.BloomFilterUtil;
import com.itcast.util.DealTimeUtil;
import com.itcast.util.DiffDayUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private NoteScanMapper noteScanMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CommentClient commentClient;

    @Autowired
    private BloomFilterUtil bloomFilterUtil;

    private static final Lock lock = new ReentrantLock();

    @Override
    public Result<NoteVo> getNote(Integer noteId) throws ParseException {
        Note note;
        // 1.查询redis中是否存在缓存
        Note cacheNote = (Note) redisTemplate.opsForHash().get(RedisConstant.NOTE_DETAIL_CACHE + noteId, "note");

        // 2.如果缓存不存在，则
        if (cacheNote == null) {
            try {
                // 2.1 加锁防止缓存击穿
                if (lock.tryLock()) {
                    log.info("加锁成功");
                    // 2.2 查询数据库
                    note = noteMapper.selectById(noteId);
                    if (note == null) {
                        redisTemplate.opsForHash().put(RedisConstant.NOTE_DETAIL_CACHE + noteId, "note", null);
                        throw new NoteNoExistException(ExceptionConstant.NOTE_NO_EXIST);
                    } else {
                        // 2.3 缓存到redis并设置有效时间
                        redisTemplate.opsForHash().put(RedisConstant.NOTE_DETAIL_CACHE + noteId, "note", note);
                        redisTemplate.expire(RedisConstant.NOTE_DETAIL_CACHE + noteId, 5, TimeUnit.MINUTES);
                    }
                } else {
                    Thread.sleep(50);
                    return getNote(noteId);
                }
            } catch (Exception e) {
                log.error(e.toString());
                return Result.success(null);
            } finally {
                lock.unlock();
            }
        } else {
            note = cacheNote;
        }

        // 3.获取发布笔记用户信息
        User user = userClient.getUserById(note.getUserId()).getData();
        if (user == null) {
            throw new UserNoExistException(ExceptionConstant.USER_NO_EXIST);
        }

        // 4.处理时间字符串
        int days = DiffDayUtil.diffDays(
                new SimpleDateFormat("yyyy-MM-dd").parse(note.getTime()), new Date());
        String dealTime = DealTimeUtil.dealTime(days);
        if (StringUtils.isBlank(dealTime)) {
            dealTime = note.getTime();
        }

        // 5.设置vo
        NoteVo noteVo = new NoteVo();
        BeanUtils.copyProperties(note, noteVo);
        noteVo.setUser(user);
        noteVo.setDealTime(dealTime);
//        noteVo.setComment(commentClient.getCommentCount(noteId).getData());

        // 6.加入布隆过滤器
        Integer loginUserId = UserThreadLocal.getUserId();
        bloomFilterUtil.add(RedisConstant.USER_BLOOM_FILTER + loginUserId, noteId.toString());

        // 7.记录用户访问笔记
        try {
            NoteBrowse noteBrowse = new NoteBrowse();
            noteBrowse.setNoteId(noteId);
            noteBrowse.setUserId(loginUserId);
            noteScanMapper.insert(noteBrowse);
        } catch (Exception e) {
            log.error("用户已经访问过，不需要再次插入数据库");
        }

        return Result.success(noteVo);
    }

    @Setter
    private GetNotesStrategy getNotesStrategy;

    @Override
    public Result<List<NoteVo>> getNotes() {
        return getNotesStrategy.getNotes();
    }

    @Autowired
    private NoteHandler setUserIdHandler;

    @Autowired
    private NoteHandler uploadImgHandler;

    @Autowired
    private NoteHandler getLocationHandler;

    @Autowired
    private NoteHandler filterTitleHandler;

    @Autowired
    private NoteHandler getTopicHandler;

    @Autowired
    private NoteHandler saveNoteHandler;

    @Autowired
    private NoteHandler saveNoteTopicHandler;

    @Autowired
    private NoteHandler saveLocationToRedisHandler;

    @Autowired
    private NoteHandler saveNoteToEsHandler;

    // 设置用户id -> 上传图片 -> 解析位置 -> 过滤标题敏感词 -> 提取话题 -> 保存笔记 -> 保存笔记话题   -> 保存笔记位置到redis -> 保存笔记到es
    @Override
    @Transactional
    public Result<Void> postNote(NoteDto dto) throws IOException, InterruptedException {
        log.info("发布笔记...");
        setUserIdHandler.setNextHandler(uploadImgHandler);
        uploadImgHandler.setNextHandler(getLocationHandler);
        getLocationHandler.setNextHandler(filterTitleHandler);
        filterTitleHandler.setNextHandler(getTopicHandler);
        getTopicHandler.setNextHandler(saveNoteHandler);
        saveNoteHandler.setNextHandler(saveNoteTopicHandler);
        saveNoteTopicHandler.setNextHandler(saveLocationToRedisHandler);
        saveLocationToRedisHandler.setNextHandler(saveNoteToEsHandler);
        setUserIdHandler.handle(dto);
        log.info("笔记发布成功...");
        return Result.success(null);
    }
}

