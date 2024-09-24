package com.itcast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.constant.ExceptionConstant;
import com.itcast.exception.FileIsNullException;
import com.itcast.exception.UserNoExistException;
import com.itcast.mapper.UserMapper;
import com.itcast.result.Result;
import com.itcast.service.UserService;
import com.itcast.thread.UserThreadLocal;
import com.itcast.user.pojo.User;
import com.itcast.user.vo.UserVo;
import com.itcast.util.AgeUtil;
import com.itcast.util.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OssUtil ossUtil;

    @Override
    public Result<User> getInfo() throws ParseException {
        // 1.获取登录用户信息
        Integer userId = UserThreadLocal.getUserId();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UserNoExistException(ExceptionConstant.USER_NO_EXIST);
        }
        // 2.设置userVo
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        // 3.根据生日生成年龄
        if (!StringUtils.isBlank(user.getBirthday())) {
            Integer age = AgeUtil.calAge(user.getBirthday());
            userVo.setAge(age);
        }
        // 4.返回结果
        return Result.success(userVo);
    }

    @Override
    public Result<User> getUserById(Integer userId) {
        log.info("根据id查询用户信息...");
        return Result.success(userMapper.selectById(userId));
    }

    @Override
    public Result<Void> updateImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FileIsNullException(ExceptionConstant.FILE_IS_NULL);
        }
        log.info("用户更新头像...");
        // 1.上传头像
        String url = ossUtil.uploadImg(file.getBytes());
        // 2.根据userId更新数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, UserThreadLocal.getUserId());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UserNoExistException(ExceptionConstant.USER_NO_EXIST);
        }
        // 3.设置头像
        user.setImage(url);
        // 4.更新数据库
        userMapper.updateById(user);
        return Result.success(null);
    }

    @Override
    public Result<Void> editInfo(User user) {
        log.info("用户更新个人信息...");
        userMapper.updateById(user);
        return Result.success(null);
    }
}
