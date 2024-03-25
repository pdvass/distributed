package distributed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private ExecutorService executor;

    public ThreadPool() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void submitTask(Thread task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}