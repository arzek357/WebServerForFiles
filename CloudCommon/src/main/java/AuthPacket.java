import java.io.Serializable;

public class AuthPacket implements Serializable {
    private String userName;
    private String pass;
    private String actionCode;
    public AuthPacket(String userName,String pass,String actionCode){
        this.pass=pass;
        this.userName=userName;
        this.actionCode = actionCode;
    }
    public String getUserName() {
        return userName;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getActionCode() {
        return actionCode;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
