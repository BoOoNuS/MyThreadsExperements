package MultithreadWordCounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Стас on 26.05.2016.
 */
public class FileWatcher implements Callable<Integer> {

    private File directory;
    private List<Future<Integer>> futures = new ArrayList<>();
    private Integer wordCounter = 0;
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
    public File[] getFiles(){
        File[] files = directory.listFiles();
        for (File file : files) {
            FutureTask<Integer> future = new FutureTask<>(new FileWatcher(file));
            futures.add(future);
            exec.execute(future);
        }
        exec.shutdown();
        return files;
    }

    @Override
    public Integer call() throws Exception {

        System.out.println(Thread.currentThread().getName());
        /**
         *Пытался понять какие потоки входят, и сколько их
         *и окозалось что пропускает он по два, все в порядке
         */

        //в файл теперь заходить не будем, посчитаем все корректно
        if(directory.isFile()) {
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
}
