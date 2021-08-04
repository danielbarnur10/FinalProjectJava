package algorithms;

import matrix.*;
import server.IHandler;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixIHandler implements IHandler {
    private boolean doWork = true;
    private final Index index = new Index(0, 0);
    private TraversableWeightedMatrix traversableWeightedMatrix;
    private Matrix matrix;
    private Matrix weightedMatrix;
    private List task1;


    /**
     *
     * @param inputFromUser
     * @param inputToUser
     * @throws IOException
     * @throws ClassNotFoundException
     * Handles requests from Server
     */
    @Override
    public void handle(InputStream inputFromUser, OutputStream inputToUser) throws IOException, ClassNotFoundException {

        ObjectInputStream objectInputStream = new ObjectInputStream(inputFromUser);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(inputToUser);

        while (doWork) {
            String taskChoiceFromUser = objectInputStream.readObject().toString();
            switch (taskChoiceFromUser) {

                case "matrix":
                {
                    this.matrix = new Matrix((int[][]) objectInputStream.readObject());
                    break;
                }

                case "weightedMatrix":
                {
                    this.weightedMatrix = new Matrix((int[][]) objectInputStream.readObject());
                    break;
                }

                case "1": {
                    List result = task1(matrix);
                    objectOutputStream.writeObject(result);
                    break;
                }
                case "2": {

                    // Receive source from Server
                    Index source = (Index) objectInputStream.readObject();
                    // Receive target from Server
                    Index dest = (Index) objectInputStream.readObject();
                    Collection<Index> result = task2(matrix, source, dest);
                    objectOutputStream.writeObject(result);
                    break;
                }
                case "3": {
                    int result = task3(matrix);
                    objectOutputStream.writeObject(result);
                    break;
                }
                case "4":{

                    // Get source
                    Index source = (Index) objectInputStream.readObject();

                    // Get target
                    Index dest = (Index) objectInputStream.readObject();

                    Collection<List<Index>> result = task4(weightedMatrix,source,dest);

                    objectOutputStream.writeObject(result);
                }
                case "stop": {
                    doWork = false;
                    break;
                }

            }

        }

    }

    /**
     *
     * @param traversableMatrix
     * @param setOfComponents
     * Related function to Task 1.
     */
    private void DFSCallable(TraversableMatrix traversableMatrix, Set<HashSet<Index>> setOfComponents) {
        // CPU consuming logic here.
        // e.g. check if set of vertices is a submarine (3), or collect all vertices in a connected component (1)
        System.out.println("Running Thread: " + Thread.currentThread().getName());
        setOfComponents.add((HashSet<Index>) new ThreadLocalDfsVisit<Index>().traverse(traversableMatrix));

    }

    /**
     *
     * @param matrix
     * @return List of connected component.
     *  SCC- strongly connected components
     */
    private List task1(Matrix matrix)  {
        List<Callable<Void>> tasks = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
//        AtomicInteger count = new AtomicInteger();
        // Get a matrix from client
        int row = matrix.getRowSize();
        int col = matrix.getColumnSize();

        HashSet<HashSet<Index>> setOfComponents = new HashSet<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                index.setRowAndCol(i, j);
                if (matrix.getValue(index) == 1) {
                    TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);
                    traversableMatrix.setStartIndex(new Index(i, j));
                    //Callable function DFSCallableDFSCallable(matrix.TraversableMatrix matrix,setOfcomponents)
                    tasks.add(() -> {
                        DFSCallable(traversableMatrix, setOfComponents);
                        return null;
                    });
                }
            }
        }
        try {
            // Now invoke all of the tasks
            threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            // Handle the exception here..
            e.printStackTrace();
        }
        threadPool.shutdown();
        List componentResult = new LinkedList();
        Comparator<HashSet<Index>> lengthComparator = (component1, component2) -> Integer.compare(component1.size(), component2.size());
        setOfComponents.stream().sorted(lengthComparator).forEach((i) -> componentResult.add(i));
        task1 = componentResult;
        return componentResult;
    }

    /**
     *
     * @param matrix
     * @param source
     * @param dest
     * @return Collection<Index>
     * Lightest paths from source to destination

     */
    private Collection<Index> task2(Matrix matrix,Index source, Index dest) {

        // Create matrix.TraversableMatrix Object
        TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);

        // Initialize origin index
        traversableMatrix.setStartIndex(source);

        // Receive shortest path from BFSAlgo class traverse
        Collection<Index> shortestPath = new BFSvisit<Index>().traverse(new Node<>(dest), traversableMatrix);

        return shortestPath;
    }

    /**
     *
     * @param traversableMatrix
     * @param index
     * @return Set<matrix.Index>
     * Submarine Game, Related to Task 3.
     */
    private boolean isSubmarine(TraversableMatrix traversableMatrix, Index index) {
        // CPU consuming logic here.
        // e.g. check if set of vertices is a submarine (3), or collect all vertices in a connected component (1)
        System.out.println("Running Thread: " + Thread.currentThread().getName());
        traversableMatrix = new TraversableMatrix(traversableMatrix,index);
        Set<Index> set = new ThreadLocalDfsVisit<Index>().traverse(traversableMatrix);
        if(set.size() < 2)
        {
            return false;
        }
        Index Max = traversableMatrix.getStartIndex(), Min = traversableMatrix.getStartIndex();
        //Finding max and min indices (start and end indices) comparing indices

        for (Index element :
                set) {
            if (element.compareTo(Max) == 1) {
                Max = element;

            }
            if (element.compareTo(Min) <= 0) {
                Min = element;
            }
        }
        int maxR = Max.getRow() + 1, maxC = Max.getColumn() + 1;
        int minR = Min.getRow(), minC = Min.getColumn();
        //Calculating the Area of the ones (1)s,
        //if the Area is equal to amount of set we return true (isSubmarine)
        if (((maxR - minR) * (maxC - minC)) == set.size()) {
            System.out.println(set);
            return true;
        } else {
            return false;

        }
    }

    /**
     *
     * @param matrix
     * @return int
     * Submarine Game, return number of submarines
     */
    private int task3(Matrix matrix)  {

        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        List<HashSet<Index>> connectedComponent = task1;
        final TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);

        for(HashSet<Index> component : connectedComponent)
        {
            Index index = (Index) component.toArray()[0];
            tasks.add(() -> {
                boolean isSubmarine = isSubmarine(traversableMatrix, index);
                // Run the CPU consuming action outside the lock! (Why?)
                if (isSubmarine) {
                    counter.incrementAndGet();
                }
                return null; // This is a must, cause we are Callable<Void> and not Runnable.
            });

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
        threadPool.shutdown();

        return counter.get();
    }

    /**
     *
     * @param matrix
     * @param source
     * @param dest
     * @return
     */
    private Collection<List<Index>> task4(Matrix matrix,Index source,Index dest) {

        // Get traversable
        traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);

        // Initialize origin index
        traversableWeightedMatrix.setStartIndex(source);

        Collection<List<Index>> LightestPaths = new ThreadLocalDijkstraVisit<Index>().traverse(traversableWeightedMatrix, new Node<>(dest));

        return LightestPaths;

    }
}
