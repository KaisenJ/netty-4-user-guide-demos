package com.waylau.netty.demo.discard;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 丢弃任何进入的数据
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        //EventLoopGroup 处理I/O操作的多线程事件循环器
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1) 接收进入的链接 并将其注册到 worker上
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //处理已经收到的链接
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2) 启动NIO服务辅助类
            b.group(bossGroup, workerGroup) //加入链接处理循环器
             .channel(NioServerSocketChannel.class) // (3) 指定通道类 来处理进入的链接
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4) 处理最近接收到的Channel ChannelInitializer配置新Channel的辅助类
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     //加入 自定义事件处理类
                     ch.pipeline().addLast(new DiscardServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5) 设置 backlog 大小
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6) 设置通道活性

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync(); // (7)

            // 等待服务器  socket 关闭 。
            // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
            f.channel().closeFuture().sync();
        } finally {
            //关闭处理链接的I/O 多线程事件处理器
            workerGroup.shutdownGracefully();
            //关闭 接收链接 多线程处理器
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        //设置服务器端口
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8081;
        }
        //启动服务
        new DiscardServer(port).run();
    }
}