package Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Message {
    String file;
    String filename;
    String login;
    String password;
    MsgType typ;
    public enum MsgType{
        FileUpload, FileRequest, Registration, FileList
    }
    public void initFromByteBuf(ByteBuf buf){
        Gson g = new GsonBuilder().create();
        String b = "";
        for (int i = 0; i < buf.capacity(); i++) {
            byte bute = buf.getByte(i);
            b=b+String.valueOf(bute);
        }
        Message m = g.fromJson(b, Message.class);
        file = m.file;
        filename = m.filename;
        login = m.login;
        password = m.password;
        typ = m.typ;
    }

    public MsgType getMsgType(){
        return this.typ;
    }

    public ByteBuf fillByteBuff(ByteBuf b){
        Gson g = new GsonBuilder().create();
        ByteBuf bs = b;
        bs.writeBytes(g.toJson(this).getBytes(StandardCharsets.UTF_8));
        return b;


    }
}
