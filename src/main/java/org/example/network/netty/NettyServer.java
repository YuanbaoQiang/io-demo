package org.example.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.example.constant.NetworkConstant;

/**
 * @author: YuanbaoQiang
 * @createTime: 2023/5/3 14:29
 */
public class NettyServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup(1);
        EventLoopGroup handlerGroup = new DefaultEventLoopGroup(10);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        //  ChannelInboundHandlerAdapter 入站的处理器
                        channel.pipeline()
                                .addLast(new StringCodec())
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                // 处理消息接收
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                        // msg默认为ByteBuf形式 先经过StringCodec处理
                                        //ByteBuf buf = (ByteBuf) msg;
                                        System.out.println(Thread.currentThread().getName() + " >> 收到客户端发送的数据: " + msg);
                                        handlerGroup.submit(() -> {
                                            System.out.println("当前处理线程：" + Thread.currentThread().getName());
                                            ctx.channel().writeAndFlush(Unpooled.wrappedBuffer("已收到！".getBytes()));
                                        });
                                    }
                                });
                    }
                });
        ChannelFuture future = bootstrap.bind(NetworkConstant.PORT);

        //future.addListener(channelFuture -> {
        //    System.out.println("listener");
        //    System.out.println("服务器当前状态：" + future.isDone());
        //    System.out.println("我是服务器启动完成之后要做的事情");
        //});
        //System.out.println("服务器当前状态：" + future.isDone());
    }
}
