import java.io.Serializable;

public class AuthPacket implements Serializable {
    private String userName;
    private String pass;
    private String functionOfPacket;
    public AuthPacket(String userName,String pass,String functionOfPacket){
        this.pass=pass;
        this.userName=userName;
        this.functionOfPacket = functionOfPacket;
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
    public String getFunctionOfPacket() {
        return functionOfPacket;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
