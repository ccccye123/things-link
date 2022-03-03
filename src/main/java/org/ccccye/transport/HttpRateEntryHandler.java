package org.ccccye.transport;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务入口限流
 */
public class HttpRateEntryHandler extends ChannelInboundHandlerAdapter {
    private static final String ENTRY_RATE_KEY = "ENTRY_RATE";

    private final HttpTransportContext transportContext;

    public HttpRateEntryHandler(HttpTransportContext httpTransportContext) {
        this.transportContext = httpTransportContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean pass = transportContext.getRateLimitRedis()
                .rateLimit(ENTRY_RATE_KEY,
                        transportContext.getEntryBucketMax(),
                        transportContext.getEntryRateLimit());
        if (pass) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireExceptionCaught(new Exception("入口限流:" + transportContext.getEntryRateLimit()));
        }
    }
}
