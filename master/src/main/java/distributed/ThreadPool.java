package distributed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The ThreadPool class represents the implementation of several threads that we will need to implement 
 * in the Terminal class with respect to the various requests it will have to serve.
 * 
 * @author panagou
 * @see Terminal
 */

public class ThreadPool {
    private ExecutorService executor;

    public ThreadPool() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void submitTask(Thread task) {
        executor.submit(task);
    }

    public void timingRequests(Thread task) {

		synchronized(task) {
			System.out.println();
		}

	}

    public void shutdown() {
        executor.shutdown();
    }
}