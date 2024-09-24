package com.itcast.note.vo;

import com.itcast.note.pojo.Note;
import com.itcast.user.pojo.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoteVo extends Note {

    /**
     * 发布笔记用户信息
     */
    private User user;

    /**
     * 处理后的时间
     */
    private String dealTime;

    /**
     * 距离
     */
    private String distance;

    /**
     * 评论数
     */
    private Integer comment;
}
