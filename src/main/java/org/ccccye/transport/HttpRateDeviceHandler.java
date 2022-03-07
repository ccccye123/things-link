package org.ccccye.transport;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 按设备限流
 */
public class HttpRateDeviceHandler extends ChannelInboundHandlerAdapter {
    private HttpTransportContext transportContext;
    private final int BUCKET_MAX;
    private final int RATE_LIMIT;
    private static final String DEVICE_RATE_KEY_TEMPLATE = "rate:device.%s.limit";

    public HttpRateDeviceHandler(HttpTransportContext transportContext) {
        this.transportContext = transportContext;
        this.BUCKET_MAX = transportContext.getDeviceBucketMax();
        this.RATE_LIMIT = transportContext.getDeviceRateLimit();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        String token = req.headers().getAsString("D-Token");
        String redisKey = String.format(DEVICE_RATE_KEY_TEMPLATE, token);

        boolean pass = transportContext.getRateLimitRedis().rateLimit(redisKey, BUCKET_MAX, RATE_LIMIT);
        if (pass) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireExceptionCaught(new Exception("设备限流:" + RATE_LIMIT));
        }
    }
}
