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
    public void pressSendButton(ActionEvent event) throws IOException {
        FilePacket fileToSend = localFilesTable.getSelectionModel().getSelectedItem();
        serverFiles.add(fileToSend);
        userNetwork.sendFileToServer(fileToSend);
        pressLocalRefreshButton();
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
        pressServerRefreshButton();
    }
    @FXML
    public void pressServerRefreshButton(ActionEvent event) throws IOException {
    pressServerRefreshButton();
    }
    private void pressServerRefreshButton(){
        serverFilesTable.getItems().removeAll(serverFiles);
        userNetwork.refreshFilesFromServer();
    }
    //________________________________________________________//
    void startConnection() {
        checkLocalDirectory();
        initListInLocalTableView();
        userNetwork.setController(this);
        userNetwork.connect();
    }
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
    void initListInLocalTableView(){
        localFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        localFileLength.setCellValueFactory(new PropertyValueFactory<>("fileLength"));
        localFilesTable.setItems(clientFiles);
    }
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
}
