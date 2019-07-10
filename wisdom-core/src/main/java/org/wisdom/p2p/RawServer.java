package org.wisdom.p2p;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// plain netty server
@Component
public class RawServer extends ChannelInboundHandlerAdapter {
    @Value("${p2p.port}")
    private int port;


    @Autowired
    private ApplicationContext ctx;

    private List<MessageHandler> handlers;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) { // (1)
        final ByteBuf data = (ByteBuf) msg;
        try {
            Wisdom.Message m = Wisdom.Message.parseFrom(data.array());
            for(MessageHandler h: handlers){
                h.handleMessage(m);
            }
        } catch (Exception e) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @PostConstruct
    public void init() throws Exception {
        this.handlers = Arrays.asList(this.ctx.getBeansOfType(MessageHandler.class).values().toArray(new MessageHandler[]{}));
        this.handlers.sort(Comparator.comparing(MessageHandler::getPriority));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(this);
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
