package org.example.network.reactor;

import java.nio.channels.SelectionKey;

/**
 * 分发处理器
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 14:18
 */
public class Dispatcher {
    public static void dispatch(SelectionKey key) {
        // 获取attachment，ServerSocketChannel和对应的客户端Channel都添加了的
        Object att = key.attachment();
        if (att instanceof Runnable) {
            // 由于Handler和Acceptor都实现自Runnable接口，这里就统一调用一下
            // 这样就实现了对应的时候调用对应的Handler或是Acceptor了
            ((Runnable) att).run();
        }
    }
}
