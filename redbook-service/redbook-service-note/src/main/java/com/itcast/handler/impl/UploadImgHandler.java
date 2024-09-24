package com.itcast.handler.impl;

import com.itcast.handler.NoteHandler;
import com.itcast.note.dto.NoteDto;
import com.itcast.util.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class UploadImgHandler extends NoteHandler {

    @Autowired
    private OssUtil ossUtil;

    @Override
    public void handle(NoteDto noteDto) throws IOException, InterruptedException {
        MultipartFile file = noteDto.getFile();
        String path = ossUtil.uploadImg(file.getBytes());
        noteDto.setImage(path);

        if(nextHandler != null) {
            nextHandler.handle(noteDto);
        }
    }
}
