import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class FXMLAuthController {
    private UserNetwork userNetwork;
    private Stage primaryStage;
    private Scene nextScene;
    private FXMLMainController mainController;
    public FXMLAuthController(Stage primaryStage,Scene nextScene,FXMLMainController mainController) {
        this.primaryStage=primaryStage;
        this.nextScene = nextScene;
        this.mainController = mainController;
        //Создаем класс сети с гостевым именем
        userNetwork = new UserNetwork("guest");
        userNetwork.setMainController(mainController);
        mainController.setUserNetwork(userNetwork);
        //Запускаем класс сети (определяем потоки ввода/вывода и коннектим сокет)
        userNetwork.connect();
    }
    @FXML
    private Label loginLabel;
    @FXML
    private Label passLabel;
    @FXML
    private TextField textBox1;
    @FXML
    private TextField textBox2;
    @FXML
    public void button1press(ActionEvent event) {
        //Запускаем процедуру аутентификации
        checkAuth();
    }
    @FXML
    public void button2press(ActionEvent event) {
        //Запускаем окно регистрации
        primaryStage.hide();
        try{
        createRegistrationWindow();
        } catch (IOException e){
            System.out.println("Ошибка при запуске формы регистрации.");
            e.printStackTrace();
        }
    }
    @FXML
    private void clearTextBoxes(){
        textBox1.clear();
        textBox2.clear();
    }
    private void checkAuth(){
        //Отправляем с помощью класса нашей сети пакет, содержащий логин и пароль, и выбираем действие в зависимости от ответа.
        if (userNetwork.sendAuthInfo(new AuthPacket(textBox1.getText(),textBox2.getText(),"log"))){
            System.out.println("Hello!");
            mainController.start();
            primaryStage.setScene(nextScene);
            primaryStage.setTitle("Сетевое хранилище");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Доступ закрыт! Проверьте правильность введенных данных.");
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.showAndWait();
            clearTextBoxes();
        }
    }
    private void createRegistrationWindow() throws IOException {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registrationScene.fxml"));
            fxmlLoader.setController(new FXMLRegistrationController(primaryStage,stage,userNetwork));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Окно регистрации");
            stage.show();
    }
}
