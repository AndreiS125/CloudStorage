package Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Message {
    String file;
    String filename;
    String login;
    String password;
    MsgType typ;
    String files="";

    public enum MsgType{
        FileUpload, FileRequest, Registration, FileList, FileUpdate, FileDelete, Login, LoginSuccess
    }
    public void initFromByteBuf(ByteBuf buf){
        Gson g = new GsonBuilder().setLenient().create();
        String b = "";
        String c= "";
        byte bute[]= {2};
        for (int i = 0; i < buf.capacity(); i++) {
            bute[0] = buf.getByte(i);


            b=b+ new String(bute);
        }
        for (int i = 0; i <= b.indexOf("}"); i++) {
            bute[0] = b.getBytes(StandardCharsets.UTF_8)[i];

            c=c+ new String(bute);
        }




        System.out.println(c);

        Message m = g.fromJson(c, Message.class);
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
