package MultithreadFileCrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Стас on 30.05.2016.
 */
public class Crawler implements Callable<Integer>, AutoCloseable {

    private File directory;
    private Integer countOfConcurrence = 0;
    private static String keyword;
    private List<Callable<Integer>> forExec = new ArrayList<>();
    private static ExecutorService exec = Executors.newCachedThreadPool();
    private List<Future<Integer>> results = new ArrayList<>();

    public Crawler(File directory, String keyword) {
        this.directory = directory;
        this.keyword = keyword;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println(Thread.currentThread().getName());
        if(directory.isDirectory()){
            File[] files = directory.listFiles();
            for (File file : files) {
                forExec.add(new Crawler(file, keyword));
            }
            results = exec.invokeAll(forExec);
        }
        else {
            return wordCounter(directory);
        }
        for (Future<Integer> result : results) {
            countOfConcurrence += result.get();
        }
        return countOfConcurrence;
    }

    private Integer wordCounter(File directory){
        String text;
        Integer countOfConcurrence = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(directory))){
            while((text = reader.readLine())!=null){
                String[] patsText = text.split(" ");
                for (String s : patsText) {
                    if(s.equals(keyword)){
                        countOfConcurrence++;
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return countOfConcurrence;
    }

    @Override
    public void close() throws Exception {
        exec.shutdown();
    }
}
