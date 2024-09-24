package com.itcast.service.Impl;

import com.itcast.client.UserClient;
import com.itcast.model.dto.CommentDto;
import com.itcast.model.pojo.Comment;
import com.itcast.model.vo.CommentVo;
import com.itcast.result.Result;
import com.itcast.service.CommentService;
import com.itcast.thread.UserThreadLocal;
import com.itcast.util.DealTimeUtil;
import com.itcast.util.DiffDayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserClient userClient;

    @Override
    public Result<Void> postComment(CommentDto commentDto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        comment.setUserId(UserThreadLocal.getUserId());
        comment.setTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        mongoTemplate.insert(comment);
        return Result.success(null);
    }

    @Override
    public Result<List<CommentVo>> getCommentList(Integer noteId) throws ParseException {
        List<CommentVo> commentVoList = new ArrayList<>();
        // 1.根据笔记id获取所有评论列表
        Query query = new Query(
                Criteria.where("noteId").is(noteId)).with(Sort.by(Sort.Order.desc("_id")));
        List<Comment> commentList = mongoTemplate.find(query, Comment.class, "rb_comment");
        // 2.根据parentId=0获取所有一级评论列表
        List<Comment> parentList = commentList.stream().filter(comment -> "0".equals(comment.getParentId())).collect(Collectors.toList());
        // 3.构建树模型
        for (Comment comment : parentList) {
            CommentVo commentVo = convertToCommentVo(comment);
            commentVo.setChildrenList(buildChildrenList(comment.getId().toString(), commentList));
            commentVoList.add(commentVo);
        }
        return Result.success(commentVoList);
    }

    @Override
    public Result<Integer> getCommentCount(Integer noteId) {
        Integer count = Math.toIntExact(mongoTemplate.count(
                new Query(
                        Criteria.where("noteId").is(noteId)), Comment.class, "rb_comment"));
        return Result.success(count);
    }

    /**
     * 实体转换成vo
     * @param comment
     * @return
     * @throws ParseException
     */
    private CommentVo convertToCommentVo(Comment comment) throws ParseException {
        String dealTime = DealTimeUtil.dealTime(
                DiffDayUtil.diffDays(
                        new SimpleDateFormat("yyyy-MM-dd").parse(comment.getTime()), new Date()));
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        commentVo.setDealTime(dealTime);
        commentVo.setCommentVoId(comment.getId().toString());
        commentVo.setUser(userClient.getUserById(comment.getUserId()).getData());
        return commentVo;
    }

    /**
     * 递归构建评论列表
     * @param parentId
     * @param commentList
     * @return
     * @throws ParseException
     */
    private List<CommentVo> buildChildrenList(String parentId, List<Comment> commentList) throws ParseException {
        List<CommentVo> childrenVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            if (parentId.equals(comment.getParentId())) {
                CommentVo commentVo = convertToCommentVo(comment);
                commentVo.setChildrenList(buildChildrenList(comment.getId().toString(), commentList));
                childrenVoList.add(commentVo);
            }
        }
        return childrenVoList;
    }

}
