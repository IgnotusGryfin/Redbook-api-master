package com.itcast.strategy;

import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;

import java.util.List;

public interface GetNotesStrategy {
    Result<List<NoteVo>> getNotes();
}
