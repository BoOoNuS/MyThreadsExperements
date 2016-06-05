import MultithreadWordCounter.FileWatcher;
import java.io.*;
import java.util.concurrent.FutureTask;

/**
 * Created by Стас on 21.04.2016.
 */
public class Demo {
    public static void main(String[] args) {
        try (FileWatcher fileWatcher = new FileWatcher(new File("Files"))) {
            fileWatcher.tryThreads(fileWatcher.getFiles());
            FutureTask<Integer> future = new FutureTask<>(fileWatcher);
            Thread thread = new Thread(future);
            thread.start();
            System.out.println("Count of word - " + future.get());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
