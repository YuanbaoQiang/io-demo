package org.example.network.block;

import org.example.constant.NetworkConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 阻塞IO - 服务端，使用Channel，可读可写
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:30
 */
public class Server {

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(NetworkConstant.PORT));
            System.out.println("Sever start listening port: " + NetworkConstant.PORT);
            for (; ; ) {
                SocketChannel socket = serverSocketChannel.accept();
                System.out.println("Sever receive a new connect: " + socket.getRemoteAddress());
                new TaskHandler(socket);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TaskHandler implements Runnable {
        private SocketChannel socketChannel;

        public TaskHandler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            Thread thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            ByteBuffer buf = ByteBuffer.allocate(512);
            try {
                socketChannel.read(buf);
                buf.flip();
                System.out.printf("Server thread: %s, receive msg from client: %s%n",
                        Thread.currentThread().getName(), new String(buf.array(), 0, buf.remaining()));
                socketChannel.write(ByteBuffer.wrap("server has received!".getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
