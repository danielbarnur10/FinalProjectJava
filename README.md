# Graph Algorithms

This project contains implementations of various graph traversal algorithms in Java, including BFS, DFS, and Dijkstra's algorithm. The algorithms are designed to work with a matrix representation of graphs.

## Files

- `BFSvisit.java`: Implements the Breadth-First Search (BFS) algorithm.
- `MatrixIHandler.java`: Handles matrix-related tasks and requests from a server.
- `ThreadLocalDfsVisit.java`: Implements the Depth-First Search (DFS) algorithm using thread-local storage.
- `ThreadLocalDijkstraVisit.java`: Implements Dijkstra's algorithm using thread-local storage.

## BFSvisit.java

### Functions

- **BFSvisit()**: Constructor that initializes the working queue and finished set.
- **Collection<T> traverse(Node<T> dest, Traversable<T> partOfGraph)**: Traverses the graph using BFS to find all shortest paths from the origin to the destination node.
- **private LinkedList<T> getPath(Node<T> node)**: Retrieves the path from the origin to the given node.
- **private List allShortestPaths(LinkedList<LinkedList<Node<T>>> paths)**: Finds all shortest paths from the list of paths.

## MatrixIHandler.java

### Functions

- **void handle(InputStream inputFromUser, OutputStream inputToUser)**: Handles requests from the server and performs tasks based on the user's input.
- **private void DFSCallable(TraversableMatrix traversableMatrix, Set<Set<Index>> setOfComponents)**: Performs a DFS traversal to find connected components.
- **private List<Set<Index>> task1(Matrix matrix)**: Finds all connected components in the matrix.
- **private Collection<Index> task2(Matrix matrix, Index source, Index dest)**: Finds the shortest path from the source to the destination using BFS.
- **private boolean isSubmarine(TraversableMatrix traversableMatrix, Index index)**: Checks if a set of vertices forms a submarine.
- **private int task3(Matrix matrix)**: Counts the number of submarines in the matrix.
- **private Collection<List<Index>> task4(Matrix matrix, Index source, Index dest)**: Finds the shortest path from the source to the destination in a weighted matrix using Dijkstra's algorithm.

## ThreadLocalDfsVisit.java

### Functions

- **protected void threadLocalPush(Node<T> node)**: Pushes a node onto the thread-local stack.
- **protected Node<T> threadLocalPop()**: Pops a node from the thread-local stack.
- **public Set<T> traverse(Traversable<T> partOfGraph)**: Traverses the graph using DFS and returns the set of visited nodes.

## ThreadLocalDijkstraVisit.java

### Functions

- **protected void AddPriorityQueue(Node<T> node)**: Adds a node to the thread-local priority queue.
- **protected void AddFinishedSet(Node<T> node)**: Adds a node to the thread-local finished set.
- **public Collection<List<T>> traverse(Traversable<T> graph, Node<T> dest)**: Traverses the graph using Dijkstra's algorithm to find the shortest paths from the origin to the destination node.
- **private List<T> getPath(Node<T> node, Node<T> dest)**: Retrieves the path from the origin to the destination node.

## Usage

1. Compile the code using a Java compiler.
2. Run the executable.
3. Interact with the server to perform various tasks such as finding connected components, shortest paths, and counting submarines.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
