import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;


public class UserNetwork {
    private String userName;
    private FXMLMainController mainController;
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
    }
    private void determineSocketAndStreams(){
        try {
            socket = new Socket("localhost", 8189);
            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            odis = new ObjectDecoderInputStream(socket.getInputStream(), 100 * 1024 * 1024);
            System.out.println("Подключение к серверу произошло успешно. Сетевое соединение открыто.");
        } catch (IOException e){
            System.out.println("Невозможно подключиться к серверу!");
        }
    }
    private void startReadingThread(){
        Thread readThread = new Thread(() -> {
            try {
                while (true) {
                    Object msg = odis.readObject();
                    if (msg instanceof SendPacket) {
                        File clientFile = new File("CloudClient\\src\\main\\resources\\" + userName + "\\" + ((SendPacket) msg).getFileName());
                        Files.write(clientFile.toPath(), ((SendPacket) msg).getFileByteArr());
                        mainController.getClientFiles().add(new FilePacket(clientFile));
                        mainController.initListInLocalTableView();
                    }
                    if (msg instanceof RequestPacket){
                        mainController.getServerFiles().removeAll();
                        for (File s:((RequestPacket) msg).getFiles()){
                            mainController.getServerFiles().add(new FilePacket(s));
                        }
                        mainController.initListInServerTableView();
                    }
                }
            } catch (IOException e){
                System.out.println("Поток чтения прекратил свою работу в связи с разрывом соединения");
            } catch (ClassNotFoundException e){
                System.out.println("Ошибка чтения данных сервера");
                e.printStackTrace();
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
    void setMainController(FXMLMainController mainController) {
        this.mainController = mainController;
    }
    void disconnect(){
       disconnect(socket,oeos,odis);
        System.out.println("Приложение закончило свою работу. Сетевое соединение закрыто.");
    }

    void sendWelcomeMessage(){
        try{
        RequestPacket userInfoPacket = new RequestPacket(userName);
        oeos.writeObject(userInfoPacket);
        oeos.flush();
        } catch (IOException e){
            System.out.println("Ошибка при отправке идентифицирующего пакета.");
        }
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
    boolean sendAuthInfo(AuthPacket authPacket){
        try {
            oeos.writeObject(authPacket);
            oeos.flush();
            AuthPacket msg = (AuthPacket) odis.readObject();
            if (msg.getPass().equals("true")){
                userName = msg.getUserName();
                startReadingThread();
                return true;
            }
            if (msg.getUserName().equals("true")){
                return true;
            }
        } catch (IOException e){
            System.out.println("Произошла ошибка во время отправки аутентификационных данных!");
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }
}
