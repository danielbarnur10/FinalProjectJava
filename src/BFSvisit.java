import java.util.*;
import java.util.stream.Collectors;

public class BFSvisit<T> {

    // Queue and set for BFS algorithm.
    private final LinkedList<Node<T>> workingQueue;
    private final Set<Node<T>> finished;

    public BFSvisit(){
        finished = new HashSet<>();
        workingQueue = new LinkedList<>();
    }

    public Collection<T> traverse(Node<T> dest, Traversable<T> partOfGraph){

        // A linked list that store all the path from origin to the target.
        LinkedList<LinkedList<Node<T>>> allPath = new LinkedList<>();

       // Add the origin node to the queue.
        workingQueue.add(partOfGraph.getOrigin());


        // while the queue is not empty keep taking out node from the queue.
        while(!workingQueue.isEmpty()){
            Node<T> poppedNode = workingQueue.poll();

            // Add the poppedNode to the finished set.
            finished.add(poppedNode);

            // Get all reachable nodes of poppedNode.
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(poppedNode);

            // Goes over all the reachable nodes of poppedNode.
            for (Node<T> singleReachableNode: reachableNodes){

                // If finished set not contain the reachable node go into the loop.
                if (!finished.contains(singleReachableNode)){

                    // Initialize the parent of the reachable node to the poppedNode.
                    singleReachableNode.setParent(poppedNode);

                    //  If the value of reachable node equals to dest node its means that we arrive at the target.
                    if(dest.getData().equals((singleReachableNode.getData()))) {

                        // Store the path in allPath LinkedList, use getPath function.
                        allPath.add((LinkedList<Node<T>>) getPath(singleReachableNode));
                    }

                    // Add the reachable nod into the queue for searching another paths.
                    workingQueue.add(singleReachableNode);
                }
            }
        }
        // List to store the shortest paths.
        List shortestPaths = allShortestPaths(allPath);
        return shortestPaths;
    }

    // "getPath" is a function to get all the "family trees" of the current node.
    private LinkedList<T> getPath(Node<T> node)
    {
        LinkedList<T> path = new LinkedList<>();
        path.add(node.getData());
        Node<T> next = node.getParent();
        while(next!=null)
        {
            path.add(next.getData());
            next = next.getParent();
        }
        return path;
    }

    // Find all minimum paths
    private List allShortestPaths(LinkedList<LinkedList<Node<T>>> paths)
    {
        int minPath = paths
                .stream()
                .mapToInt(LinkedList::size)
                .min()
                .orElse(Integer.MIN_VALUE);

        List shortPaths = paths.stream().filter((x)->x.size() == minPath).collect(Collectors.toList());
        return shortPaths;
    }
}