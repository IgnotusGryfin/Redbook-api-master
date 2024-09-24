package com.itcast.controller;

import com.itcast.result.Result;
import com.itcast.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/note")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PutMapping("/like/{noteId}")
    public Result<Void> like(@PathVariable("noteId") Integer noteId) {
        return likeService.like(noteId);
    }

    @GetMapping("/isLike/{noteId}")
    public Result<Boolean> isLike(@PathVariable("noteId") Integer noteId) {
        return likeService.isLike(noteId);
    }
}
