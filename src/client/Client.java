package client;

import matrix.Index;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
                {1, 1, 0, 1},
                {1, 0, 0, 0},
                {1, 0, 1, 1},
                {1, 0, 1, 1}

        };

        final int[][] matrix2 = {
                {100, 100, 100, 100},
                {500, 300, 200, 100},
                {100, 100, 100, 100}

        };
        System.out.println("Binary matrix:");
        PrintArr(matrix);

        toServer.writeObject("matrix");
        toServer.writeObject(matrix);
        toServer.writeObject("weightedMatrix");
        toServer.writeObject(matrix2);

        task1(toServer,fromServer);
        System.out.println("###################################");

        task2(toServer,fromServer);
       System.out.println("###################################");

        task3(toServer,fromServer);
        System.out.println("###################################");

        System.out.println("Positive Weighted matrix:");
        PrintArr(matrix2);
        task4(toServer,fromServer);
        System.out.println("###################################");

        CloseConnection(socket,toServer,fromServer);
    }


    @SuppressWarnings("unchecked")
    public static void task1(ObjectOutputStream toServer,ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        toServer.writeObject("1");
        // client send matrix
        List<Set<Index>> result =
                new LinkedList((List<Index>) fromServer.readObject());
        // display result
        System.out.println("The connected components of matrix is:");
        PrintPaths(result);
        System.out.println("Client.Client finished Task 1");

    }
    @SuppressWarnings("unchecked")
    public static void task2( ObjectOutputStream toServer,ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        toServer.writeObject("2");
        // initialize source index
        Index source = new Index(0,0);
        // initialize target index
        Index dest = new Index(3,0);
        // client send source index
        toServer.writeObject(source);
        // client send target index
        toServer.writeObject(dest);
        // get shortest paths from server
        List result = new LinkedList((List<Index>) fromServer.readObject());
        // display result
        if(result.size() > 0)
        {
            System.out.println("The Shortest Paths from " + source + " to " + dest + " is:");
            PrintPaths(result);
        }
        else
        {
            System.out.println("No path!");
        }

        System.out.println("Client.Client finished Task 2");
    }

    private static void task3(ObjectOutputStream toServer, ObjectInputStream fromServer)throws IOException, ClassNotFoundException {
        toServer.writeObject("3");
        int result = (int)fromServer.readObject();
        // display result
        System.out.println("Number of submarines: " + result);
        System.out.println("Client.Client finished Task 3");
    }

    @SuppressWarnings("unchecked")
    private static void task4(ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        toServer.writeObject("4");
        toServer.writeObject(new Index(1,0));
        toServer.writeObject(new Index(1,2));
        Collection<List<Index>> result = (Collection<List<Index>>) fromServer.readObject();
        // display result
         System.out.println("The Lightest paths are:");
            System.out.println(result);
           System.out.println("Client.Client finished Task 4");
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