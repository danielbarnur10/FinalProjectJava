package algorithms;

import matrix.Node;
import matrix.Traversable;

import java.util.*;

public class ThreadLocalDfsVisit<T> {
    protected final ThreadLocal<Stack<Node<T>>> stackThreadLocal =
            ThreadLocal.withInitial(Stack::new);
    protected final ThreadLocal<Set<Node<T>>> setThreadLocal =
            ThreadLocal.withInitial(()->new LinkedHashSet<>());

    protected void threadLocalPush(Node<T> node){
        stackThreadLocal.get().push(node);
    }

    protected Node<T> threadLocalPop(){
        return stackThreadLocal.get().pop();
    }


    public Set<T> traverse(Traversable<T> partOfGraph){
        threadLocalPush(partOfGraph.getOrigin());
        while(!stackThreadLocal.get().isEmpty()){
            Node<T> poppedNode = threadLocalPop();
            setThreadLocal.get().add(poppedNode);
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(poppedNode);
            for (Node<T> singleReachableNode: reachableNodes){
                if (!setThreadLocal.get().contains(singleReachableNode) &&
                        !stackThreadLocal.get().contains(singleReachableNode)){
                    threadLocalPush(singleReachableNode);
                }
            }
        }
        HashSet<T> blackList = new HashSet<>();
        for (Node<T> node: setThreadLocal.get()){
            blackList.add(node.getData());
        }
        this.setThreadLocal.get().clear();
        this.stackThreadLocal.get().clear();
        return blackList;
    }



}
