import java.io.Serializable;

public class InstructionForServer implements Serializable {
    private String fileName;
    private String actionCode;
    public InstructionForServer(String fileName,String actionCode){
        this.actionCode=actionCode;
        this.fileName=fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getActionCode() {
        return actionCode;
    }
}
