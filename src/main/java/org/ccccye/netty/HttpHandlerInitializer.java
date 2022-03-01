package org.ccccye.netty;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpHandlerInitializer extends ChannelInitializer<SocketChannel> {
    private HttpTransportContext httpTransportContext;

    public HttpHandlerInitializer(HttpTransportContext httpTransportContext) {
        this.httpTransportContext = httpTransportContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("compressor", new HttpContentCompressor());
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
        pipeline.addLast("entryHandler", new HttpRateEntryHandler(httpTransportContext));
        pipeline.addLast("authHandler", new HttpAuthHandler(httpTransportContext));
        pipeline.addLast("deviceHandler", new HttpDeviceRateHandler(httpTransportContext));
        pipeline.addLast("handler", new HttpTransportHandler());
    }
}
