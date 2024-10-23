
package server.game;
import java.io.File;

public interface DataFileDir {
    static String currDir = System.getProperty("user.dir");
    String dataFileDir = currDir + File.separator + "data" + File.separator;
}
