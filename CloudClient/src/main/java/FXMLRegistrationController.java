import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FXMLRegistrationController {
    private UserNetwork userNetwork;
    private Stage primaryStage;
    private Stage registrationStage;
    public FXMLRegistrationController(Stage primaryStage,Stage registrationStage,UserNetwork userNetwork){
        this.primaryStage=primaryStage;
        this.registrationStage=registrationStage;
        this.userNetwork=userNetwork;
    }
    @FXML
    private TextField textBox1;
    @FXML
    private TextField textBox2;
    @FXML
    public void pressRegButton(ActionEvent event) {
    registrationRequest(textBox1.getText(),textBox2.getText());
    }
    @FXML
    public void pressExitButton(ActionEvent event) {
    registrationStage.close();
    primaryStage.show();
    }
    @FXML
    private void clearTextBoxes(){
        textBox1.clear();
        textBox2.clear();
    }
    private void registrationRequest(String login,String pass){
        Pattern pattern = Pattern.compile("["+"a-zA-Z"+"\\d"+"]" +"*");
        Matcher loginMatcher = pattern.matcher(login);
        Matcher passMatcher = pattern.matcher(pass);
        if (loginMatcher.matches()&&passMatcher.matches()&&pass.length()>=5){
            if (userNetwork.sendAuthInfo(new AuthPacket(login,pass,"reg"))){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Регистрация прошла успешно!");
                alert.setTitle("Уведолмение");
                alert.setHeaderText(null);
                alert.showAndWait();
                registrationStage.close();
                primaryStage.show();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"Пользователь с таким логином уже зарегистрирован. Попробуйте снова.");
                alert.setTitle("Уведомление");
                alert.setHeaderText(null);
                alert.showAndWait();
                clearTextBoxes();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING,"Ошибка регистрации! Логин и пароль должны состоять из букв латинского алфавита и цифр. Пароль должен иметь длину не менее 5 символов.");
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.showAndWait();
            clearTextBoxes();
        }
    }
}
