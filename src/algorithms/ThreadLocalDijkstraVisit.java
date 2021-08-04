package algorithms;

import matrix.Node;
import matrix.Traversable;
import server.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadLocalDijkstraVisit<T> {
    protected final ConcurrentLinkedQueue<Node<T>> priorityQueueThreadLocal = new ConcurrentLinkedQueue<>();
    protected final ThreadLocal<Set<Node<T>>> finishedThreadLocal = ThreadLocal.withInitial(LinkedHashSet::new);
    protected Integer minimumCost = Integer.MAX_VALUE;
    protected Set<Node<T>> minList = new HashSet<>();

    /**
     * We use this counter for having an indication of when we end traversing the whole matrix.<br/>
     *
     */
    private final AtomicInteger tasksCounter = new AtomicInteger();

    /**
     * A locker to protect minimumCost and minList because we update them from multiple threads.
     */
    private final ReentrantLock locker = new ReentrantLock();

    protected void AddPriorityQueue(Node<T> node) {
        priorityQueueThreadLocal.add(node);
    }

    protected void AddFinishedSet(Node<T> node) {
        finishedThreadLocal.get().add(node);
    }


    /**
     * @param dest
     * @param graph
     * @return List
     */
    public Collection<List<T>> traverse(Traversable<T> graph, Node<T> dest) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Set<List<T>> allPath = new HashSet<>();
        AddPriorityQueue(graph.getOrigin());
        graph.getOrigin().setCost(0);//setting distance source

        // Now submit the first visit task, which will submit its sub-tasks, based
        HandlingThread.getHandlingThreadInstance().Submit(new NodeVisitTask( graph, graph.getOrigin(), dest));

        try {
            while (tasksCounter.get() > 0) {
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            // Handle the exception here.. Somehow...
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        for (Node<T> node : minList) {
            if (node.getCost() == minimumCost) {
                List<T> nodePath = getPath(node, dest);
                allPath.add(nodePath);
            }
        }
        System.out.println(allPath);
        return allPath;
    }

    /**
     * @param node
     * @param dest
     * @return
     */
    private List<T> getPath(Node<T> node, Node<T> dest) {
        List<T> path = new LinkedList<>();
        path.add(dest.getData());
        path.add(node.getData());
        Node<T> next = node.getParent();
        while (next != null) {
            path.add(next.getData());
            next = next.getParent();
        }
        return path;
    }

    private class NodeVisitTask implements Runnable {
        private final Traversable<T> graph;

        /**
         * Already consumed vertex - It has cost
         */
        private final Node<T> node;

        private final Node<T> dest;

        /**
         * The thread pool which is used to invoke us. (NodeVisitTask)
         */


        public NodeVisitTask( Traversable<T> graph, Node<T> node, Node<T> dest) {
            this.graph = graph;
            this.node = node;
            this.dest = dest;
            tasksCounter.incrementAndGet();
        }

        @Override
        public void run() {
            AddFinishedSet(node);
            Collection<Node<T>> neighbors = graph.getReachableNodes(node);

            for (Node<T> neighbor : neighbors) {
                int newCost = node.getCost() + (Integer) graph.getValue(neighbor);
                if (newCost < neighbor.getCost()) {
                    neighbor.setCost(newCost);
                }

                if (!finishedThreadLocal.get().contains(neighbor)) {
                    if (dest.getData().equals(neighbor.getData())) {
                        // Lock to protect minimumCost and List when we get here from multiple threads
                        locker.lock();
                        try {
                            if (node.getCost() <= minimumCost) {
                                minList.add(node);
                                minimumCost = node.getCost();
                                System.out.println("mini cost " + minimumCost);
                            }
                        } finally {
                            locker.unlock();
                        }
                    } else {
                        // Parallel
                        HandlingThread.getHandlingThreadInstance().Submit((new NodeVisitTask(graph, neighbor, dest)));
                    }
                }
            }

            tasksCounter.decrementAndGet();
        }
    }
}