package server;


import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HandlingThread {


    private static volatile HandlingThread  singleHandlingThread;
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();
    int amountOfThreads = Runtime.getRuntime().availableProcessors();
    private final ExecutorService threadPool = new ThreadPoolExecutor(amountOfThreads, amountOfThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private HandlingThread() { }

    public void InvokeAll(List<Callable<Void>> tasks) throws InterruptedException {
        threadPool.invokeAll(tasks);
    }

    public void Submit(Runnable tasks) {
           threadPool.submit(tasks);

    }
    public void Shutdown()
    {
        threadPool.shutdown();
    }

    public static HandlingThread getHandlingThreadInstance()
    {
        if(singleHandlingThread == null)
        {
            lock.writeLock().lock();
            if(singleHandlingThread == null)
            {
                singleHandlingThread = new HandlingThread();
                lock.writeLock().unlock();
            }
        }
        return singleHandlingThread;
    }
}
