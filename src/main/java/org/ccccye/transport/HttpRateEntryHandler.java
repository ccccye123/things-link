package org.ccccye.transport;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务入口限流
 */
public class HttpRateEntryHandler extends ChannelInboundHandlerAdapter {
    private static final String ENTRY_RATE_KEY = "rate:entry.limit";
    private final int BUCKET_MAX;
    private final int RATE_LIMIT;

    private final HttpTransportContext transportContext;

    public HttpRateEntryHandler(HttpTransportContext transportContext) {
        this.transportContext = transportContext;
        this.BUCKET_MAX = transportContext.getEntryBucketMax();
        this.RATE_LIMIT = transportContext.getEntryRateLimit();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean pass = transportContext.getRateLimitRedis()
                .rateLimit(ENTRY_RATE_KEY,
                        this.BUCKET_MAX,
                        this.RATE_LIMIT);
        if (pass) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireExceptionCaught(new Exception("入口限流:" + this.RATE_LIMIT));
        }
    }
}
