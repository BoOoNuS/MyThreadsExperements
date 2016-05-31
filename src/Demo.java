import MultithreadFileCrawler.Crawler;
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

        System.out.println("//////////////////////////////////////next");

        try (Crawler crawler = new Crawler(new File("Files1"), "werg")) {
            FutureTask future = new FutureTask(crawler);
            Thread thread = new Thread(future);
            thread.start();
            System.out.println("Count of concurrence - " + future.get());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
