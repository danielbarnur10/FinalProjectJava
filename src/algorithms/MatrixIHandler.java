package algorithms;

import matrix.*;
import server.HandlingThread;
import server.IHandler;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class MatrixIHandler implements IHandler {
    private boolean doWork = true;
    private final Index index = new Index(0, 0);
    private Matrix matrix;
    private Matrix weightedMatrix;
    private List<Set<Index>> task1;


    /**
     *
     * @param inputFromUser from user
     * @param inputToUser to user
     * @throws IOException exception
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
                    List<Set<Index>> result = task1(matrix);
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
     * @param traversableMatrix created at task 1
     * @param setOfComponents
     * Related function to Task 1.
     */
    private void DFSCallable(TraversableMatrix traversableMatrix, Set<Set<Index>> setOfComponents) {
        // CPU consuming logic here.
        // e.g. check if set of vertices is a submarine (3), or collect all vertices in a connected component (1)
        System.out.println("Running Thread: " + Thread.currentThread().getName());
        setOfComponents.add(new ThreadLocalDfsVisit<Index>().traverse(traversableMatrix));

    }

    /**
     *
     * @param matrix from client
     * @return List of connected component.
     *  SCC- strongly connected components
     */
    private List<Set<Index>> task1(Matrix matrix)  {
        List<Callable<Void>> tasks = new ArrayList<>();
//       ExecutorService threadPool = Executors.newFixedThreadPool(10);
        HandlingThread.getHandlingThreadInstance();
        // Get a matrix from client
        int row = matrix.getRowSize();
        int col = matrix.getColumnSize();

        Set<Set<Index>> setOfComponents = new HashSet<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                index.setRowAndCol(i, j);
                if (matrix.getValue(index) == 1) {
                    TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);
                    traversableMatrix.setStartIndex(new Index(i, j));
                    //Callable function DFSCallableDFSCallable(matrix.TraversableMatrix matrix,setComponents)
                    tasks.add(() -> {
                        DFSCallable(traversableMatrix, setOfComponents);
                        return null;
                    });
                }
            }
        }
        try {
            // Now invoke all of the tasks
            HandlingThread.getHandlingThreadInstance().InvokeAll(tasks);
        } catch (InterruptedException e) {
            // Handle the exception here..
            e.printStackTrace();
        }
        List<Set<Index>> componentResult = new LinkedList<>();
        setOfComponents.stream().sorted(Comparator.comparingInt(Set::size)).forEach(componentResult::add);
        task1 = componentResult;
        return componentResult;
    }

    /**
     *
     * @param matrix from client
     * @param source from client source index
     * @param dest from client destination index
     * @return Collection<Index>
     * Lightest paths from source to destination

     */
    private Collection<Index> task2(Matrix matrix,Index source, Index dest) {

        // Create matrix.TraversableMatrix Object
        TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);

        // Initialize origin index
        traversableMatrix.setStartIndex(source);

        // Receive shortest path from BFSAlgo class traverse

        return new BFSvisit<Index>().traverse(new Node<>(dest), traversableMatrix);
    }

    /**
     *
     * @param traversableMatrix from Task 3 the for traverse
     * @param index the index start
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
            if (element.compareTo(Max) > 0) {
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
     * @param matrix from client
     * @return int
     * Submarine Game, return number of submarines
     */
    private int task3(Matrix matrix)  {

//        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        List<Set<Index>> connectedComponent = task1;
        final TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);

        for(Set<Index> component : connectedComponent)
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
            HandlingThread.getHandlingThreadInstance().InvokeAll(tasks);
        } catch (InterruptedException e) {
            // Handle the exception here.. Somehow...
            e.printStackTrace();
        }

        return counter.get();
    }

    /**
     *
     * @param matrix from client weighted matrix
     * @param source start
     * @param dest end
     * @return Collection<List<Index>>
     */
    private Collection<List<Index>> task4(Matrix matrix,Index source,Index dest) {

        // Get traversable
        TraversableWeightedMatrix traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);

        // Initialize origin index
        traversableWeightedMatrix.setStartIndex(source);

        return new ThreadLocalDijkstraVisit<Index>().traverse(traversableWeightedMatrix, new Node<>(dest));

    }
}
