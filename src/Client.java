import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket =new Socket("127.0.0.1",8010);
        System.out.println("client: Created Socket");

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream toServer=new ObjectOutputStream(outputStream);
        ObjectInputStream fromServer=new ObjectInputStream(inputStream);

        // sending #1 matrix
        final int[][] matrix = {
                {1, 0, 1, 1},
                {1, 0, 1, 1},
                {1, 0, 0, 1}

            //1,0));
            // (1,2));
        };
        PrintArr(matrix);

//        task1(toServer,fromServer,matrix);
//        task2(toServer,fromServer,matrix);
//        task3(toServer,fromServer,matrix);

        final int[][] matrix2 = {
                {100, 100, 100, 100},
                {500, 300, 200, 100},
                {100, 100, 100, 100}

                //1,0));
                // (1,2));
        };
        task4(toServer,fromServer,matrix2);

        CloseConnection(socket,toServer,fromServer);
    }



    public static void task1(ObjectOutputStream toServer,ObjectInputStream fromServer,int[][]matrix) throws IOException, ClassNotFoundException {
        toServer.writeObject("1");
        // client send matrix
        toServer.writeObject(matrix);
        List result =
                new LinkedList((List<Index>) fromServer.readObject());
        // display result
        System.out.println("The connected components of matrix is:");
        PrintPaths(result);
        System.out.println("Client finished Task 1");

    }

    public static void task2( ObjectOutputStream toServer,ObjectInputStream fromServer,int[][]matrix) throws IOException, ClassNotFoundException {
        toServer.writeObject("2");
        // client send matrix
        toServer.writeObject(matrix);
        // initialize source index
        Index source = new Index(0,0);
        // initialize target index
        Index dest = new Index(3,3);
        // client send source index
        toServer.writeObject(source);
        // client send target index
        toServer.writeObject(dest);
        // get shortest paths from server
        List result =
                new LinkedList((List<Index>) fromServer.readObject());
        // display result
        System.out.println("The Shortest Paths from " + source + " to " + dest + " is:");
        PrintPaths(result);
        System.out.println("Client finished Task 2");
    }

    private static void task3(ObjectOutputStream toServer, ObjectInputStream fromServer, int[][] matrix)throws IOException, ClassNotFoundException {
        toServer.writeObject("3");
        // client send matrix
        toServer.writeObject(matrix);
        int result = (int)fromServer.readObject();
        // display result
        System.out.println("Number of submarines: " + result);
        System.out.println("Client finished Task 3");
    }

    private static void task4(ObjectOutputStream toServer, ObjectInputStream fromServer, int[][] matrix) throws IOException, ClassNotFoundException {
        toServer.writeObject("4");
        toServer.writeObject(matrix);
        toServer.writeObject(new Index(1,0));
        toServer.writeObject(new Index(1,2));
        int result = (int)fromServer.readObject();
        // display result
         System.out.println("The connected components of matrix is:");
            System.out.println(result);
           System.out.println("Client finished Task 3");
    }

    public static void PrintArr(int[][] arr)
    {
        for (int i = 0 ; i < arr.length ; i++)
        {
            for(int j = 0 ; j < arr[0].length;j++)
            {
                System.out.print("[ " + arr[i][j] + " ]");
            }
            System.out.println();
        }
    }

    public static void PrintPaths(List paths)
    {
        for(var path : paths)
        {
            System.out.println(path);
        }
    }

    public static void CloseConnection(Socket socket,ObjectOutputStream toServer,ObjectInputStream fromServer) throws IOException {
        toServer.writeObject("stop");
        fromServer.close();
        toServer.close();
        socket.close();
    }

}