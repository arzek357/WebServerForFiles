
import java.io.Serializable;

public class RequestPacket implements Serializable {
    private String userName;
    private String fileName;
    private String pathOnServer;
    private long fileLength;
    public RequestPacket(String userName){
        this.userName = userName;
        this.fileName = null;
        this.pathOnServer = null;
        this.fileLength = 0;
    }
    public RequestPacket(String userName, String fileName, String pathOnServer, long fileLength){
        this.userName = userName;
        this.fileName = fileName;
        this.fileLength=fileLength;
        this.pathOnServer=pathOnServer;
    }
    public String getUserName() {
        return userName;
    }
    public String getFileName() {
        return fileName;
    }

    public String getPathOnServer() {
        return pathOnServer;
    }

    public long getFileLength() {
        return fileLength;
    }
}
