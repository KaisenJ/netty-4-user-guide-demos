package com.waylau.netty.demo.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 处理服务端 channel.
 */

/**
 * 服务处理类
 *
 * ChannelInboundHandlerAdapter 事件处理类
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

    /**
     * 消息读取事件 （当服务端收到客户端发来的的消息时调用）
     * @param ctx 处理通道上下文
     * @param msg 消息 （ByteBuf类型）
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
    	/*
        // 默默地丢弃收到的数据
        ((ByteBuf) msg).release(); // (3)
        */
    	
        /*
        try {
	        // Do something with msg
	    } finally {
	        ReferenceCountUtil.release(msg);
	    }
        */
        
	    ByteBuf in = (ByteBuf) msg;
	    try {
	        while (in.isReadable()) { // (1) 读取消息
	            System.out.print((char) in.readByte());
	            System.out.flush();
	        }
	    } finally {
	        //显示释放通道  ReferenceCountUtil 引用类计数工具类
	        ReferenceCountUtil.release(msg); // (2)
	    }
        
    }

    /**
     * 异常事件处理 当通道的IO 处理出现异常时 调用改事件
     * @param ctx 通道上下文
     * @param cause 异常类型 以及内容
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 当出现异常就关闭连接
        cause.printStackTrace(); //打印异常详情
        ctx.close(); //关闭通道
    }
}