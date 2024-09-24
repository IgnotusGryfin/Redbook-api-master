package com.itcast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

/**
 * 年龄计算工具类
 */
public class AgeUtil {

    public static Integer calAge(String birthdayString) throws ParseException {
        // 1.解析生日
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date birthdayDate = format.parse(birthdayString);
        LocalDate birthday = birthdayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 2.获取当前时间
        LocalDate now = LocalDate.now();

        // 3.计算年龄
        return Period.between(birthday, now).getYears();
    }
}
