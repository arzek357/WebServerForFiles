
import java.io.File;
import java.io.Serializable;

public class RequestPacket implements Serializable {
    private String userName;
    private File[] files;
    public RequestPacket(String userName){
        this.userName = userName;
    }
    public RequestPacket(String userName,File[] files){
        this.userName = userName;
        this.files = files;
    }

    public File[] getFiles() {
        return files;
    }
    public String getUserName() {
        return userName;
    }
}
