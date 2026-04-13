package ittimfn.sample;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class MultiThreadController<K, V, P> {
    private ExecutorService exec;

    private Map<K, Callable<P>> tasks;
    private Map<K, Future<V>> futures;

    public MultiThreadController(int poolSize) {
        this.exec = Executors.newFixedThreadPool(poolSize);
    }

    public void put(P value) {
        K key = this.getKey(value);
        Callable<K> task = this.tasks.get(key);
        if (task != null) {
            Future<V> future = this.exec.submit(task);
            this.futures.put(key, future);
        }
    }

    public abstract K getKey(P value);

}
