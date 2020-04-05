import javafx.collections.ObservableList;

import java.io.File;
/*
*Основной механизм этого статического класса - правильное присвоение имен файлам, уже имеещимся в директории, но добавленными туда вновь.
*/
public class CopyFileModule {
    static File checkFileAndBackUniName(File file){
        int count = 0;
        return checkFile(file,count);
    }
    private static File checkFile(File file,int count){
        count++;
        if (!file.exists()){
            return file;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(file);
        if (count==1){
            sb.insert(sb.indexOf("."),"_copy"+count);
        }
        else {
            sb.replace(sb.indexOf(".")-1,sb.indexOf("."),Integer.toString(count));
        }
        file = new File(sb.toString());
        return checkFile(file,count);
    }
}
