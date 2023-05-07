package org.example.network.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 从Reactor，分发读写事件
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:53
 */
public class SubReactor implements Runnable, Closeable {

    private final Selector selector;

    private static final int SUB_REACTOR_COUNT = 4;

    private static final SubReactor[] SUB_REACTORS = new SubReactor[SUB_REACTOR_COUNT];

    private static final ExecutorService POOL = Executors.newFixedThreadPool(SUB_REACTOR_COUNT);

    private static int index = 0;

    SubReactor() throws IOException {
        selector = Selector.open();
    }

    static {
        for (int i = 0; i < SUB_REACTOR_COUNT; i++) {
            try {
                SUB_REACTORS[i] = new SubReactor();
                POOL.submit(SUB_REACTORS[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Selector nextSelector() {
        Selector selector = SUB_REACTORS[index].selector;
        index = (index + 1) % SUB_REACTOR_COUNT;
        return selector;
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

    @Override
    public void run() {
        // 启动后直接等待selector监听到对应的事件即可，其他的操作逻辑和主Reactor一致
        try {
            for (; ; ) {
                int count = selector.select();
                System.out.println(Thread.currentThread().getName() + " has detected events count: " + count);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    Dispatcher.dispatch(iterator.next());
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
