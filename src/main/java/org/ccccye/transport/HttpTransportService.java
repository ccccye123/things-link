package org.ccccye.transport;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Service
@Slf4j
public class HttpTransportService {
    @Autowired
    private HttpTransportContext httpTransportContext;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;

    private Channel serverChannel;

    @Value("${netty.port}")
    private Integer nettyPort;

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("Staring Http transport");

        bossGroup = new NioEventLoopGroup(1);
        // 默认线程数量是 CPU核心*2
        workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(this.nettyPort))
                .childHandler(new HttpHandlerInitializer(httpTransportContext))
                .childOption(ChannelOption.SO_KEEPALIVE, false);

//        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
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
