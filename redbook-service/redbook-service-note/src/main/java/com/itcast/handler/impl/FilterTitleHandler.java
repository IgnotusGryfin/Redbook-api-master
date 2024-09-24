package com.itcast.handler.impl;

import com.google.gson.Gson;
import com.itcast.handler.NoteHandler;
import com.itcast.note.dto.NoteDto;
import lombok.Data;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilterTitleHandler extends NoteHandler {

    /**
     * 敏感词
     */
    @Data
    public static class SensitiveWord {
        private String sensitiveWord;
    }

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        String newTitle = sensitiveWordFilter(noteDto.getTitle());
        noteDto.setTitle(newTitle);

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }

    /**
     * 敏感词过滤
     * @param text
     * @return
     */
    public String sensitiveWordFilter(String text) {
        // 1.构建搜索请求
        SearchRequest searchRequest = new SearchRequest("rb_sensitive_word");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("sensitiveWord", text));
        searchRequest.source(searchSourceBuilder);

        List<String> sensitiveWords = new ArrayList<>();
        try {
            // 2.执行搜索请求
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            // 3.处理搜索结果
            SearchHit[] hits = searchResponse.getHits().getHits();
            for (SearchHit hit : hits) {
                String source = hit.getSourceAsString();
                // 4.解析JSON
                SensitiveWord sensitiveWord = new Gson().fromJson(source, SensitiveWord.class);
                sensitiveWords.add(sensitiveWord.getSensitiveWord());
            }
            // 5.过滤
            for (String sensitiveWord : sensitiveWords) {
                text = text.replaceAll(sensitiveWord, "**");
            }
            return text;
        } catch (IOException e) {
            return null;
        }
    }
}
