package com.itcast.controller;

import com.itcast.result.Result;
import com.itcast.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/note")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping("/isCollection/{noteId}")
    public Result<Boolean> isCollection(@PathVariable("noteId") Long noteId) {
        return collectionService.isCollection(noteId);
    }

    @PutMapping("/collection/{noteId}")
    public Result<Void> collection(@PathVariable("noteId") Integer noteId) {
        return collectionService.collection(noteId);
    }
}
