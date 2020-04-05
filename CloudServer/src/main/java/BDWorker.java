import java.sql.*;
import java.util.ArrayList;

public class BDWorker {
    private static Connection connection;
    private static Statement statement;
    //Метод для подключения к БД
    private static void connectionToDb(){
        try{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:CloudServer\\src\\main\\resources\\auth.db");
        statement=connection.createStatement();
        } catch (SQLException e){
            System.out.println("При подключении к БД произошла ошибка! Проверьте правильность пути к БД.");
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            System.out.println("Ошибка подключения драйвера для БД!");
            e.printStackTrace();
        }
    }
    //Метод,проверяющий наличие данной пары логин/пароль в БД
    static boolean checkAuth(String login,String pass){
        connectionToDb();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM ENTER");
            while (rs.next()) {
                if (login.equals(rs.getString("LOGIN")) && pass.equals(rs.getString("PASS"))) {
                    disconnectFromDB();
                    return true;
                }
            }
        } catch (SQLException e){
            System.out.println("Ошибка при работе с БД во время попытки аутентификации!");
            e.printStackTrace();
        }
        disconnectFromDB();
        return false;
    }
    //Метод, проверяющий наличие данного логина в БД, и в случае его отсутствия, создает новую строку с этим логином и переданным паролем
    static boolean regRequest(String login,String password) {
        connectionToDb();
        ArrayList<String> bdArray = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT LOGIN FROM ENTER");
            while (rs.next()) {
                bdArray.add(rs.getString(1));
            }
            disconnectFromDB();
            if (!bdArray.contains(login)) {
                connectionToDb();
                statement.executeUpdate("INSERT INTO ENTER (LOGIN,PASS) VALUES ('" + login + "','" + password + "')");
                disconnectFromDB();
                return true;
            }
        } catch (SQLException e){
            System.out.println("Ошибка при работе с БД во время попытки регистрации!");
            e.printStackTrace();
        }
        return false;
    }
    //Метод для отключения от БД
        private static void disconnectFromDB(){
        try{
            connection.close();
        } catch (SQLException e){
            System.out.println("Ошибка при отключении т БД!");
            e.printStackTrace();
        }
    }
}
