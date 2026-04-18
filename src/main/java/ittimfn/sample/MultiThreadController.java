package ittimfn.sample;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class MultiThreadController<K> {
    private ExecutorService exec;

    private Map<K, FileWriter> tasks;
    private Map<K, Future<Integer>> futures;

    private Map<K, Exception> tasksExceptions;

    public MultiThreadController(int poolSize) {
        this.exec = Executors.newFixedThreadPool(poolSize);
        this.tasks = new HashMap<>();
        this.futures = new HashMap<>();
        this.tasksExceptions = new HashMap<>();
    }

    public void put(String value) {
        K key = this.getKey(value);
        if (!this.tasks.containsKey(key)) {
            FileWriter task = new FileWriter(
                Path.of(App.APP_HOME, "output", "output_" + key + ".txt"),
                new LinkedBlockingQueue<String>()
            );
            Future<Integer> future = this.exec.submit(task);
            this.tasks.put(key, task);
            this.futures.put(key, future);
        }
        this.tasks.get(key).put(value);
    }

    public abstract K getKey(String value);

    public Map<K,Integer> shutdown() {
        Map<K, Integer> results = new HashMap<>();
        this.exec.shutdown();
        tasks.values().forEach(task -> task.shutdown());

        try {
            if (!exec.awaitTermination(180, TimeUnit.SECONDS)) {
                exec.shutdownNow();
                if (!exec.awaitTermination(180, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            exec.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            for(Map.Entry<K, Future<Integer>> entry : this.futures.entrySet()) {
                try {
                    results.put(entry.getKey(), entry.getValue().get());
                } catch (Exception e) {
                    this.tasksExceptions.put(entry.getKey(), e);
                }
            }
        }
        return results;
    }

}
