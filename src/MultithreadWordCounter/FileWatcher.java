package MultithreadWordCounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by Стас on 26.05.2016.
 */
public class FileWatcher implements Callable<Integer>, AutoCloseable {

    private File directory;
    private Integer wordCounter = 0;
    private List<Future<Integer>> futures;
    private static String REGEXP_FOR_MATH = ".+.txt";
    private static Pattern pattern = Pattern.compile(REGEXP_FOR_MATH);
    private static Matcher match;
    private static ExecutorService exec = Executors.newFixedThreadPool(2);
    /**
     * обробатываем по два потока
     * т.к. два ядра у меня)
     * да и проще отследить роботу
     */

    public FileWatcher(File directory) {
        this.directory = directory;
    }

    //вынес в отдельный метод что-бы не заходило в файлы..
    public List<Callable<Integer>> getFiles(){
        List<Callable<Integer>> callables = new ArrayList<>();
        File[] files = directory.listFiles();
        for (File file : files) {
            callables.add(new FileWatcher(file));
        }
        return callables;
    }

    public List<Future<Integer>> tryThreads(List<Callable<Integer>> callables){
        try {
            futures = exec.invokeAll(callables);
            /**
             * invoke - я не нашел, а вот invokeAll - крутая штука,
             * получает коллекцию Callable, запускает с установленными условиями executor-а,
             * и следит за выполнениями потоков,
             * на много улучшило код по моему.
             * Возвращает масив Future-ов
             */
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return futures;
    }

    //// TODO: 26.05.2016 что бы при встрече не .txt файла не выбрасывало Nullpointer
    //// TODO: 26.05.2016 ограничить время выполнение парсинга
    @Override
    public Integer call() throws Exception {
        match = pattern.matcher(directory.toString());
        System.out.println(Thread.currentThread().getName());
        /**
         *Пытался понять какие потоки входят, и сколько их
         *и окозалось что пропускает он по два, все в порядке
         */
        if(match.matches()) {
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
            /**
             *get блокирует выполнения програмы до тех пор пока не
             *получим отклик от Future
             */
        }
        return wordCounter;
    }

    @Override
    public void close() throws Exception {
        exec.shutdown();
    }
}
