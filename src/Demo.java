import MultithreadWordCounter.WordCounter;
import java.io.*;

/**
 * Created by Стас on 21.04.2016.
 */
public class Demo {
    public static void main(String[] args) {
        /**
         * Если указать не существующий файл,
         * вылетит с метода startThreads(),
         * и здесь выведет null
         */
        try (WordCounter fileWatcher = new WordCounter(new File("Files"))) {
            System.out.println("Count of word - " + fileWatcher.startThreadsWithDirectory());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
