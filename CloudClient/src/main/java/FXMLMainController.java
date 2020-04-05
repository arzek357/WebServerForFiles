import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FXMLMainController {
    private ObservableList<FilePacket> clientFiles = FXCollections.observableArrayList();
    private ObservableList<FilePacket> serverFiles = FXCollections.observableArrayList();
    private UserNetwork userNetwork;

    @FXML
    private TableView<FilePacket> localFilesTable;
    @FXML
    private TableColumn<FilePacket, Path> localFileName;
    @FXML
    private TableColumn<FilePacket,Long> localFileLength;
    @FXML
    private TableView<FilePacket> serverFilesTable;
    @FXML
    private TableColumn<FilePacket, Path> serverFileName;
    @FXML
    private TableColumn<FilePacket,Long> serverFileLength;

    @FXML
    public void pressSendButton(ActionEvent event) {
        FilePacket fileToSend = localFilesTable.getSelectionModel().getSelectedItem();
        userNetwork.sendFileToServer(fileToSend);
        pressServerRefreshButton();
    }
    @FXML
    public void pressLocalDeleteButton(ActionEvent event) throws IOException {
        FilePacket fileToDownload = localFilesTable.getSelectionModel().getSelectedItem();
        Files.delete(fileToDownload.getFile().toPath());
        clientFiles.remove(fileToDownload);
        pressLocalRefreshButton();
    }
    @FXML
    public void pressLocalRefreshButton(ActionEvent event) {
        pressLocalRefreshButton();
    }
    private void pressLocalRefreshButton(){
        localFilesTable.getItems().removeAll(clientFiles);
        checkLocalDirectory();
        initListInLocalTableView();
    }
    @FXML
    public void pressDownloadButton(ActionEvent event){
        FilePacket fileToDownload = serverFilesTable.getSelectionModel().getSelectedItem();
        userNetwork.downloadFileFromServer(fileToDownload);
    }
    @FXML
    public void pressServerDeleteButton(ActionEvent event) {
        FilePacket fileToDelete = serverFilesTable.getSelectionModel().getSelectedItem();
        userNetwork.deleteFileFromServer(fileToDelete);
        serverFiles.remove(fileToDelete);
        initListInServerTableView();
    }
    @FXML
    public void pressServerRefreshButton(ActionEvent event) {
    pressServerRefreshButton();
    }
    private void pressServerRefreshButton(){
        userNetwork.refreshFilesFromServer();
    }
    //________________________________________________________//
    //Метод, вкючающий в себя стандартный набор методов при переходе пользователя на основное окно приложения
    void start() {
        userNetwork.sendWelcomeMessage();
        checkLocalDirectory();
        initListInLocalTableView();
    }
    //Метод, проверяющий файлы в локальном хранилище и заполняющий ими коллекцию, при отсутствии хранилища пользователя, создает его
    private void checkLocalDirectory() {
        File file = new File("CloudClient\\src\\main\\resources\\"+userNetwork.getUserName());
        if (!file.exists()){
            file.mkdirs();
        }
        else {
            File[] arrFiles = file.listFiles();
            if (arrFiles!=null){
                for (File k:arrFiles){
                    FilePacket ks = new FilePacket(k);
                    if(!clientFiles.contains(ks)){
                    clientFiles.add(ks);
                    }
                }
            }
            else{
                return;
            }
        }
    }
    //Метод, заполняющий таблицу значениями из коллекции, принадлежащей файлам на клиентской стороне
    void initListInLocalTableView(){
        localFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        localFileLength.setCellValueFactory(new PropertyValueFactory<>("fileLength"));
        localFilesTable.setItems(clientFiles);
    }
    //Метод, заполняющий таблицу значениями из коллекции, принадлежащей файлам на серверной стороне
    void initListInServerTableView(){
        serverFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        serverFileLength.setCellValueFactory(new PropertyValueFactory<>("fileLength"));
        serverFilesTable.setItems(serverFiles);
    }

    UserNetwork getUserNetwork() {
        return userNetwork;
    }

    ObservableList<FilePacket> getClientFiles() {
        return clientFiles;
    }

    ObservableList<FilePacket> getServerFiles() {
        return serverFiles;
    }
    void setUserNetwork(UserNetwork userNetwork){
        this.userNetwork = userNetwork;
    }

    public TableView<FilePacket> getServerFilesTable() {
        return serverFilesTable;
    }
}
