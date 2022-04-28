package com.hurys.video.flv;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * flv服务
 *
 * @author yyk
 */
@Slf4j
public class HttpFlvServer {

    private int port;

    ChannelFuture channelFuture;

    EventLoopGroup eventLoopGroup;

    int handlerThreadPoolSize;

    private AtomicInteger connectNum;

    public HttpFlvServer(int port, int threadPoolSize) {
        this.port = port;
        this.handlerThreadPoolSize = threadPoolSize;
        connectNum = new AtomicInteger(0);
    }

    public void run() throws Exception {
        eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpRequestDecoder());
                        ch.pipeline().addLast(new HttpResponseEncoder());
                        ch.pipeline().addLast(new HttpFlvHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        channelFuture = b.bind(port).sync();
        log.info("HttpFlv启动成功 ,监听端口:{}", port);
    }
}

