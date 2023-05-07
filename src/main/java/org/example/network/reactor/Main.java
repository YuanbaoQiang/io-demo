package org.example.network.reactor;

/**
 * 需要JDK17启动， JDK8启动，多客户端连接会出现卡死情况
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:53
 */
public class Main {
    public static void main(String[] args) {
        try (MainReactor mainReactor = new MainReactor()) {
            mainReactor.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
