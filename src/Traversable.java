import java.util.Collection;

/**
 * This interface defines the functionality required for a traversable graph
 */
public interface Traversable<T> {
    public Node<T> getOrigin();
    public Collection<Node<T>> getReachableNodes(Node<T> someNode);

    Collection<Node<T>> getReachableNodes2(Node<T> poppedNode);
}
