package com.itcast.handler.impl;

import com.itcast.handler.NoteHandler;
import com.itcast.mapper.NoteMapper;
import com.itcast.note.dto.NoteDto;
import com.itcast.note.pojo.Note;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SaveNoteHandler extends NoteHandler {

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        Note note = new Note();
        BeanUtils.copyProperties(noteDto, note);
        noteMapper.insert(note);
        noteDto.setId(note.getId());

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
