
import java.sql.*;
import java.util.ArrayList;

public class BDWorker {
    private static Connection connection;
    private static Statement statement;
    private static void connectionToDb() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:CloudServer\\src\\main\\resources\\auth.db");
        statement=connection.createStatement();
    }
    static boolean checkAuth(String login,String pass) throws SQLException, ClassNotFoundException {
        connectionToDb();
        ResultSet rs = statement.executeQuery("SELECT * FROM ENTER");
        while (rs.next()) {
            if (login.equals(rs.getString("LOGIN")) && pass.equals(rs.getString("PASS"))) {
                disconnectFromDB();
                return true;
            }
        }
        disconnectFromDB();
        return false;
    }
    static boolean regRequest(String login,String password) throws SQLException, ClassNotFoundException {
        connectionToDb();
        ArrayList<String> bdArray = new ArrayList<>();
        ResultSet rs = statement.executeQuery("SELECT LOGIN FROM ENTER");
        while (rs.next()){
            bdArray.add(rs.getString(1));
        }
        disconnectFromDB();
        if (!bdArray.contains(login)){
            connectionToDb();
            statement.executeUpdate("INSERT INTO ENTER (LOGIN,PASS) VALUES ('"+login+"','"+password+"')");
            disconnectFromDB();
            return true;
        }
        return false;
    }
        private static void disconnectFromDB() throws SQLException {
            connection.close();
        }
}
