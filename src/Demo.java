import MultithreadWordCounter.FileWatcher;
import java.io.*;

/**
 * Created by Стас on 21.04.2016.
 */
public class Demo {
    public static void main(String[] args) {
        try (FileWatcher fileWatcher = new FileWatcher(new File("Files"))) {
            System.out.println("Count of word - " + fileWatcher.startThreads());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
