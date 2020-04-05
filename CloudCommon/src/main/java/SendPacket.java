import java.io.Serializable;

public class SendPacket implements Serializable {
    private String fileName;
    private byte[] fileByteArr;
    private String actionCode;
    public SendPacket(String fileName,String actionCode){
        this.fileName = fileName;
        this.actionCode = actionCode;
        fileByteArr=null;
    }
    public SendPacket(String fileName,byte[] fileByteArr,String actionCode){
        this.fileName = fileName;
        this.fileByteArr = fileByteArr;
        this.actionCode =actionCode;
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
    public String getActionCode() {
        return actionCode;
    }
}
