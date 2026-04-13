package ittimfn.sample;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class FileWriter implements Callable<Integer> {
    private Path filePath;
    private Queue<Integer> queue;
    private CountDownLatch latch;

    public FileWriter(Path filePath, Queue<Integer> queue, CountDownLatch latch) {
        this.filePath = filePath ;
        this.queue = queue;
        this.latch = latch;
    }

    @Override
    public Integer call() throws Exception {
        int count = 0;
        try(BufferedWriter writer = Files.newBufferedWriter(this.filePath, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
            while(this.latch.getCount() != 0) {
                Integer number = queue.poll();
                if (number != null) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    
}
