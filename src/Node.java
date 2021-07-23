import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.PriorityQueue;

/**
 * This class wraps a concrete object and supplies getters and setters
 *
 * @param <T>
 *
 */
public class Node<T> implements Comparable<Node<T>>
{
    private T data;
    private Node<T> parent;
    private int cost;


    public Node(T someObject, final Node<T> discoveredBy){
        this.data = someObject;
        this.parent = discoveredBy;
        this.cost = Integer.MAX_VALUE;
    }

    public Node(T someObject){
        this(someObject,null);
    }

    public T getData() {
        return data;
    }

    public Node(){
        this(null);
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    /*
    This is used when accessing objects multiple times with comparisons,
    when using a HashTable
    Set<Node<T>> finished - this will work only if concrete object are different
    Node<Index> Node<Coordinate> Node<ComputerLocation>
    Node<Index> Node<Index> Node<Index>
     */
    @Override
    public int hashCode() {
        return data != null ? data.hashCode():0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node<?> state1 = (Node<?>) o;

        return Objects.equals(data, state1.data);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public int compareTo(@NotNull Node<T> o) {
        return Integer.compare(this.cost,o.cost);
    }

    public static void main(String[] args) {
        Node<Index> node1 = new Node<>(new Index(1,1),null);
        Node<Index> node2 = new Node<>(new Index(2,2),null);
        Node<Index> node3 = new Node<>(new Index(3,3),null);

        node1.setCost(1);
        node2.setCost(2);
        node3.setCost(3);

        PriorityQueue<Node<Index>> queue = new PriorityQueue<>();
        queue.add(node1);
        queue.add(node2);
        queue.add(node3);

        System.out.println(queue.peek());

    }
}
