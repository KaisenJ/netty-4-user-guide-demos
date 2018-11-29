package com.waylau.netty.demo.echo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * 处理服务端 channel.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取事件 当客户端发来消息 读取时调用
     * @param ctx   处理链接的通道上下文
     * @param msg   消息内容
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //输出服务地址 以及客户端发来的信息
    	System.out.println(ctx.channel().remoteAddress()+"->Server :"+ msg.toString());
//        ctx.write(msg); // (1)
//        ctx.flush(); // (2)
//        final ChannelFuture future = ctx.writeAndFlush(msg);
        //ChannelFuture 异步处理 通道内的读写
        final ChannelFuture future = ctx.write("服务端消息已读取" + "\n");
//        future.addListener(ChannelFutureListener.CLOSE);
//        future.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                System.out.println("close channel");
//                future.channel().close();
//            }
//        });
    }

    /**
     * 通道读取完毕事件 消息读取完毕调用
     *
     * @param ctx 通道上下文
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server read complete");
        //将ctx.write()操作时 写入到缓冲区的数据强行输出
        ctx.flush();
        TimeUnit.MILLISECONDS.sleep(200);
    }

    /**
     * 通道内 读取消息 写入消息 出错时 调用
     * @param ctx 通道上下文
     * @param cause 错误信息
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
        // 当出现异常就关闭连接
        cause.printStackTrace();//打印异常信息
        ctx.close(); //关闭通道
    }
}