import javax.print.DocFlavor;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePacket implements Serializable {
    private Path fileName;
    private long fileLength;
    private File fullPath;

    public FilePacket(File file){
    this.fullPath = file;
    fileLength=file.length();
    findFileName();
    }

    public FilePacket(String fileName, long fileLength, String fullPath) {
        this.fileName = Paths.get(fileName);
        this.fileLength = fileLength;
        this.fullPath = new File(fullPath);
    }

    public File getFile() {
        return fullPath;
    }

    public long getFileLength() {
        return fileLength;
    }

    public Path getFileName() {
        return fileName;
    }
    private void findFileName(){
        Path path = Paths.get(fullPath.toURI());
        fileName=path.getFileName();
    }
}
