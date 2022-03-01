package org.ccccye.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.SetOperations;

public class HttpAuthHandler extends ChannelInboundHandlerAdapter {
    private HttpTransportContext transportContext;

    public HttpAuthHandler(HttpTransportContext transportContext) {
        this.transportContext = transportContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest req = (FullHttpRequest) msg;
        String token = req.headers().getAsString("D-Token");
        if (StringUtils.isBlank(token)) {
            ctx.fireExceptionCaught(new Exception("token为空"));
        } else {
            SetOperations<String, String> ops = transportContext.getStringRedisTemplate().opsForSet();
            Boolean exist = ops.isMember("device.token", token);
            if (BooleanUtils.isNotFalse(exist)) {
                ctx.fireExceptionCaught(new Exception("token不存在," + token));
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }
}
