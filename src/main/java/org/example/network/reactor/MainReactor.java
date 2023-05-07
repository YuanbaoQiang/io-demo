package org.example.network.reactor;

import org.example.constant.NetworkConstant;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 主Reactor，分发连接事件
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:53
 */
public class MainReactor implements Runnable, Closeable {

    private final ServerSocketChannel serverSocketChannel;

    private final Selector selector;

    MainReactor() throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
    }

    @Override
    public void close() throws IOException {
        serverSocketChannel.close();
        selector.close();
    }

    @Override
    public void run() {
        System.out.println("Server start listening port: " + NetworkConstant.PORT);
        try {
            serverSocketChannel.bind(new InetSocketAddress(NetworkConstant.PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(serverSocketChannel));
            for (; ; ) {
                int count = selector.select();
                System.out.println("event count: " + count);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    Dispatcher.dispatch(iterator.next());
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
