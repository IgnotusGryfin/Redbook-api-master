package com.itcast.service.impl;

import com.google.gson.Gson;
import com.itcast.client.UserClient;
import com.itcast.constant.RedisConstant;
import com.itcast.mapper.HistoryMapper;
import com.itcast.note.pojo.Note;
import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;
import com.itcast.search.pojo.History;
import com.itcast.service.SearchService;
import com.itcast.thread.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private UserClient userClient;

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<List<NoteVo>> search(String key) throws IOException {
        // 1.构造request
        SearchRequest request = new SearchRequest("rb_note");
        // 2.多字段查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery(key, "title", "content"));
        request.source(sourceBuilder);
        // 3.获取结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        // 4.设置vo
        List<NoteVo> noteVos = new ArrayList<>();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            // 4.1 解析JSON
            Note note = new Gson().fromJson(source, Note.class);
            // 4.2 设置vo
            NoteVo noteVo = new NoteVo();
            BeanUtils.copyProperties(note, noteVo);
            noteVo.setUser(userClient.getUserById(note.getUserId()).getData());
            noteVos.add(noteVo);
        }
        // 5.保存搜索记录
        try {
            History history = new History();
            history.setHistory(key);
            history.setUserId(UserThreadLocal.getUserId());
            historyMapper.insert(history);
        } catch (Exception e) {
            log.info("用户搜索重复");
        }
        // 6.保存热度
        redisTemplate.opsForZSet().incrementScore(RedisConstant.NOTE_SCORE, key, 1);
        return Result.success(noteVos);
    }
}
