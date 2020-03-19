import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UserNetwork {
    private final String userName;
    private FXMLMainController controller;
    private Socket socket;
    private ObjectEncoderOutputStream oeos;
    private ObjectDecoderInputStream odis;
    private ObservableList<FilePacket> serverFiles;

    public UserNetwork(String userName){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void connect() {
            try{
                socket = new Socket("localhost",8189);
                oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
                odis = new ObjectDecoderInputStream(socket.getInputStream(),100 * 1024 * 1024);
                startSynchronizeThread();
                RequestPacket userInfoPacket = new RequestPacket(userName);
                oeos.writeObject(userInfoPacket);
                oeos.flush();
 //               startReadingThread();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Невозможно подключиться к серверу!");
            }
    }
    private void startSynchronizeThread() throws IOException {
        Thread syncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    int number = (int) odis.readObject();
                    while (serverFiles.size()<number){
                        RequestPacket obj= (RequestPacket) odis.readObject();
                        serverFiles.add(new FilePacket(obj.getFileName(),obj.getFileLength(),obj.getPathOnServer()));
                    }
                    controller.initListInServerTableView(serverFiles);
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        });
        syncThread.start();
    }
    private void startReadingThread() throws InterruptedException {
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Object msg = odis.readObject();
                        if (msg instanceof SendPacket){
                            Files.write(Paths.get("CloudClient\\src\\main\\resources\\" + userName),((SendPacket) msg).getFileByteArr());
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        readThread.join();
        readThread.setDaemon(true);
        readThread.start();
    }
    public void setController(FXMLMainController controller) {
        this.controller = controller;
    }
    public void setServerFiles(ObservableList<FilePacket> serverFiles) {
        this.serverFiles = serverFiles;
    }
    public void disconnect(){
        disconnect(socket,oeos,odis);
    }
    private void disconnect(Socket socket,ObjectEncoderOutputStream oeos,ObjectDecoderInputStream odis) {
        try{
            oeos.close();
            odis.close();
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("При отключении от сервера произошла ошибка.");
        }
    }
    void downloadFileFromServer(FilePacket file) throws IOException {
        oeos.writeObject(new SendPacket(file.getFileName().toString()));
        oeos.flush();
    }
}
