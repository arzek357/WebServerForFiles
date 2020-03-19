import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;


import java.io.File;
import java.io.IOException;
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
    public void pressDownloadButton(ActionEvent event) throws IOException {
        FilePacket fileToDownload = serverFilesTable.getSelectionModel().getSelectedItem();
        userNetwork.downloadFileFromServer(fileToDownload);
    }

    public void setUserNetwork(UserNetwork userNetwork){
        this.userNetwork = userNetwork;
    }
    public void startConnection() {
        checkLocalDirectory();
        userNetwork.connect();
        userNetwork.setController(this);
        userNetwork.setServerFiles(serverFiles);
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
                    clientFiles.add(new FilePacket(k));
                }
                initListInLocalTableView();
            }
            else{
                return;
            }
        }
    }
    private void initListInLocalTableView(){
        localFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        localFileLength.setCellValueFactory(new PropertyValueFactory<>("fileLength"));
        localFilesTable.setItems(clientFiles);
    }
    public void initListInServerTableView(ObservableList<FilePacket> serverStorage){
        serverFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        serverFileLength.setCellValueFactory(new PropertyValueFactory<>("fileLength"));
        serverFilesTable.setItems(serverStorage);
    }

    public UserNetwork getUserNetwork() {
        return userNetwork;
    }
}
