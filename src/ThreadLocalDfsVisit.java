import java.util.*;

public class ThreadLocalDfsVisit<T> {
    protected final ThreadLocal<Stack<Node<T>>> stackThreadLocal =
            ThreadLocal.withInitial(Stack::new);
    protected final ThreadLocal<Set<Node<T>>> setThreadLocal =
            ThreadLocal.withInitial(() -> new LinkedHashSet<>());

    protected void threadLocalPush(Node<T> node) {
        stackThreadLocal.get().push(node);
    }

    protected Node<T> threadLocalPop() {
        return stackThreadLocal.get().pop();
    }
    /*
    Push to stack the starting node of our graph V
    While stack is not empty: // there are nodes to handle V
        removed = pop operation V
        insert to finish set V
        invoke getReachableNodes on the removed node V
        For each reachable node:
            if the current reachable node is not in finished set && working stack
            push to stack
     */


    /**
     * travers is a DFS function
     * @return  HashSet<T> blackList
     * @param partOfGraph
     * @param visited
     */
    public Set<T> traverse(HashSet<Index> visited, Traversable<T> partOfGraph) {
        threadLocalPush(partOfGraph.getOrigin());
        while (!stackThreadLocal.get().isEmpty()) {
            Node<T> poppedNode = threadLocalPop();
            setThreadLocal.get().add(poppedNode);
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(poppedNode);
            for (Node<T> singleReachableNode : reachableNodes) {
                if (!setThreadLocal.get().contains(singleReachableNode) &&
                        !stackThreadLocal.get().contains(singleReachableNode)) {
                    threadLocalPush(singleReachableNode);
                }
            }
        }
        HashSet<T> blackList = new HashSet<>();
        for (Node<T> node : setThreadLocal.get()) {
            visited.add((Index) node.getData());
            blackList.add(node.getData());
        }
        stackThreadLocal.remove();
        setThreadLocal.remove();
        return blackList;
    }

}









