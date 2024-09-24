package com.itcast.controller;

import com.itcast.result.Result;
import com.itcast.service.LoginService;
import com.itcast.user.dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/send/{phone}")
    public Result<String> send(@PathVariable String phone) {
        return loginService.send(phone);
    }

    @GetMapping("/verify")
    public Result<String> verify(LoginDto loginDto) {
        return loginService.verify(loginDto);
    }
}
