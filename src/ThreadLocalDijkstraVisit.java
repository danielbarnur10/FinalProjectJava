import java.util.*;

public class ThreadLocalDijkstraVisit<T> {
    protected final ThreadLocal<PriorityQueue<Node<T>>> priorityQueueThreadLocal =
            ThreadLocal.withInitial(()->new PriorityQueue<>());
    protected final ThreadLocal<Set<Node<T>>> finishedThreadLocal =
            ThreadLocal.withInitial(()->new LinkedHashSet<>());
    protected ThreadLocal<Integer> minimumCost =
            ThreadLocal.withInitial(()->Integer.MAX_VALUE);
    protected HashSet<Node> minList =new HashSet<>();

    protected void AddPriorityQueue(Node<T> node){
        priorityQueueThreadLocal.get().add(node);
    }
    protected void AddFinishedSet(Node<T> node){
        finishedThreadLocal.get().add(node);
    }

    protected Node<T> PopPriorityQueue(){
        return priorityQueueThreadLocal.get().peek();
    }

    public List traverse(Node<T>dest,Traversable<T> partOfGraph){

        LinkedList<LinkedList<Node<T>>> allPath = new LinkedList<>();

        AddPriorityQueue(partOfGraph.getOrigin());
        AddFinishedSet(partOfGraph.getOrigin());
        partOfGraph.getOrigin().setCost(0);//setting distance source

        while(!priorityQueueThreadLocal.get().isEmpty())
        {
            Node<T> poppedNode = PopPriorityQueue();
            priorityQueueThreadLocal.get().poll();
            AddFinishedSet(poppedNode);
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(poppedNode);
            for(Node<T> singleReachableNode : reachableNodes)
            {
                if(!finishedThreadLocal.get().contains(singleReachableNode))
                {
                    if(dest.getData().equals(singleReachableNode.getData()))
                    {
                        if(poppedNode.getCost() <= minimumCost.get() )
                        {
                            minList.add(poppedNode);
                            minimumCost.set(poppedNode.getCost());

                        }

                    }
                    AddPriorityQueue(singleReachableNode);
                }
            }
        }


        for (Node<T> node:
             minList) {
            if(node.getCost() == minimumCost.get()){
                LinkedList<Node<T>> nodePath = (LinkedList<Node<T>>) getPath(node,dest);
                allPath.add(nodePath);
            }
        }
        List<LinkedList<Node<T>>> result = new LinkedList<>();
        result.addAll(allPath);
        return result;
    }
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

    private void printPath(Node<T> node)
    {
        Node<T>next = node.getParent();
        while(next.getParent()!=null)
        {
            node = node.getParent();
        }
    }
}