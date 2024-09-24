package com.itcast.service;

import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    Result<List<NoteVo>> search(String key) throws IOException;
}
