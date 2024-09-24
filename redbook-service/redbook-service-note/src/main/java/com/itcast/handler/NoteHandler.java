package com.itcast.handler;

import com.itcast.note.dto.NoteDto;
import lombok.Setter;

import java.io.IOException;

@Setter
public abstract class NoteHandler {

    /**
     * 下一条责任链
     */
    protected NoteHandler nextHandler;

    /**
     * 处理责任链抽象类
     * @param noteDto noteDto
     */
    public abstract void handle(NoteDto noteDto) throws IOException, InterruptedException;
}
