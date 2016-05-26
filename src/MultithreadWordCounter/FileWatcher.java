package MultithreadWordCounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by Стас on 26.05.2016.
 */
public class FileWatcher implements Callable<Integer> {

    private File directory;
    private List<Future<Integer>> futures = new ArrayList<>();
    private Integer wordCounter = 0;

    public FileWatcher(File directory) {
        this.directory = directory;
    }

    @Override
    public Integer call() throws Exception {
        if (directory.isDirectory()){
            File[] files = directory.listFiles();
            for (File file : files) {
                FutureTask<Integer> future = new FutureTask<>(new FileWatcher(file));
                futures.add(future);
                new Thread(future).start();
            }
        }
        else {
            String textLine;
            try(BufferedReader reader = new BufferedReader(new FileReader(directory))){
                while((textLine = reader.readLine())!=null){
                    wordCounter += textLine.split(" ").length;
                }
            }
            return wordCounter;
        }
        for (Future<Integer> future : futures) {
            wordCounter += future.get();
        }
        return wordCounter;
    }
}
