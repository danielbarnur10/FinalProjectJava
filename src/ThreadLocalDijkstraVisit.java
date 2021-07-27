import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadLocalDijkstraVisit<T> {
    protected final ThreadLocal<PriorityQueue<Node<T>>> priorityQueueThreadLocal =
            ThreadLocal.withInitial(()->new PriorityQueue<>());
    protected final ThreadLocal<Set<Node<T>>> finishedThreadLocal =
            ThreadLocal.withInitial(()->new LinkedHashSet<>());
    protected ThreadLocal<Integer> minimumCost =
            ThreadLocal.withInitial(()->Integer.MAX_VALUE);
    protected ThreadLocal<Set<Node<T>>> minList = ThreadLocal.withInitial(()->new HashSet<>());
    protected AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    protected void AddPriorityQueue(Node<T> node){
        priorityQueueThreadLocal.get().add(node);
    }
    protected void AddFinishedSet(Node<T> node){
        finishedThreadLocal.get().add(node);
    }
    protected void AddMinSet(Node<T> node){
        minList.get().add(node);
    }
    protected Node<T> PopPriorityQueue(){
        return priorityQueueThreadLocal.get().peek();
    }



    private  Collection<Node<T>> Lightest(Traversable<T> partOfGraph,Node<T> poppedNode,Node<T> dest) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@");
        return partOfGraph.getReachableNodes(poppedNode);

    }
        /**
         *
         * @param dest
         * @param partOfGraph
         * @return List
         */
    public List traverse(Traversable<T> partOfGraph,Node<T>dest){
        List<Callable<Void>> tasks = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        ReentrantLock locker = new ReentrantLock();

        LinkedList<LinkedList<Node<T>>> allPath = new LinkedList<>();
        AddPriorityQueue(partOfGraph.getOrigin());
        AddFinishedSet(partOfGraph.getOrigin());
        partOfGraph.getOrigin().setCost(0);//setting distance source


        while(!priorityQueueThreadLocal.get().isEmpty())
        {

            tasks.add(()->{
                System.out.println("%%%%23%%%%%%%%%%%");

                if(atomicBoolean.get()) {
                   try {
                       locker.lock();
                       atomicBoolean.set(false);
                       System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                       Node<T> poppedNode = PopPriorityQueue();
                       priorityQueueThreadLocal.get().poll();
                       AddFinishedSet(poppedNode);
                       Collection<Node<T>> reachableNodes = Lightest(partOfGraph, poppedNode, dest);
                       for (Node<T> singleReachableNode : reachableNodes) {
                           threadPool.submit(new Runnable() {
                               @Override
                               public void run() {

                                   if (!finishedThreadLocal.get().contains(singleReachableNode)) {
                                       if (dest.getData().equals(singleReachableNode.getData())) {
                                           if (poppedNode.getCost() <= minimumCost.get()) {
                                               AddMinSet(poppedNode);
                                               minimumCost.set(poppedNode.getCost());
                                           }
                                       }
                                       AddPriorityQueue(singleReachableNode);
                                   }
                               }

                           });
                       }
                   }finally{ locker.unlock();
                    atomicBoolean.set(true);
                   }
                }

                return null;});

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
        for (Node<T> node:
             minList.get()) {
            if(node.getCost() == minimumCost.get()){
                LinkedList<Node<T>> nodePath = (LinkedList<Node<T>>) getPath(node,dest);
                allPath.add(nodePath);
            }
        }
        List<LinkedList<Node<T>>> result = new LinkedList<>();
        result.addAll(allPath);
        return result;
    }

    /**
     *
     * @param node
     * @param dest
     * @return
     */
    private LinkedList<T> getPath(Node<T> node,Node<T> dest)
    {
        LinkedList<T> path = new LinkedList<>();
        path.add(dest.getData());
        path.add(node.getData());
        Node<T> next = node.getParent();
        while(next!=null)
        {
            path.add(next.getData());
            next = next.getParent();
        }
        return path;
    }

    /**
     *
     * @param node
     */
    private void printPath(Node<T> node)
    {
        Node<T>next = node.getParent();
        while(next.getParent()!=null)
        {
            node = node.getParent();
        }
    }
}