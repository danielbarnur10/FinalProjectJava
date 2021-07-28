package matrix;

import java.util.Collection;

/**
 * This interface defines the functionality required for a traversable graph
 */
public interface Traversable<T> {
    Node<T> getOrigin();
    Collection<Node<T>> getReachableNodes(Node<T> someNode);
    <R> R getValue(Node<T> someNode);
}
