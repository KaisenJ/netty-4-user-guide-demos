package com.waylau.netty.demo.simplechat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 服务端 channel
 * 
 * @author waylau.com
 * @date 2015-2-16
 */
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> { // (1)
	
	/**
	 * A thread-safe Set  Using ChannelGroup, you can categorize Channels into a meaningful group.
	 * A closed Channel is automatically removed from the collection,
	 */
	//创建通道线程组 组内信息共享
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 添加通道事件 共享信息
     * @param ctx  通道
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel(); //获取传入通道
        
        // Broadcast a message to multiple Channels 输出通道IP地址信息 组内广播信息
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入\n");
        
        channels.add(ctx.channel()); //加入共享信息通道线程组群
    }

    /**
     * 移除通道事件 将该通道从共享信息线程组 中 移除 即断线事件
     * @param ctx 通道
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        
        // Broadcast a message to multiple Channels 组内广播移除的通道信息
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 离开\n");
        
        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channels.remove(ctx.channel());"
    }

    /**
     * 读取消息事件
     * @param ctx 用户通道
     * @param s 消息（当前用户发来的消息）
     * @throws Exception
     */
    @Override
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception { // (4)
		Channel incoming = ctx.channel(); //获取通道
		for (Channel channel : channels) { //循环遍历通道
            if (channel != incoming){   //判断通道是否为当前用户
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
            } else {
            	channel.writeAndFlush("[you]" + s + "\n");
            }
        }
	}

    /**
     * 通道激活事件 （通道连接成功）
     * @param ctx 通道上下文
     * @throws Exception
     */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"在线");
	}

    /**
     * 通道断开时调用
     * @param ctx
     * @throws Exception
     */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"掉线");
	}

    /**
     * 通道操作异常时调用
     * @param ctx
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
    	Channel incoming = ctx.channel();
		System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}