import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    public void pressRegButton(ActionEvent event) {
    registrationRequest(textField1.getText(), textField2.getText());
    }
    @FXML
    public void pressExitButton(ActionEvent event) {
    registrationStage.close();
    primaryStage.show();
    }
    @FXML
    private void clearTextBoxes(){
        textField1.clear();
        textField2.clear();
    }
    //Метод, проверяющий правильность предложенных данных для регистрации и формирующий запрос на регистрацию через класс UserNetwork
    private void registrationRequest(String login,String pass){
        Pattern pattern = Pattern.compile("["+"a-zA-Z"+"\\d"+"]" +"*");
        Matcher loginMatcher = pattern.matcher(login);
        Matcher passMatcher = pattern.matcher(pass);
        if (loginMatcher.matches()&&passMatcher.matches()&&pass.length()>=5){
            //Если все условия к данным для регистрации соблюдены, то формируем запрос
            if (userNetwork.sendAuthInfo(new AuthPacket(login,pass,"reg"))){
                //Получили подтверждение сервера о регистрации, уведомляем пользователя
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Регистрация прошла успешно!");
                alert.setTitle("Уведомление");
                alert.setHeaderText(null);
                alert.showAndWait();
                registrationStage.close();
                primaryStage.show();
            }
            else{
                //Получили ответ от сервера, но подтверждения не было, значит такой пользователь уже есть
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"Пользователь с таким логином уже зарегистрирован. Попробуйте снова.");
                alert.setTitle("Уведомление");
                alert.setHeaderText(null);
                alert.showAndWait();
                clearTextBoxes();
            }
        }
        else {
            //Данные для регистрации не подошли под условия
            Alert alert = new Alert(Alert.AlertType.WARNING,"Ошибка регистрации! Логин и пароль должны состоять из букв латинского алфавита и цифр. Пароль должен иметь длину не менее 5 символов.");
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.showAndWait();
            clearTextBoxes();
        }
    }
}
