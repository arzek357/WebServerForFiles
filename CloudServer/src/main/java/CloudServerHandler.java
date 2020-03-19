import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Files;

public class CloudServerHandler  extends ChannelInboundHandlerAdapter {
    private ObservableList<FilePacket> serverFiles = FXCollections.observableArrayList();
    private String userName;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент отключился...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass().toString());
        if (msg instanceof RequestPacket){
            userName = ((RequestPacket) msg).getUserName();
            System.out.println("К серверу подключился пользователь "+userName);
            checkServerDirectoryAndSendInfo(userName,ctx);
        }
        if (msg instanceof SendPacket){
            for (FilePacket s:serverFiles){
                if (s.getFileName().toString().equals(((SendPacket) msg).getFileName())){
                    ((SendPacket) msg).setFileByteArr(Files.readAllBytes(s.getFile().toPath()));
                }
            }
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
    private void checkServerDirectoryAndSendInfo(String userName,ChannelHandlerContext ctx) {
        File file = new File("CloudServer\\src\\main\\resources\\"+userName);
        if (!file.exists()){
            file.mkdirs();
        }
        else {
            File[] arrFiles = file.listFiles();
            if (arrFiles!=null){
                for (File k:arrFiles){
                    serverFiles.add(new FilePacket(k));
                }
            updateInfoAboutServerDirectory(userName,ctx);
            }
            else{
                return;
            }
        }
    }
    private void updateInfoAboutServerDirectory(String userName,ChannelHandlerContext ctx){
        ctx.writeAndFlush(serverFiles.size());
        for (FilePacket s: serverFiles){
            ctx.writeAndFlush(new RequestPacket(userName,s.getFileName().toString(),s.getFile().toString(),s.getFileLength()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
