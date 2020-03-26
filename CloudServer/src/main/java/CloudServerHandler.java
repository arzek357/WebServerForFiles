import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Files;

public class CloudServerHandler  extends ChannelInboundHandlerAdapter {
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
            checkServerDirectoryAndSendInfo(userName,ctx);
        }
        if (msg instanceof SendPacket){
            if(((SendPacket) msg).getFileByteArr()==null){
                File potentialFile = new File("CloudServer\\src\\main\\resources\\"+userName+"\\"+((SendPacket) msg).getFileName());
                if (potentialFile.exists()){
                    ((SendPacket) msg).setFileByteArr(Files.readAllBytes(potentialFile.toPath()));
                    ctx.writeAndFlush(msg);
                }
            }
            else {
                File potentialFile = new File("CloudServer\\src\\main\\resources\\" + userName + "\\" + ((SendPacket) msg).getFileName());
                Files.write(potentialFile.toPath(), ((SendPacket) msg).getFileByteArr());
            }
        }
        if (msg instanceof InstructionForServer){
            File potentialFile = new File("CloudServer\\src\\main\\resources\\" + userName + "\\"+((InstructionForServer) msg).getFileName());
            if (((InstructionForServer) msg).getActionCode().equals("delete")){
                potentialFile.delete();
            }
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
            ctx.writeAndFlush(new RequestPacket(userName,arrFiles));
            }
            else{
                return;
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
