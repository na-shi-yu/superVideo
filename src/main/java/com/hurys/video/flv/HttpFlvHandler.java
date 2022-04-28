package com.hurys.video.flv;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * HttpFlvHandler
 *
 * @author yyk
 */
@Slf4j
public class HttpFlvHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * flv客户端连接数
     */
    private AtomicInteger connectNum;
    /**
     * 检测的最大连接数
     */
    private int MAX_CONNECTS = 100;
    /**
     * flv请求参数
     */
    private int PATH_VARIABLES = 3;

    public static Map<String, String> channelMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.uri();
/*
            List<String> appAndStreamName = Splitter.on("/").omitEmptyStrings().splitToList(uri);
            if (appAndStreamName.size() != PATH_VARIABLES) {
                httpResponseStreamNotExist(ctx, uri);
                return;
            }
            // 保存请求信息
            String chanelId = ctx.channel().id().asLongText();
            String token = appAndStreamName.get(2);
            channelMap.put(chanelId, token);
            // 获取流
            String app = appAndStreamName.get(0);
            String streamName = appAndStreamName.get(1);
            if (streamName.endsWith(".flv")) {
                streamName = streamName.substring(0, streamName.length() - 4);
            }
*/

            DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(CONTENT_TYPE, "video/x-flv");
            response.headers().set(TRANSFER_ENCODING, "chunked");
            response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
            response.headers().set(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");
            ctx.writeAndFlush(response);

        }
    }

    private void httpResponseStreamNotExist(ChannelHandlerContext ctx, String uri) {
        ByteBuf body = Unpooled.wrappedBuffer(("stream [" + uri + "] not exist").getBytes());
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND, body);
        response.headers().set(CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (connectNum.incrementAndGet() % MAX_CONNECTS != 0) {
            log.debug("当前连接httpFlv服务的客户端数量：{}", connectNum.get());
        } else {
            log.debug("当前无客户端连接httpFlv服务");
        }
    }


}
