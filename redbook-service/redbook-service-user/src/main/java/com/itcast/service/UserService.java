package com.itcast.service;

import com.itcast.result.Result;
import com.itcast.user.pojo.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface UserService {
    Result<User> getInfo() throws ParseException;

    Result<User> getUserById(Integer userId);

    Result<Void> updateImage(MultipartFile file) throws IOException;

    Result<Void> editInfo(User user);
}
