package org.example.network.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 处理连接请求
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:54
 */
public class Acceptor implements Runnable {

    private final ServerSocketChannel serverSocketChannel;

    Acceptor(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("Sever receive a new connect: " + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);

            // 将感兴趣的事件注册给从reactor的selector上
            // 选取下一个从Reactor的Selector，初始化从线程
            Selector selector = SubReactor.nextSelector();
            // 在注册之前唤醒一下防止卡死
            selector.wakeup();

            // 注册从Reactor的Selector
            socketChannel.register(selector, SelectionKey.OP_READ, new Handler(socketChannel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
