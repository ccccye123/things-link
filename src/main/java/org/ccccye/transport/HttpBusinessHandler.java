package org.ccccye.transport;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class HttpBusinessHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static AtomicInteger count = new AtomicInteger(0);
    ExecutorService threadPool = Executors.newFixedThreadPool(8);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (req.method() != HttpMethod.POST) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("error", CharsetUtil.UTF_8))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }

        threadPool.execute(() -> {
            // 处理请求
            String body = req.content().toString(CharsetUtil.UTF_8);
//        System.out.println(body);
            count.addAndGet(1);
            System.out.println("success: " + count.get());

            // 应答
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer("ok", CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn(cause.getMessage());
        ByteBuf msg = Unpooled.copiedBuffer("error", CharsetUtil.UTF_8);
        ctx.writeAndFlush(msg);
        ctx.close();
    }
}
