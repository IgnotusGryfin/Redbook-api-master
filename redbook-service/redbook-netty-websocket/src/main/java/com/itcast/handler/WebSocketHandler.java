package com.itcast.handler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.itcast.session.Session;
import com.itcast.util.JwtUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@ChannelHandler.Sharable
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 1.定义Map的类型
        String token = msg.text();
        if (!StringUtils.isBlank(token)) {
            try {
                Integer userId = Integer.valueOf(JwtUtil.parseToken(token));
                Session.bind(userId ,ctx.channel());
                log.info("用户登录，登录用户的ID为：" + userId);
            } catch (TokenExpiredException e) {
                log.error("用户token过期，请重新认证后重试");
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        Integer userId = Session.getUserId(channel);
        Session.unbind(userId,ctx.channel());
    }
}
