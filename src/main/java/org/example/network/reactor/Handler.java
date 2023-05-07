package org.example.network.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理读写
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:54
 */
public class Handler implements Runnable {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(2);

    private final SocketChannel channel;

    Handler(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(128);
            channel.read(buf);
            buf.flip();
            POOL.submit(() -> {
                try {
                    System.out.printf("Thread: %s process read operation%n", Thread.currentThread().getName());
                    System.out.printf("receive msg from: %s, msg: %s%n", channel.getRemoteAddress().toString(),
                            new String(buf.array(), 0, buf.remaining()));
                    // 给客户端发送消息
                    channel.write(ByteBuffer.wrap("pong".getBytes()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            // 客户端建立了连接，但是随后中断连接，此时的读取操作报错，直接关闭socket
            try {
                channel.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
