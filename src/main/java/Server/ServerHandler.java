package Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static Server.Server.r;

public class ServerHandler extends ChannelInboundHandlerAdapter { //(1)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        System.out.println("MESSAGE READ");//(2)
        Message message = new Message();

        message.initFromByteBuf((ByteBuf) msg);

        if(message.getMsgType()== Message.MsgType.Registration){
            if(!r.auth(message.login, message.password)){
                System.out.println("Registration success");
                r.add(message.login, message.password);
                Message m = new Message();
                m.login=message.login;
                m.password=message.password;
                m.typ= Message.MsgType.LoginSuccess;
                Gson g = new GsonBuilder().create();
                ctx.writeAndFlush((Unpooled.copiedBuffer(g.toJson(m), CharsetUtil.UTF_8)));
            }
        }
        if(message.getMsgType()== Message.MsgType.FileRequest && r.auth(message.login, message.password)){
            System.out.println("Request from client");
            Message m = new Message();
            byte[] b = Files.readAllBytes(Paths.get("saves/"+message.login+"/"+message.filename));
            String s = "";
            for(byte by:b){
                s=s+String.valueOf(by);
            }
            m.file=s;
            m.typ= Message.MsgType.FileUpload;
            m.filename=message.filename;
            Gson g = new GsonBuilder().create();

            ctx.writeAndFlush(((ByteBuf) msg).writeBytes(g.toJson(m).getBytes(StandardCharsets.UTF_8)));

        }
        if (message.typ== Message.MsgType.FileList && r.auth(message.login, message.password)){
            System.out.println("File list request");
            Message m = new Message();
            File f = new File(message.filename);
            Stream<Path> xz = null;
            try {
                xz= Files.list(Paths.get("saves/"));
            } catch (IOException e) {
                try {
                    Files.createDirectory(Paths.get("saves/"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            for(Path p :xz.toList()){
                m.files= m.files+(p.getFileName().toString());
            };
            m.typ= Message.MsgType.FileList;
            Gson g = new GsonBuilder().create();

            ctx.writeAndFlush(((ByteBuf) msg).writeBytes(g.toJson(m).getBytes(StandardCharsets.UTF_8)));
        }

        if(message.typ == Message.MsgType.FileUpload && r.auth(message.login, message.password)){
            System.out.println("File uploading from a client");
            File f = new File("saves/"+message.login+"/"+message.filename);
            if(f.exists()){
                f.createNewFile();
                Files.write(f.toPath(), message.file.getBytes(StandardCharsets.UTF_8));

            }
        }
        if(message.typ == Message.MsgType.FileUpdate && r.auth(message.login, message.password)){
            System.out.println("FileUpdate");
            File f = new File("saves/"+message.login+"/"+message.filename);
            if(f.exists()){
                Files.write(f.toPath(), message.file.getBytes(StandardCharsets.UTF_8));

            }
        }
        if(message.getMsgType()== Message.MsgType.Login){

            if(r.auth(message.login, message.password)){
                System.out.println("Login success");
                Message m = new Message();
                m.login=message.login;
                m.password=message.password;
                m.typ= Message.MsgType.LoginSuccess;
                Gson g = new GsonBuilder().create();
                ctx.write(((ByteBuf) msg).writeBytes(g.toJson(m).getBytes(StandardCharsets.UTF_8)));
                ctx.flush();
            }
        }
        ctx.flush();



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
