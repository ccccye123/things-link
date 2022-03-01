package org.ccccye.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpDeviceRateHandler extends ChannelInboundHandlerAdapter {
    private HttpTransportContext transportContext;

    public HttpDeviceRateHandler(HttpTransportContext transportContext) {
        this.transportContext = transportContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        String token = req.headers().getAsString("D-Token");

        int bucketMax = transportContext.getDeviceBucketMax();
        int rate = transportContext.getEntryRateLimit();

        boolean pass = transportContext.getRateLimitRedis().rateLimit(token, bucketMax, rate);
        if (pass) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireExceptionCaught(new Exception("设备限流:" + transportContext.getDeviceRateLimit()));
        }
    }
}
