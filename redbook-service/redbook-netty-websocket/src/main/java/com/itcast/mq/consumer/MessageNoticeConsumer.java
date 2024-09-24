package com.itcast.mq.consumer;

import com.google.gson.Gson;
import com.itcast.constant.MqConstant;
import com.itcast.message.pojo.Message;
import com.itcast.note.pojo.Collection;
import com.itcast.note.pojo.Like;
import com.itcast.session.Session;
import com.itcast.user.pojo.Attention;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageNoticeConsumer {

    @RabbitListener(queues = {MqConstant.LOGIN_NOTICE_QUEUE})
    public void loginNotice(Integer userId) {
        log.info("登录用户的ID：" + userId);
    }

    @RabbitListener(queues = MqConstant.LIKE_NOTICE_QUEUE)
    public void likeNotice(Message message) {
        Like like = (Like) message.getObj();
        log.info("点赞用户的ID：" + like.getUserId() + ",点赞的笔记：" + like.getNoteId());
        Channel channel = Session.getChannel(message.getNoticeId());
        if (channel != null) {
            log.info("该用户的channel不为空,执行点赞通知操作");
            String json = new Gson().toJson(message);
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    @RabbitListener(queues = MqConstant.COLLECTION_NOTICE_QUEUE)
    public void collectionNotice(Message message) {
        Collection collection = (Collection) message.getObj();
        log.info("收藏用户的ID：" + collection.getUserId() + ",收藏的笔记ID：" + collection.getNoteId());
        Channel channel = Session.getChannel(message.getNoticeId());
        if (channel != null) {
            log.info("该用户的channel不为空,执行点赞通知操作");
            String json = new Gson().toJson(message);
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    @RabbitListener(queues = MqConstant.ATTENTION_NOTICE_QUEUE)
    public void attentionNotice(Message message) {
        Attention attention = (Attention) message.getObj();
        log.info("关注用户的ID：" + attention.getOwnId() + ",关注的人ID：" + attention.getOtherId());
        Channel channel = Session.getChannel(message.getNoticeId());
        if (channel != null) {
            log.info("该用户的channel不为空,执行点赞通知操作");
            String json = new Gson().toJson(message);
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }
}
