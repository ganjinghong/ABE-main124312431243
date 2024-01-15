import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class storePropToFileUtil {
    public static void storePropToFile(Properties prop, String fileName){
        try(FileOutputStream out = new FileOutputStream(fileName)){
            prop.store(out, null);
        }
        catch (IOException e) {
            e.printStackTrace();
//            System.out.println(fileName + " save failed!");
            System.exit(-1);
        }
    }
}
