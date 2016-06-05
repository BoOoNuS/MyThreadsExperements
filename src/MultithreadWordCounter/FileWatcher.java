package MultithreadWordCounter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Стас on 26.05.2016.
 */
public class FileWatcher implements Callable<Integer>, AutoCloseable {

    private static String REGEXP_FOR_MATH = ".+.txt";
    private static Pattern pattern = Pattern.compile(REGEXP_FOR_MATH);
    private static Matcher match;
    //еще одна класная штука в экзекюторе, запускает столько потоков, сколько ядер у машины
    private static ExecutorService exec = Executors.newWorkStealingPool();
    public static final String DIRECTORY_SEPARATOR = "\\";
    private File directory;
    private Integer wordCounter = 0;
    private List<Future<Integer>> futures;


    public FileWatcher(File directory) {
        this.directory = directory;
    }

    public List<Callable<Integer>> getFiles(){
        List<Callable<Integer>> callables = new ArrayList<>();
        String[] files = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                match = pattern.matcher(name);
                return match.matches();
            }
        });
        for (String file : files) {
            callables.add(new FileWatcher(new File(directory.getPath() + DIRECTORY_SEPARATOR + file)));
        }
        return callables;
    }

    public List<Future<Integer>> tryThreads(List<Callable<Integer>> callables){
        try {
            futures = exec.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return futures;
    }

    @Override
    public Integer call() throws Exception {
        //Здесь глупы момент с матчером и ифом, я подумаю как его убрать
        match = pattern.matcher(directory.toString());
        if (match.matches()) {
            System.out.println(Thread.currentThread().getName());
            String textLine;
            try (BufferedReader reader = new BufferedReader(new FileReader(directory))) {
                while ((textLine = reader.readLine()) != null) {
                    wordCounter += textLine.split(" ").length;
                }
            }
            return wordCounter;
        }

        for (Future<Integer> future : futures) {
            wordCounter += future.get(2, TimeUnit.SECONDS);
        }
        return wordCounter;
    }

    @Override
    public void close() throws Exception {
        exec.shutdown();
    }
}
