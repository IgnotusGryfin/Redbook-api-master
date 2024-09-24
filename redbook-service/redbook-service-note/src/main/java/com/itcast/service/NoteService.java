package com.itcast.service;

import com.itcast.note.dto.NoteDto;
import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface NoteService {
    Result<NoteVo> getNote(Integer noteId) throws ParseException;

    Result<List<NoteVo>> getNotes();

    Result<Void> postNote(NoteDto dto) throws IOException, InterruptedException;
}
