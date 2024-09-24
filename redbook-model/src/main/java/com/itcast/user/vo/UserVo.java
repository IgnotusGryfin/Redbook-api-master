package com.itcast.user.vo;

import com.itcast.user.pojo.User;
import lombok.Data;

@Data
public class UserVo extends User {
    /**
     * 年龄
     */
    private Integer age;
}
