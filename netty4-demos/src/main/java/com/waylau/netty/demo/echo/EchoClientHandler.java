package com.waylau.netty.demo.echo;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

 private final String firstMessage;
 
    /**
      * Creates a client-side handler.
       */
      public EchoClientHandler() {
//         firstMessage = Unpooled.buffer(EchoClient.SIZE);
//         for (int i = 0; i < firstMessage.capacity(); i ++) {
//              firstMessage.writeByte((byte) i);
//         }
////         firstMessage.writeByte('\n');
          //客户端发送的消息
          firstMessage = "hello 坎坎坷坷扩扩扩扩扩扩扩扩扩 坎坎坷坷扩 kkkkkk\n";
      }

      public EchoClientHandler(String Message){
          firstMessage = Message+"\n";
      }

    /**
     * 链接 成功后
     * 通道激活后调用事件
     * @param ctx 通道上下文
     */
    @Override
      public void channelActive(ChannelHandlerContext ctx) {
          ctx.writeAndFlush(firstMessage);
          System.out.println("channel active.");
      }

    /**
     * 客户端 接收到服务端发送到消息 读取消息时调用
     * @param ctx 通道上下文
     * @param msg 消息内容
     */
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
          //打印服务端发来的信息
          System.out.println("channel read from server: " + msg);
         //客户端将信息缓冲到内部 此处写入时 自动释放读取指针 如果没用写入操作 则需要显示释放 读取
          ctx.write(firstMessage + "\n");
      }
  
     @Override
      public void channelReadComplete(ChannelHandlerContext ctx) {
         ctx.flush();//调用刷新 将 ctx.write() 写入到缓冲区的数据 从缓冲区强行输出  使用cxt.writeAndFlush(msg)可以完成同样的操作
         System.out.println("channel read complete");
      }
  
     @Override
     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
          // Close the connection when an exception is raised.
         cause.printStackTrace();
         ctx.close();
      }
  }