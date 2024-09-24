package com.itcast.handler.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.handler.NoteHandler;
import com.itcast.mapper.TopicMapper;
import com.itcast.note.dto.NoteDto;
import com.itcast.note.pojo.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GetTopicHandler extends NoteHandler {

    private final Pattern pattern = Pattern.compile("#(\\S+)\\s");

    @Autowired
    private TopicMapper topicMapper;



    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        Matcher matcher = pattern.matcher(noteDto.getContent());
        StringBuilder sb = new StringBuilder();
        List<Integer> topicIdList = new ArrayList<>();
        while (matcher.find()) {
            // 获取匹配到的字符串
            String match = matcher.group(1);
            LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Topic::getContent, match);
            Topic topic = topicMapper.selectOne(queryWrapper);

            // 保存话题
            if (topic == null) {
                Topic dbTopic = new Topic();
                dbTopic.setContent(match);
                dbTopic.setHot(1);
                topicMapper.insert(dbTopic);
                topicIdList.add(dbTopic.getId());
            } else {
                topic.setHot(topic.getHot() + 1);
                topicMapper.updateById(topic);
                topicIdList.add(topic.getId());
            }

            // 处理笔记内容中的话题
            matcher.appendReplacement(sb, "<span style='color: #687F9D;'>" + "#" + match + " " + "</span>");
            matcher.appendTail(sb);
        }
        if (sb.length() > 0) noteDto.setContent(sb.toString());
        noteDto.setTopicList(topicIdList);

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
