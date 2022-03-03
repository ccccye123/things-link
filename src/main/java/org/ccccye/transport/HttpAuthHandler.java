package org.ccccye.transport;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.SetOperations;

/**
 * 设备身份认证
 */
public class HttpAuthHandler extends ChannelInboundHandlerAdapter {
    private HttpTransportContext transportContext;

    private static final String TOKEN_KEY = "D-Token";
    private static final String DEVICES_REDIS_KEY = "device.token";

    public HttpAuthHandler(HttpTransportContext transportContext) {
        this.transportContext = transportContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest req = (FullHttpRequest) msg;
        // 从HTTP头部获取token
        String token = req.headers().getAsString(TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            ctx.fireExceptionCaught(new Exception("token为空"));
        } else {
            SetOperations<String, String> ops = transportContext.getStringRedisTemplate().opsForSet();
            Boolean exist = ops.isMember(DEVICES_REDIS_KEY, token);
            if (BooleanUtils.isNotFalse(exist)) {
                ctx.fireExceptionCaught(new Exception("token不存在," + token));
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }
}
