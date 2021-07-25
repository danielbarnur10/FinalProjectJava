import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixIHandler implements IHandler {
    // Atomic integer containing the next thread ID to be assigned
    private boolean doWork = true;
    private final ThreadLocalDfsVisit<Index> dfsAlgo;

    private final BFSvisit<Index> bfsAlgo;////////////

    private final ThreadLocalDijkstraVisit<Index> dijkstraAlgo;

    private final Index index = new Index(0, 0);
    private TraversableMatrix traversableMatrix;
    private TraversableWeightedMatrix traversableWeightedMatrix;


    public MatrixIHandler() {
        bfsAlgo = new BFSvisit<>();
        dfsAlgo = new ThreadLocalDfsVisit<>();
        dijkstraAlgo = new ThreadLocalDijkstraVisit<>();
    }

    @Override
    public void handle(InputStream inputFromUser, OutputStream inputToUser) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputFromUser);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(inputToUser);
        while (doWork) {
            String taskChoiceFromUser = objectInputStream.readObject().toString();
            switch (taskChoiceFromUser) {
                case "1": {
                    task1(objectInputStream, objectOutputStream);
                    break;
                }
                case "2": {
                    task2(objectInputStream, objectOutputStream);
                    break;
                }
                case "3": {
                    task3(objectInputStream, objectOutputStream);
                    break;
                }
                case "4": {
                    task4(objectInputStream, objectOutputStream);
                    break;
                }
                case "stop": {
                    doWork = false;
                    break;
                }

            }

        }

    }

    private void dfsAlgoCPU(TraversableMatrix matrix, HashSet<HashSet<Index>> setOfComponents) {
        // CPU consuming logic here.
        // e.g. check if set of vertices is a submarine (3), or collect all vertices in a connected component (1)
        System.out.println("Running Thread: " + Thread.currentThread().getName());
        setOfComponents.add((HashSet<Index>) dfsAlgo.traverse(traversableMatrix));

    }

    private void task1(ObjectInputStream inputFromUser, ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {
        List<Callable<Void>> tasks = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        AtomicInteger count = new AtomicInteger();
        // Get a matrix from client
        Matrix matrix = new Matrix((int[][]) inputFromUser.readObject());
        traversableMatrix = new TraversableMatrix(matrix);
        traversableMatrix.setStartIndex(index);
        int row = matrix.primitiveMatrix.length;
        int col = matrix.primitiveMatrix[0].length;

        HashSet<HashSet<Index>> setOfComponents = new HashSet<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                index.setRowAndCol(i, j);
                if (matrix.getValue(index) == 1) {
                    traversableMatrix.setStartIndex(new Index(i, j));
                    //Callable function dfsAlgoCPU(TraversableMatrix matrix)
                    tasks.add(() -> {
                        dfsAlgoCPU(traversableMatrix, setOfComponents);
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

        List componentResult = new LinkedList();
        Comparator<HashSet<Index>> lengthComparator = (component1, component2) -> Integer.compare(component1.size(), component2.size());
        setOfComponents.stream().sorted(lengthComparator).forEach((i) -> componentResult.add(i));
        outputToUser.writeObject(componentResult);
    }

    private void task2(ObjectInputStream inputFromUser, ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {
        // Get matrix
        Matrix matrix = new Matrix((int[][]) inputFromUser.readObject());

        // Get source
        Index source = (Index) inputFromUser.readObject();

        // Get target
        Index dest = (Index) inputFromUser.readObject();

        // Get traversable
        traversableMatrix = new TraversableMatrix(matrix);

        // Initialize origin index
        traversableMatrix.setStartIndex(source);

        // Get shortest path
        List shortestPath = (List) bfsAlgo.traverse(new Node(dest), traversableMatrix);

        // Sending the result to the client
        outputToUser.writeObject(shortestPath);

    }

    private Set<Index> isSubmarine(TraversableMatrix traversableMatrix, Index index) {
        // CPU consuming logic here.
        // e.g. check if set of vertices is a submarine (3), or collect all vertices in a connected component (1)
        System.out.println("Running Thread: " + Thread.currentThread().getName());
        traversableMatrix = new TraversableMatrix(traversableMatrix,index);
        Set<Index> set = (HashSet<Index>) dfsAlgo.traverse(traversableMatrix);
        Index Max = traversableMatrix.startIndex, Min = traversableMatrix.startIndex;
        //Finding max and min indecies (start and end indecies) comparing indecies

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
        //if the Area is equal to amount of count2 we return true (isSubmarine)
        if (((maxR - minR) * (maxC - minC)) == set.size()) {
            System.out.println("found Submarine");
            return set;
        } else {
            return null;

        }
    }

    private void task3(ObjectInputStream inputFromUser, ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();
        ReentrantLock locker = new ReentrantLock();
        Object object = inputFromUser.readObject();
        System.out.println("input:"+object);
        int[][] matrixFromUser = (int[][]) object;
        Matrix matrix = new Matrix(matrixFromUser);
        final TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);//final
        int row = matrix.primitiveMatrix.length;
        int col = matrix.primitiveMatrix[0].length;
        Set<Set<Index>> setOfComponents = new HashSet<>();
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {

                index.setRowAndCol(i, j);
                if (matrix.getValue(index) == 1) {
                    final Index callableIndex = new Index(i, j);
                    tasks.add(() -> {
                        Set isSubmarine = isSubmarine(traversableMatrix, callableIndex);
                        // Run the CPU consuming action outside the lock! (Why?)
                        if (isSubmarine != null) {
                            try {
                                locker.lock();
                                setOfComponents.add(isSubmarine);
                            } finally {
                                locker.unlock();
                            }
                        }
                        return null; // This is a must, cause we are Callable<Void> and not Runnable.
                    });

               /*     traversableMatrix.setStartIndex(index);
                    HashSet<Index> set = (HashSet<Index>) dfsAlgo.traverse(traversableMatrix);

                    Index Max = traversableMatrix.startIndex, Min = traversableMatrix.startIndex;
                    //Finding max and min indecies (start and end indecies) comparing indecies

                    for (Index element :
                            set) {
                        dfsAlgo.count.set(dfsAlgo.count.get());
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
                    //if the Area is equal to amount of count2 we return true (isSubmarine)
                    if (((maxR - minR) * (maxC - minC)) == dfsAlgo.count.get()) {
                        System.out.println("Found a Submarine");
                    }
                    else{
                        System.out.println("nothing");
                    }
                }
            }
       */
                }
            }
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

//        System.out.printf("Detected %d nine tailed images out of %d%n", count, images.size());

        System.out.println(setOfComponents.size());
        System.out.println(setOfComponents);
        outputToUser.writeObject(setOfComponents.size());
    }

    private void task4(ObjectInputStream inputFromUser, ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {
        // Get matrix
        Matrix matrix = new Matrix((int[][]) inputFromUser.readObject());

        // Get source
        Index source = (Index) inputFromUser.readObject();

        // Get target
        Index dest = (Index) inputFromUser.readObject();

        // Get traversable
        traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);

        // Initialize origin index
        traversableWeightedMatrix.setStartIndex(source);

        // Get shortest path
        List shortestPath = (List) dijkstraAlgo.traverse(new Node(dest), traversableWeightedMatrix);

        System.out.println(shortestPath);
//        // Sending the result to the client
//        outputToUser.writeObject(shortestPath);
    }
}
