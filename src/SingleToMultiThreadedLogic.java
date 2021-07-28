import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class SingleToMultiThreadedLogic {
    // The collector. All tasks that return true will be counted.
    // Updating this variable must be atomic when running in multi threaded environment. (Critical Section)
    int count;

    private boolean isNineTailed(int[][] image) {
        // CPU consuming logic here.
        // e.g. check if set of vertices is a submarine (3), or collect all vertices in a connected component (1)

        return false;
    }

    public void countSingleThreaded() {
        // Your data, e.g. list of sets (list of connected component from Ex1, or list of all vertices (all indices with value=1)
        List<int[][]> images = new ArrayList<>();
        count = 0;

        for (int[][] image : images) {
            if (isNineTailed(image)) {
                count++;
            }
        }

        System.out.printf("Detected %d nine tailed images out of %d%n", count, images.size());
    }

    // This implementation does not require us to synchronize the access to our collector.
    // Another implementation uses a locker. Check next method
    public void countMultiThreaded() {
        // Your thread pool must be limited (upper bound) so the jvm will not crush
        // Example for using thread pool of 10 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // Prepare callables that we will invoke using the thread pool.
        // The thread pool will take care of separating the tasks among its threads
        // Here our logic returns a boolean, hence Callable<Boolean>. Ex1 returns Set<matrix.Index> or Set<matrix.Node>
        List<Callable<Boolean>> tasks = new ArrayList<>();

        // Your data, e.g. list of sets (list of connected component from Ex1, or list of all vertices (all indices with value=1)
        List<int[][]> images = new ArrayList<>();
        count = 0;

        // Create all tasks that will be invoked later, in parallel
        for (int[][] image : images) {
            // Here we create a Callable using lambda reference
            tasks.add(() -> isNineTailed(image));
        }

        try {
            // Now invoke all of the tasks
            List<Future<Boolean>> allResultsAsFuture = threadPool.invokeAll(tasks);

            // Now get all of the results, and count those that we wanted
            for (Future<Boolean> currResult: allResultsAsFuture) {
                if (currResult.get()) {
                    count++;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            // Handle the exception here.. Somehow...
            e.printStackTrace();
        }

        System.out.printf("Detected %d nine tailed images out of %d%n", count, images.size());
    }


    public void countMultiThreadedWithLocker() {
        // Your thread pool must be limited (upper bound) so the jvm will not crush
        // Example for using thread pool of 10 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // Prepare callables that we will invoke using the thread pool.
        // The thread pool will take care of separating the tasks among its threads
        // Here our logic does not return anything. (Void) Every task updates the collector,
        // so we also have to lock (sync) the access to the collector.
        // Use Callable<Void> and not Runnable cause there is no "invokeAll" method that accepts runnables.. :|
        List<Callable<Void>> tasks = new ArrayList<>();

        ReentrantLock locker = new ReentrantLock();

        // Your data, e.g. list of sets (list of connected component from Ex1, or list of all vertices (all indices with value=1)
        List<int[][]> images = new ArrayList<>();

        // Create all tasks that will be invoked later, in parallel
        for (int[][] image : images) {
            // Here we create a Callable using lambda reference
            tasks.add(() -> {
                boolean isNineTailed = isNineTailed(image);
                // Run the CPU consuming action outside the lock! (Why?)
                if (isNineTailed) {
                    locker.lock();
                    try {
                        count++;
                    } finally {
                        locker.unlock();
                    }
                }
                return null; // This is a must, cause we are Callable<Void> and not Runnable.
            });
        }
        try {
            // Now invoke all of the tasks
            // Note that there is no result from callable, we use Callable<Void>, so we do not need
            // to iterate over the futures. invokeAll already waits for all tasks to complete.
            threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            // Handle the exception here.. Somehow...
            e.printStackTrace();
        }

        System.out.printf("Detected %d nine tailed images out of %d%n", count, images.size());
    }
    public static void main(String[] args) {
    }
    }
