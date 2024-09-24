package com.itcast.handler.impl;

import com.itcast.handler.NoteHandler;
import com.itcast.note.dto.NoteDto;
import com.itcast.thread.UserThreadLocal;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SetUserIdHandler extends NoteHandler {
    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        Integer userId = UserThreadLocal.getUserId();
        noteDto.setUserId(userId);

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
