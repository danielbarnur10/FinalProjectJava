import java.io.*;
import java.net.Socket;
import java.util.*;


public class Client {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner= new Scanner(System.in);
        Socket socket =new Socket("127.0.0.1",8010);
        System.out.println("client: Socket was created");

        ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer=new ObjectInputStream(socket.getInputStream());

        // sending #1 matrix
        int[][] matrix = {
                {1,1,0,1,1},
                {0,1,1,1,1},
                {0,0,0,0,0},
                {0,0,1,0,0}
        };
        /**
         * TODO:
         * make sure to get a matrix before getting reachables,neighbors commands
         * prompt the user to enter one of the following commands: matrix,reachables,neighbors
         * if token is not valid, prompt the user to enter a valid command
         * if the user entered either reachables or neighbors command: get index according
         * to the following pattern: (rowNumber,columnNumber).
         * Hint: use split() method to split the input and create an Index object
         * use  Integer.parseInt() to convert a string into an integer number
         * validation of number (either floating point number or int is not mandatory)
         *
         * EXTRA:
         * get input arrays from file  using FileInputStream
         * https://docs.oracle.com/javase/7/docs/api/java/io/FileInputStream.html#:~:text=A%20FileInputStream%20obtains%20input%20bytes%20from%20a%20file%20in%20a%20file%20system.&text=FileInputStream%20is%20meant%20for%20reading,%2C%20FileDescriptor%20%2C%20FileOutputStream%20%2C%20Files.
         */


        /**
         * Sets the matrix to the server
         * */
        toServer.writeObject("matrix");
        toServer.writeObject(matrix);

        /**
         * Task 1: find 2d array reachables groups sorted from biggest to smallest group
         * */

        toServer.writeObject("1");
        System.out.println("Client finished Task 1");

       /**
        * Task 2: find all shortest paths from source to destination
        * Dijkstra
        * */
        toServer.writeObject("2");
        Index sourceIndex = new Index(0,0);
        System.out.println(sourceIndex);
        Index destinationIndex = new Index(0,4);
        toServer.writeObject(sourceIndex);
        System.out.println("client source index" + sourceIndex);

        toServer.writeObject(destinationIndex);
        System.out.println("Client finished Task 2");



        toServer.writeObject("stop");


        /**closing the streams because the system allocates memory for them
        * */
        fromServer.close();
        toServer.close();
        socket.close();
        System.out.println("Client: Closed operational socket");

    }
}
