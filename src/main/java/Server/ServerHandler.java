package Server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter { //(1)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {     //(2)
        ByteBuf in = (ByteBuf) msg;
        System.out.println("MSG READ: " + msg.toString());
        ctx.writeAndFlush(msg);
        ctx.flush();
        try {
            while (in.isReadable()) {        // (1)
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HIIII");
        ctx.fireChannelRegistered();
    }
}
