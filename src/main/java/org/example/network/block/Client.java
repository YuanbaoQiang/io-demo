package org.example.network.block;

import org.example.constant.NetworkConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * 客户端，使用Channel，可读可写
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 13:40
 */
public class Client {
    public static void main(String[] args) {
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(NetworkConstant.LOCAL_HOST,
                NetworkConstant.PORT)); Scanner scanner = new Scanner(System.in)) {
            System.out.println("Connect successfully!");
            System.out.print("Send msg to server: ");
            String text = scanner.nextLine();

            // send msg to server
            channel.write(ByteBuffer.wrap(text.getBytes()));
            System.out.println("send successfully");

            // read msg from server
            ByteBuffer buffer = ByteBuffer.allocate(512);
            channel.read(buffer);
            buffer.flip();
            System.out.println("Client receive msg: " + new String(buffer.array(), 0, buffer.remaining()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
