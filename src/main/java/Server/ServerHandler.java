package Server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Server.Server.r;

public class ServerHandler extends ChannelInboundHandlerAdapter { //(1)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {     //(2)
        Message message = new Message();
        message.initFromByteBuf((ByteBuf) msg);

        if(message.getMsgType()== Message.MsgType.Registration){
            if(!r.auth(message.login, message.password)){
                r.add(message.login, message.password);
            }
        }
        if(message.getMsgType()== Message.MsgType.FileRequest){
            Message m = new Message();
            File f = new File(message.filename);
            byte[] b = Files.readAllBytes(Paths.get(message.filename));
            String s = "";
            for(byte by:b){
                s=s+String.valueOf(by);
            }

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
