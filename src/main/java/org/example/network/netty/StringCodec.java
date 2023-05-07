package org.example.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 入站：ByteBuf -> String，出站：String -> ByteBuf
 *
 * @author: YuanbaoQiang
 * @createTime: 2023/5/7 11:47
 */
public class StringCodec extends MessageToMessageCodec<ByteBuf, String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        list.add(Unpooled.wrappedBuffer(s.getBytes()));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(byteBuf.toString(StandardCharsets.UTF_8));
    }
}
