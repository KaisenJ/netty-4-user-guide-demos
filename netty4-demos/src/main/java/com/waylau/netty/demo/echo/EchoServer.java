package com.waylau.netty.demo.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 应答服务器
 */
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        //创建 两个处理I/O操作的多线程循环处理器
        // boss 接收传入的链接 并将其注册到 worker上
        // worker 处理已经传入的链接
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)配置辅助类
            b.group(bossGroup, workerGroup) //加入 boss 链接接收组 worker 链接处理组
             .channel(NioServerSocketChannel.class) // (3) 指定处理链接的通道类
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4) 开始处理新传入的Channel ChannelInitializer帮助配置新的Channel的类
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                    // DelimiterBasedFrameDecoder 数据传输长度设置 读取设置 maxFrameLength 一次解析最大数据长度
                	 ch.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                     //Decoder 编码器
                	 ch.pipeline().addLast("decoder", new StringDecoder());
                     //Encoder 解码器
                	 ch.pipeline().addLast("encoder", new StringEncoder());
                	 //自定义事件处理 Handler
                     ch.pipeline().addLast(new EchoServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5) 设置 backlog 大小
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6) 设置服务 活性

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

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8040;
        }
        new EchoServer(port).run();
    }
}