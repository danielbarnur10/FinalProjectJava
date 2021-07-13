import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatrixIHandler implements IHandler{
    protected final ThreadLocalDfsVisit<Index> matrixTravesable;
    protected final DFSvisit<Index> matrixDFS;//added for task 2
    private Matrix matrix;
    private boolean doWork = true;
    private ThreadPoolExecutor threadPoolExecutor;

    public MatrixIHandler() {
        this.threadPoolExecutor = new ThreadPoolExecutor(3,5,
                10, TimeUnit.MICROSECONDS,new LinkedBlockingQueue<>());
        this.matrixTravesable= new ThreadLocalDfsVisit<Index>();
        this.matrixDFS = new DFSvisit<Index>();//added for task 2
    }

    @Override
    public void handle(InputStream inputFromUser, OutputStream inputToUser) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputFromUser);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(inputToUser);

        while(doWork)
        {
            switch (objectInputStream.readObject().toString())
            {
                case "matrix":
                {
                    int[][]tempArray = (int[][])objectInputStream.readObject();
                    System.out.println("Server: Got 2d array from client!");
                    this.matrix = new Matrix(tempArray);
                    this.matrix.printMatrix();
                    System.out.println("Matrix received");
                    break;
                }
                case "1":
                {
                    /**
                     *  getting the input of 2d array and printing all scc groups (with diagonal) using DFS */
                    if(this.matrix!=null) {


                        TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);
                        int lengthRow = this.matrix.getPrimitiveMatrix().length;
                        int lengthCol = this.matrix.getPrimitiveMatrix()[0].length;
                        Index index = null;
                        HashSet<HashSet<Index>> SetConnectComponents = new HashSet<>();
                        HashSet<Index> visited = new HashSet<>();
                        int[][] matrix2 = matrix.primitiveMatrix;
                        for (int i = 0; i < lengthRow ; i++) {
                            for (int j = 0; j < lengthCol; j++) {
                                index = new Index(i,j);
                                if(matrix2[i][j] == 1 && !visited.contains(index))
                                {
                                    traversableMatrix.startIndex = index;

                                    SetConnectComponents.add((HashSet<Index>) matrixTravesable.traverse(visited, traversableMatrix));

                                }
                            }
                        }

                        HashSet<Index> Task1 = new HashSet<>();
                        Comparator<HashSet<Index>> lengthComparator = (component1,component2)->Integer.compare(component1.size(),component2.size());
                        SetConnectComponents.stream().sorted(lengthComparator).map(((e)->e.toArray())).forEach(System.out::println);
                        System.out.println("DFS returned:"+ SetConnectComponents);
                          break;
                     }
                }
                case "2":{

                    Index source = (Index)objectInputStream.readObject();
                    Index dest = (Index)objectInputStream.readObject();
                    if(matrix.getValue(dest)==0 ||matrix.getValue(source)==0)
                    {
                        System.out.println("Give me index with value 1!");
                        break;
                    }

                    /* we first start with startindex source.
                    *then we go on the matrix with dfs we receive the scc object
                    * then we search for the index dest
                    *
                    * else:
                    * we have startindex then we use it with improved dfs if we receive the dest
                    * we break and send back the
                    * object scc
                    */

                    TraversableMatrix traversableMatrix = new TraversableMatrix(matrix);
                    HashSet<HashSet<Index>> SetConnectComponents = new HashSet<>();
                    traversableMatrix.startIndex = source;
                    SetConnectComponents.add((HashSet<Index>) matrixDFS.traverse(traversableMatrix));//not localthread
                    if(SetConnectComponents.contains(dest)){


                    }
                    System.out.println("Dijkstra returned:"+ SetConnectComponents);
                    break;
                  }

                case "stop":
                    doWork = false;
                    break;
            }
        }
    }
}

