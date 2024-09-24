package com.itcast.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

@ChannelHandler.Sharable
public class WebSocketChannelHandler extends ChannelInitializer<SocketChannel> {

    private final WebSocketHandler webSocketHandler = new WebSocketHandler();

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                .addLast(new HttpServerCodec())//http解码器
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new ChunkedWriteHandler())
                .addLast(new WebSocketServerProtocolHandler("/socket"))//将http协议升级为ws协议，保持长连接
                .addLast(webSocketHandler);
    }
}
