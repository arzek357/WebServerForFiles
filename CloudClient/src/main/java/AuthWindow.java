import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AuthWindow extends Application {
    private FXMLMainController fxmlMainController;
    public static void main(String[] args) {
        launch(args);
    }

    //Переопределяем метод, в условии которого отключаемся от сети при закрытии приложения
    @Override
    public void stop() {
        fxmlMainController.getUserNetwork().disconnect();
    }

    public void start(Stage primaryStage) throws Exception {
        //Создаем сцену и подгружаем FXML для основного окна
        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("mainScene.fxml"));
        fxmlMainController = new FXMLMainController();
        fxmlLoader2.setController(fxmlMainController);
        Parent root2 = fxmlLoader2.load();
        Scene scene2 = new Scene(root2);
        //Создаем сцену и подгружаем FXML для окна авторизации
        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("authScene.fxml"));
        fxmlLoader1.setController(new FXMLAuthController(primaryStage,scene2,fxmlMainController));
        Parent root1 = fxmlLoader1.load();
        Scene scene1 = new Scene(root1);
        //Ставим окно аутентификации при запуске приложения
        primaryStage.setScene(scene1);
        primaryStage.setTitle("Окно авторизации");
        primaryStage.show();
    }
}
