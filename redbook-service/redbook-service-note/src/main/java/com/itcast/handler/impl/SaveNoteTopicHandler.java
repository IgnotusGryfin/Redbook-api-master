package com.itcast.handler.impl;

import com.itcast.handler.NoteHandler;
import com.itcast.mapper.NoteTopicMapper;
import com.itcast.note.dto.NoteDto;
import com.itcast.note.pojo.NoteTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SaveNoteTopicHandler extends NoteHandler {

    @Autowired
    private NoteTopicMapper noteTopicMapper;

    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        List<Integer> topicIdList = noteDto.getTopicList();
        for (Integer topicId : topicIdList) {
            NoteTopic noteTopic = new NoteTopic();
            noteTopic.setTopicId(topicId);
            noteTopic.setNoteId(noteDto.getId());
            noteTopicMapper.insert(noteTopic);
        }

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
