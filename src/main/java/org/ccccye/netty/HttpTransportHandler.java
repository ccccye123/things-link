package org.ccccye.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpTransportHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private ByteBuf ok = Unpooled.copiedBuffer("ok", CharsetUtil.UTF_8);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (req.method() != HttpMethod.POST) {
            return;
        }

        // 处理请求
        String body = req.content().toString(CharsetUtil.UTF_8);
        System.out.println(body);

        // 应答
        ByteBuf out = ok;
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                out);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ByteBuf msg = Unpooled.copiedBuffer("error", CharsetUtil.UTF_8);
        ctx.writeAndFlush(msg);
        ctx.close();
    }
}
