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
    //Метод, в котором формируется сеть для клиента
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
    //Метод, в котором формируется клиентский поток чтения файлов от сервера
    private void startReadingThread(){
        Thread readThread = new Thread(() -> {
            try {
                while (true) {
                    Object msg = odis.readObject();
                    if (msg instanceof SendPacket) {
                        File writeFile = CopyFileModule.checkFileAndBackUniName(new File("CloudClient\\src\\main\\resources\\" + userName + "\\" + ((SendPacket) msg).getFileName()));
                        Files.write(writeFile.toPath(), ((SendPacket) msg).getFileByteArr());
                        mainController.getClientFiles().add(new FilePacket(writeFile));
                        mainController.initListInLocalTableView();
                    }
                    if (msg instanceof RequestPacket){
                        mainController.getServerFilesTable().getItems().removeAll(mainController.getServerFiles());
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
    //Метод для вызова disconnect() из классов, не имеющих доступа к элементам сети
    void disconnect(){
       disconnect(socket,oeos,odis);
        System.out.println("Приложение закончило свою работу. Сетевое соединение закрыто.");
    }
    //Метод для идентификации пользователя на сервере, отправляет пакет RequestPacket, в ответ получает информацию о файлах из серверного хранилища
    void sendWelcomeMessage(){
        try{
        RequestPacket userInfoPacket = new RequestPacket(userName);
        oeos.writeObject(userInfoPacket);
        oeos.flush();
        } catch (IOException e){
            System.out.println("Ошибка при отправке идентифицирующего пакета.");
            e.printStackTrace();
        }
    }
    //Метод для правильного завершения работы сети
    private void disconnect(Socket socket,ObjectEncoderOutputStream oeos,ObjectDecoderInputStream odis) {
        try{
            oeos.close();
            odis.close();
            socket.close();
        } catch (IOException e){
            System.out.println("При завершении работы сети произошла ошибка.");
            e.printStackTrace();
        }
    }
    //Метод для загрузки файла с серверного хранилища с помощью пакета SendPacket
    void downloadFileFromServer(FilePacket file) {
        try {
            oeos.writeObject(new SendPacket(file.getFileName().toString(),"download"));
            oeos.flush();
        } catch (IOException e){
            System.out.println("Произашла ошибка при поптыке скачивания файла "+file.getFileName().toString()+" с сервера!");
            e.printStackTrace();
        }
    }
    //Метод, запршивающий информацию о файлах, которые находятся в хранилище сервера с помощью пакета RequestPacket
    void refreshFilesFromServer() {
        try{
        oeos.writeObject(new RequestPacket(userName));
        oeos.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при попытке обновить данные сервера!");
            e.printStackTrace();
        }
    }
    //Метод, отправляющий команду удаления указанного файла на сервер c помощью пакета InstructionForServer
    void deleteFileFromServer(FilePacket file){
        try {
            oeos.writeObject(new InstructionForServer(file.getFileName().toString(),"delete"));
            oeos.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при попытке удаления файла "+file.getFileName().toString());
            e.printStackTrace();
        }
    }
    //Метод, отправляющий файл на сервер с помощью пакета SendPacket.
    void sendFileToServer(FilePacket file){
        try {
            oeos.writeObject(new SendPacket(file.getFileName().toString(),Files.readAllBytes(file.getFile().toPath()),"send"));
            oeos.flush();
        } catch (IOException e){
            System.out.println("Произошла ошибка при отправке файла "+file.getFileName().toString());
            e.printStackTrace();
        }
    }
    //Метод, отправляющий серверу информацию о пользователе, который проходит аутентификацию или пытается зарегистрироваться.
    boolean sendAuthInfo(AuthPacket authPacket){
        try {
            oeos.writeObject(authPacket);
            oeos.flush();
            AuthPacket msg = (AuthPacket) odis.readObject();
            //Если аутентификация прошла успешно, то сервер отправит назад этот же пакет, но изменит значение пароля на "true"
            if (msg.getPass().equals("true")){
                userName = msg.getUserName();
                startReadingThread();
                return true;
            }
            //Если регистрация прошла успешно, то сервер отправит назад этот же пакет, но изменит значение логина на "true"
            if (msg.getUserName().equals("true")){
                return true;
            }
        } catch (IOException e){
            System.out.println("Произошла ошибка во время отправки аутентификационных данных!");
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            System.out.println("Непонятный тип сообщения от сервера.");
            e.printStackTrace();
        }
        return false;
    }
}
