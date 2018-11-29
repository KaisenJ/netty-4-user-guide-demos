/**
 * 
 */
package com.waylau.netty.demo.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 说明：自定义协议服务端
 *
 * @author <a href="http://www.waylau.com">waylau.com</a> 2015年11月5日 
 */
public class ProtocolServer {

	private int port;
	
	private static final int MAX_FRAME_LENGTH = 1024 * 1024; //最大帧长度
	private static final int LENGTH_FIELD_LENGTH = 4; //定长字段长度
	private static final int LENGTH_FIELD_OFFSET = 6; //定长长度字段偏移量
	private static final int LENGTH_ADJUSTMENT = 0; //定长字段可调节量
	private static final int INITIAL_BYTES_TO_STRIP = 0; //初始字节去除
	
	/**
	 * 
	 */
	public ProtocolServer(int port) {
		this.port = port;
	}

	 public void run() throws Exception {
	        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap b = new ServerBootstrap(); // (2)
	            b.group(bossGroup, workerGroup)
	             .channel(NioServerSocketChannel.class) // (3)
	             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
	                     //设置自定义协议编码器
	 					ch.pipeline().addLast("decoder",
								new ProtocolDecoder(MAX_FRAME_LENGTH,
										LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH, 
										LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP));
	 					//设置自定义消息解码器
	                     ch.pipeline().addLast("encoder", new ProtocolEncoder());
	                     //设置自定义 事件处理器
	                     ch.pipeline().addLast(new ProtocolServerHandler());
	                 }
	             })
	             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
	             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)通道是否保活

	            // 绑定端口，开始接收进来的连接
	            ChannelFuture f = b.bind(port).sync(); // (7)
	            
	    		System.out.println("Server start listen at " + port );
	    		
	            // 等待服务器  socket 关闭 。
	            // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
	            f.channel().closeFuture().sync();
	            

	        } finally {
	            workerGroup.shutdownGracefully();
	            bossGroup.shutdownGracefully();
	        }
	    }
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8082;
        }
        new ProtocolServer(port).run();
	}

}
