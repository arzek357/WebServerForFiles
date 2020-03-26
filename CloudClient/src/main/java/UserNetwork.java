import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;


public class UserNetwork {
    private final String userName;
    private FXMLMainController controller;
    private Socket socket;
    private ObjectEncoderOutputStream oeos;
    private ObjectDecoderInputStream odis;

    public UserNetwork(String userName){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void connect() {
        determineSocketAndStreams();
        startReadingThread();
    }
    private void determineSocketAndStreams(){
        try {
            socket = new Socket("localhost", 8189);
            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            odis = new ObjectDecoderInputStream(socket.getInputStream(), 100 * 1024 * 1024);
        } catch (IOException e){
            System.out.println("Невозможно подключиться к серверу!");
        }
    }
    private void startReadingThread(){
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestPacket userInfoPacket = new RequestPacket(userName);
                    oeos.writeObject(userInfoPacket);
                    oeos.flush();
                    while (true) {
                        Object msg = odis.readObject();
                        if (msg instanceof SendPacket) {
                            File clientFile = new File("CloudClient\\src\\main\\resources\\" + userName + "\\" + ((SendPacket) msg).getFileName());
                            Files.write(clientFile.toPath(), ((SendPacket) msg).getFileByteArr());
                            controller.getClientFiles().add(new FilePacket(clientFile));
                            controller.initListInLocalTableView();
                        }
                        if (msg instanceof RequestPacket){
                            controller.getServerFiles().removeAll();
                            for (File s:((RequestPacket) msg).getFiles()){
                                controller.getServerFiles().add(new FilePacket(s));
                            }
                            controller.initListInServerTableView();
                        }
                    }
                } catch (IOException e){
                    System.out.println("Поток чтения прекратил свою работу в связи с разрывом соединения");
                } catch (ClassNotFoundException e){
                    System.out.println("Ошибка чтения данных сервера");
                    e.printStackTrace();
                }
            }
        });
        try {
            readThread.join();
        } catch (InterruptedException e){
            System.out.println("Поток чтения был прерван.");
            e.printStackTrace();
        }
        readThread.setDaemon(true);
        readThread.start();
    }
    void setController(FXMLMainController controller) {
        this.controller = controller;
    }
    void disconnect(){
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
    void downloadFileFromServer(FilePacket file) {
        try {
            oeos.writeObject(new SendPacket(file.getFileName().toString()));
            oeos.flush();
        } catch (IOException e){
            System.out.println("Произашла ошибка при поптыке скачивания файла "+file.getFileName().toString()+" с сервера!");
            e.printStackTrace();
        }
    }
    void refreshFilesFromServer() {
        try{
        oeos.writeObject(new RequestPacket(userName));
        oeos.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при попытке обновить данные сервера!");
            e.printStackTrace();
        }
    }
    void deleteFileFromServer(FilePacket file){
        try {
            oeos.writeObject(new InstructionForServer(file.getFileName().toString(),"delete"));
            oeos.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при попытке удаления файла "+file.getFileName().toString());
            e.printStackTrace();
        }
    }
    void sendFileToServer(FilePacket file){
        try {
            oeos.writeObject(new SendPacket(file.getFileName().toString(),Files.readAllBytes(file.getFile().toPath())));
            oeos.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при отправке файла "+file.getFileName().toString());
            e.printStackTrace();
        }
    }
}
