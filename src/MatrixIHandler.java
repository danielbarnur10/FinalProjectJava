import java.io.*;
import java.util.*;

public class MatrixIHandler implements IHandler{

    private boolean doWork = true;
    private final ThreadLocalDfsVisit<Index> dfsAlgo;
    private final BFSvisit<Index> bfsAlgo;
    private  final ThreadLocalDijkstraVisit<Index> dijkstraAlgo;
    private final Index index = new Index(0,0);
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
        String taskChoiceFromUser = objectInputStream.readObject().toString();
        while(doWork)
        {
            switch (taskChoiceFromUser)
            {
                case "1":
                {
                    task1(objectInputStream, objectOutputStream);
                    break;
                }
                case "2":
                {
                    task2(objectInputStream, objectOutputStream);
                    break;
                }
                case "3":
                {
                    task3(objectInputStream, objectOutputStream);
                    break;
                }
                case "4":
                {
                    task4(objectInputStream, objectOutputStream);
                    break;
                }
                case "stop":{
                    doWork = false;
                    break;
                }

            }

        }

    }
    private void task1(ObjectInputStream inputFromUser,ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {

        // get a matrix from client
        Matrix matrix = new Matrix((int[][])inputFromUser.readObject());
        traversableMatrix = new TraversableMatrix(matrix);
        traversableMatrix.setStartIndex(index);
        int row = matrix.primitiveMatrix.length;
        int col = matrix.primitiveMatrix[0].length;

        HashSet<HashSet<Index>> setOfComponents = new HashSet<>();
        for(int i = 0; i<row;i++)
        {
            for(int j = 0 ; j < col ; j++)
            {
                index.setRowAndCol(i,j);
                if(matrix.getValue(index) == 1)
                {
                    traversableMatrix.setStartIndex(new Index(i,j));

                    setOfComponents.add((HashSet<Index>) dfsAlgo.traverse(traversableMatrix));
                }
                System.out.println(setOfComponents);
            }
        }

        List componentResult = new LinkedList();
        Comparator<HashSet<Index>> lengthComparator = (component1,component2)->Integer.compare(component1.size(),component2.size());
        setOfComponents.stream().sorted(lengthComparator).forEach((i)->componentResult.add(i));
        outputToUser.writeObject(componentResult);
    }

    private void task2(ObjectInputStream inputFromUser,ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {
        // Get matrix
        Matrix matrix = new Matrix((int[][])inputFromUser.readObject());

        // Get source
        Index source = (Index)inputFromUser.readObject();

        // Get target
        Index dest = (Index)inputFromUser.readObject();

        // Get traversable
        traversableMatrix = new TraversableMatrix(matrix);

        // Initialize origin index
        traversableMatrix.setStartIndex(source);

        // Get shortest path
        List shortestPath = (List) bfsAlgo.traverse(new Node(dest),traversableMatrix);

        // Sending the result to the client
        outputToUser.writeObject(shortestPath);

    }

    private void task3(ObjectInputStream inputFromUser,ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException,ClassCastException {
        int[][] matrixFromUser = (int[][])inputFromUser.readObject();
        Matrix matrix = new Matrix(matrixFromUser);
        traversableMatrix = new TraversableMatrix(matrix);
        int row = matrix.primitiveMatrix.length;
        int col = matrix.primitiveMatrix[0].length;
        HashSet<HashSet<Index>> setOfComponents = new HashSet<>();
        for(int i = 0; i<row;i++)
        {
            for(int j = 0 ; j < col ; j++)
            {
                index.setRowAndCol(i,j);
                if(matrix.getValue(index) == 1)
                {
                    int count =0;
                    traversableMatrix.setStartIndex(new Index(i,j));
                    HashSet<Index> set  = (HashSet<Index>) dfsAlgo.traverse(traversableMatrix);
                    Index Max=traversableMatrix.startIndex,Min=traversableMatrix.startIndex ;
                    for (Index element:
                         set) {
                        count++;
                        if (element.compareTo(Max)==1){
                            Max = element;
                        }
                        if (element.compareTo(Min)<=0){
                            Min = element;
                        }
                    }

                    int maxR = Max.getRow()+1, maxC = Max.getColumn()+1;
                    int minR = Min.getRow(), minC = Min.getColumn();
                    if(((maxR-minR) * (maxC-minC))==count){
                        setOfComponents.add(set);
                    }

                }
            }
        }
        int size = setOfComponents.size();
        System.out.println(size);
        System.out.println(setOfComponents);
        outputToUser.writeObject(size);
    }

    private void task4(ObjectInputStream inputFromUser,ObjectOutputStream outputToUser) throws IOException, ClassNotFoundException {
        // Get matrix
        Matrix matrix = new Matrix((int[][])inputFromUser.readObject());

        // Get source
        Index source = (Index)inputFromUser.readObject();

        // Get target
        Index dest = (Index)inputFromUser.readObject();

        // Get traversable
        traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);

        // Initialize origin index
        traversableWeightedMatrix.setStartIndex(source);

        // Get shortest path
        List shortestPath = (List) dijkstraAlgo.traverse(new Node(dest),traversableWeightedMatrix);

        System.out.println(shortestPath);
//        // Sending the result to the client
//        outputToUser.writeObject(shortestPath);
    }
}
