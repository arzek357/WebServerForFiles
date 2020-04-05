import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CloudServerHandler  extends ChannelInboundHandlerAdapter {
    private String userName;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Клиент подключился...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Клиент отключился...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        System.out.println("Был получен пакет типа "+msg.getClass().toString());
        if (msg instanceof RequestPacket){
            userName = ((RequestPacket) msg).getUserName();
            checkServerDirectoryAndSendInfo(userName,ctx);
        }
        if (msg instanceof SendPacket){
            File potentialFile = new File("CloudServer\\src\\main\\resources\\"+userName+"\\"+((SendPacket) msg).getFileName());
            switch (((SendPacket) msg).getActionCode()){
                case "download":
                    if (potentialFile.exists()){
                        ((SendPacket) msg).setFileByteArr(Files.readAllBytes(potentialFile.toPath()));
                        ctx.writeAndFlush(msg);
                    }
                    break;
                case "send":
                    File writeFile = CopyFileModule.checkFileAndBackUniName(potentialFile);
                    Files.write(writeFile.toPath(), ((SendPacket) msg).getFileByteArr());
                    break;
            }
        }
        if (msg instanceof InstructionForServer){
            File potentialFile = new File("CloudServer\\src\\main\\resources\\" + userName + "\\"+((InstructionForServer) msg).getFileName());
            switch (((InstructionForServer) msg).getActionCode()){
                case "delete":
                potentialFile.delete();
                break;
            }
        }
        if (msg instanceof AuthPacket){
            switch (((AuthPacket) msg).getActionCode()){
                case ("log"):
                    if(BDWorker.checkAuth(((AuthPacket) msg).getUserName(),((AuthPacket) msg).getPass())){
                        ((AuthPacket) msg).setPass("true");
                    }
                    break;
                case ("reg"):
                    if(BDWorker.regRequest(((AuthPacket) msg).getUserName(),((AuthPacket) msg).getPass())){
                        ((AuthPacket) msg).setUserName("true");
                    }
                    break;
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
