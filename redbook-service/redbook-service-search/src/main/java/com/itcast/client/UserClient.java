package com.itcast.client;

import com.itcast.result.Result;
import com.itcast.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("redbook-service-user")
public interface UserClient {

    @GetMapping("/user/getUserById/{userId}")
    Result<User> getUserById(@PathVariable("userId") Integer userId);
}
