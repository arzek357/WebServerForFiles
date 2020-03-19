import java.io.Serializable;

public class SendPacket implements Serializable {
    private String fileName;
    private byte[] fileByteArr;
    public SendPacket(String fileName){
        this.fileName = fileName;
        fileByteArr=null;
    }
    public SendPacket(String fileName,byte[] fileByteArr){
        this.fileName = fileName;
        this.fileByteArr = fileByteArr;
    }
    public String getFileName() {
        return fileName;
    }
    public byte[] getFileByteArr() {
        return fileByteArr;
    }
    public void setFileByteArr(byte[] fileByteArr) {
        this.fileByteArr = fileByteArr;
    }
}
