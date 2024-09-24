package com.itcast.controller;

import com.itcast.result.Result;
import com.itcast.service.UserService;
import com.itcast.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getInfo")
    public Result<User> getInfo() throws ParseException {
        return userService.getInfo();
    }

    @GetMapping("/getUserById/{userId}")
    public Result<User> getUserById(@PathVariable("userId") Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/updateImage")
    public Result<Void> updateImage(@RequestParam("image") MultipartFile file) throws IOException {
        return userService.updateImage(file);
    }

    @PutMapping("/editInfo")
    public Result<Void> editInfo(@RequestBody User user) {
        return userService.editInfo(user);
    }
}
