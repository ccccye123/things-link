package org.ccccye.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Service
@Slf4j
public class HttpTransportService {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;

    private Channel serverChannel;

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("Staring Http transport");

        bossGroup = new NioEventLoopGroup(1);
        // 默认线程数量是 CPU核心*2
        workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(8888))
                .childHandler(new HttpHandlerInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, false);
        serverChannel = bootstrap.bind().sync().channel();

        log.info("Http Transport Stated");
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping Http Transport");
        try {
            serverChannel.close().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
        log.info("Http Transport Stopped");
    }
}
