package MultithreadWordCounter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Created by Стас on 26.05.2016.
 */
public class FileWatcher implements Callable<Integer>, AutoCloseable {

    //// TODO: 06.06.2016 Сделать рекурсивный обход всех директорий
    public static final String DIRECTORY_SEPARATOR = "\\";
    public static final String SPACE = " ";

    private static String REGEXP_FOR_MATH = ".+.txt";
    private static Pattern pattern = Pattern.compile(REGEXP_FOR_MATH);
    private static ExecutorService exec = Executors.newWorkStealingPool();

    private File directory;

    public FileWatcher(File directory) {
        this.directory = directory;
    }

    private List<Callable<Integer>> getFiles() {
        List<Callable<Integer>> callables = new ArrayList<>();
        String[] files = directory.list((dir, name) -> pattern.matcher(name).matches());
        for (String file : files) {
            callables.add(new FileWatcher(new File(directory.getPath() + DIRECTORY_SEPARATOR + file)));
        }
        return callables;
    }

    public int startThreads() {
        int answer = 0;
        try {
            List<Future<Integer>> futures = exec.invokeAll(getFiles());
            for (Future<Integer> future : futures) {
                answer += future.get(2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public Integer call() throws Exception {
        Integer wordCounter = 0;
        System.out.println(Thread.currentThread().getName());
        String textLine;
        try (BufferedReader reader = new BufferedReader(new FileReader(directory))) {
            while ((textLine = reader.readLine()) != null) {
                wordCounter += textLine.split(SPACE).length;
            }
        }
        return wordCounter;
    }

    @Override
    public void close() throws Exception {
        exec.shutdown();
    }
}
