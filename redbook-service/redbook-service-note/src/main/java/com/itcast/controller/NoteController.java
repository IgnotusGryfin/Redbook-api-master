package com.itcast.controller;

import com.itcast.note.dto.NoteDto;
import com.itcast.note.vo.NoteVo;
import com.itcast.result.Result;
import com.itcast.service.NoteService;
import com.itcast.service.impl.NoteServiceImpl;
import com.itcast.strategy.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private GetNoteList getNoteList;

    @Autowired
    private GetNoteListByLocation getNoteListByLocation;

    @Autowired
    private GetNoteListByOwn getNoteListByOwn;

    @Autowired
    private GetNoteListByScan getNoteListByScan;

    @Autowired
    private GetNoteListByAttention getNoteListByAttention;

    @Autowired
    private GetNoteListByCollection getNoteListByCollection;

    @Autowired
    private GetNoteListByLike getNoteListByLike;

    @GetMapping("/getNoteList")
    public Result<List<NoteVo>> getNoteList(
            @RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize) {
        getNoteList.setPage(page);
        getNoteList.setPageSize(pageSize);
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteList);
        return noteService.getNotes();
    }

    @GetMapping("/getNoteListByLocation")
    public Result<List<NoteVo>> getNoteListByLocation(
            @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude) {
        getNoteListByLocation.setLongitude(longitude);
        getNoteListByLocation.setLatitude(latitude);
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteListByLocation);
        return noteService.getNotes();
    }

    @GetMapping("/getNoteListByOwn")
    public Result<List<NoteVo>> getNoteListByOwn() {
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteListByOwn);
        return noteService.getNotes();
    }

    @GetMapping("/getNoteListByScan")
    public Result<List<NoteVo>> getNoteListByScan() {
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteListByScan);
        return noteService.getNotes();
    }

    @GetMapping("/getNoteListByAttention")
    public Result<List<NoteVo>> getNoteListByAttention() {
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteListByAttention);
        return noteService.getNotes();
    }

    @GetMapping("/getNoteByCollection")
    public Result<List<NoteVo>> getNoteByCollection() {
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteListByCollection);
        return noteService.getNotes();
    }

    @GetMapping("/getNoteByLike")
    public Result<List<NoteVo>> getNoteByLike() {
        ((NoteServiceImpl) noteService).setGetNotesStrategy(getNoteListByLike);
        return noteService.getNotes();
    }

    @GetMapping("/getNote/{noteId}")
    public Result<NoteVo> getNote(@PathVariable("noteId") Integer noteId) throws ParseException {
        return noteService.getNote(noteId);
    }

    @PostMapping("/postNote")
    public Result<Void> postNote(@RequestParam("image") MultipartFile file,
                                 @RequestParam("title") String title,
                                 @RequestParam("content") String content,
                                 @RequestParam("longitude") String longitude,
                                 @RequestParam("latitude") String latitude) throws IOException, InterruptedException {
        // 构造dto
        NoteDto dto = new NoteDto();
        dto.setFile(file);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setLongitude(Double.valueOf(longitude));
        dto.setLatitude(Double.valueOf(latitude));
        dto.setLike(0);
        dto.setCollection(0);
        return noteService.postNote(dto);
    }
}




















