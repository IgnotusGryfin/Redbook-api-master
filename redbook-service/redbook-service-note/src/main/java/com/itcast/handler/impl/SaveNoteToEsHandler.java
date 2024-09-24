package com.itcast.handler.impl;

import com.google.gson.Gson;
import com.itcast.handler.NoteHandler;
import com.itcast.note.dto.NoteDto;
import com.itcast.note.es.NoteEs;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SaveNoteToEsHandler extends NoteHandler {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        IndexRequest request = new IndexRequest("rb_note").id(String.valueOf(noteDto.getId()));
        NoteEs noteEs = new NoteEs();
        BeanUtils.copyProperties(noteDto, noteEs);
        String noteJson = new Gson().toJson(noteEs);
        request.source(noteJson, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
