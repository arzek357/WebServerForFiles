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

    @Override
    public void stop() throws Exception {
        fxmlMainController.getUserNetwork().disconnect();
    }

    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("mainScene.fxml"));
        fxmlMainController = new FXMLMainController();
        fxmlLoader2.setController(fxmlMainController);
        Parent root2 = fxmlLoader2.load();
        Scene scene2 = new Scene(root2);

        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("authScene.fxml"));
        FXMLAuthController fxmlAuthController = new FXMLAuthController(primaryStage,scene2,fxmlMainController);
        fxmlLoader1.setController(fxmlAuthController);
        Parent root1 = fxmlLoader1.load();
        Scene scene1 = new Scene(root1);

        primaryStage.setScene(scene1);
        primaryStage.setTitle("Окно авторизации");
        primaryStage.show();

    }
}
