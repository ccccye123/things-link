package org.ccccye.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 服务入口限流
 */
public class HttpRateEntryHandler extends ChannelInboundHandlerAdapter {
    private static final String entryRateKey = "ENTRY_RATE";

    private final HttpTransportContext transportContext;

    public HttpRateEntryHandler(HttpTransportContext httpTransportContext) {
        this.transportContext = httpTransportContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("entry");
        boolean pass = transportContext.getRateLimitRedis()
                .rateLimit(entryRateKey,
                        transportContext.getEntryBucketMax(),
                        transportContext.getEntryRateLimit());
        if (pass) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireExceptionCaught(new Exception("入口限流:" + transportContext.getEntryRateLimit()));
        }
    }
}
