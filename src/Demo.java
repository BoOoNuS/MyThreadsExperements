import MultithreadWordCounter.FileWatcher;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Стас on 21.04.2016.
 */
public class Demo {
    public static void main(String[] args) {
        FileWatcher fileWatcher = new FileWatcher(new File("Files"));
        fileWatcher.tryThreads(fileWatcher.getFiles());
        FutureTask<Integer> future = new FutureTask<>(fileWatcher);
        Thread thread = new Thread(future);
        thread.start();
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            fileWatcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
