package com.waylau.netty.demo.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {
	
	@Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1) 链接建立事件
        final ByteBuf time = ctx.alloc().buffer(4); // (2) 设置发送消息的字节长度
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        final ChannelFuture f = ctx.writeAndFlush(time); // (3) 发送消息
        f.addListener(new ChannelFutureListener() { //设置监听
            /**
             * 处理操作完成 调用
             * @param future
             */
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        }); // (4)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
