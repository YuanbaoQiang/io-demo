package org.example.network.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.example.constant.NetworkConstant;

import java.util.Scanner;

/**
 * @author: YuanbaoQiang
 * @createTime: 2023/5/5 23:07
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new StringCodec())
                                .addLast(new ChannelInboundHandlerAdapter() {

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(">> 接收到服务端发送的消息：" + msg);
                                        super.channelRead(ctx, msg);
                                    }
                                });
                    }
                });
        // 异步操作，本行代码执行完之后直接往下走
        Channel channel = bootstrap.connect(NetworkConstant.LOCAL_HOST, NetworkConstant.PORT).channel();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("<< 请输入要发送给服务端的内容：");
                String text = scanner.nextLine();
                if (text.isEmpty()) {
                    continue;
                }
                if ("exit".equals(text)) {
                    ChannelFuture future = channel.close();
                    future.sync(); // 同步 等待通道完全关闭
                    break;
                }
                ChannelFuture future = channel.writeAndFlush(Unpooled.wrappedBuffer(text.getBytes()));
                System.out.println("任务完成状态：" + future.isDone());
            }
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
