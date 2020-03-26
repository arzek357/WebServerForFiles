import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

public class FXMLAuthController {
    private Stage primaryStage;
    private Scene nextScene;
    private Connection connection;
    private Statement statement;
    private FXMLMainController mainController;
    private boolean authStatus = false;
    public FXMLAuthController(Stage primaryStage,Scene nextScene,FXMLMainController mainController) {
        this.primaryStage=primaryStage;
        this.nextScene = nextScene;
        this.mainController = mainController;
        try {
            connectionToDb();
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Невозможно подключиться к базе данных!");
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("Отсутствует драйвер для подключения к базе данных!");
        }
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
        try{
        checkAuth();
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Невозможно подключиться к базе данных!");
        }
    }
    @FXML
    private void clearTextBoxes(){
        textBox1.clear();
        textBox2.clear();
    }
    private void connectionToDb() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:CloudClient\\src\\main\\resources\\auth.db");
        statement=connection.createStatement();
    }
    private void checkAuth() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM ENTER");
        while (rs.next()){
            if (textBox1.getText().equals(rs.getString("LOGIN"))&&textBox2.getText().equals(rs.getString("PASS"))){
                authStatus=true;
                mainController.setUserNetwork(new UserNetwork(rs.getString("LOGIN")));
            }
        }
        if (authStatus){
            System.out.println("Hello!");
            mainController.startConnection();
            primaryStage.setScene(nextScene);
            primaryStage.setTitle("Сетевое хранилище");
            disconnectFromDB();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Доступ закрыт! Проверьте правильность введенных данных.");
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.showAndWait();
            clearTextBoxes();
        }
    }
    private void disconnectFromDB() throws SQLException {
        connection.close();
    }
}
