package ittimfn.sample;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class FileWriter implements Callable<Integer> {
    private Path filePath;
    private Queue<String> queue;

    private boolean isRunning = true;

    public FileWriter(Path filePath, Queue<String> queue) {
        this.filePath = filePath ;
        this.queue = queue;
    }

    @Override
    public Integer call() throws Exception {
        int count = 0;
        try(BufferedWriter writer = Files.newBufferedWriter(this.filePath, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
            while(this.isRunning || !queue.isEmpty()) {
                String line = queue.poll();
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                    count++;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return count;
    }

    public void put(String line) {
        this.queue.offer(line);
    }

    public void shutdown() {
        this.isRunning = false;
    }
    
}
