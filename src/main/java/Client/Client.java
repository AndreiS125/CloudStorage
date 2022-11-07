package Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

public class Client {
    private int port;
    public Channel chanell;
    public ByteBuf c;
    public Client(int port) {
        this.port = port;

    }


    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1); // (1)

        try {
            Bootstrap b = new Bootstrap(); // (2)
            b.group(group)
                    .channel(NioSocketChannel.class) // (3)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) {     //(2)
                                    ByteBuf in = (ByteBuf) msg;
                                    System.out.println("Message read");
                                    ctx.writeAndFlush(msg);
                                    c=ctx.alloc().buffer();

                                    try {
                                        while (in.isReadable()) {        // (1)
                                            System.out.print((char) in.readByte());
                                            System.out.flush();
                                        }
                                    } catch (Exception ex){
                                        ex.printStackTrace();
                                        ReferenceCountUtil.release(msg); // (2)
                                    }


                                }
                                @Override
                                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                    ctx.fireChannelRegistered();
                                    chanell=ch;
                                    c=ctx.alloc().buffer();
                                    c.writeBytes("Hello".getBytes(StandardCharsets.UTF_8));


                                    ctx.writeAndFlush("HELLO");
                                    ctx.channel().writeAndFlush("hello");
                                    System.out.println("Channel registered");

                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
                                    ByteBuf msg = ctx.alloc().buffer();
                                    msg.writeBytes("Hello".getBytes(StandardCharsets.UTF_8));
                                    ch.writeAndFlush(msg).sync().channel().writeAndFlush(msg);
                                    ctx.writeAndFlush("HELLO");
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });
                           // (5)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.connect("localhost", port).sync(); // (7)

            c.writeBytes("Hello".getBytes(StandardCharsets.UTF_8));
            chanell.writeAndFlush(c).sync().channel().writeAndFlush(c);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
            f = b.connect("localhost", port).sync(); // (7)

            //f.channel().writeAndFlush(c);
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new Client(port).run();
    }
}
