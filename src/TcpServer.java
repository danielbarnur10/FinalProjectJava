import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TcpServer {
    /*
    Motivation: 2 running programs on remote hosts wish to communicate with each other over a network connection
    How is it done?
    Application layer only translates packets to meaningful data. How is it sent?

    Transport layer:
    End-to-end Connection between hosts for applications.
    It provides services such as:
    connection-oriented communication, Data integrity and Error correction, flow and congestion control

    Most importantly:
    1. Process to process delivery:
    A program wishes to send messages to and receive messages from the program on the other end.
    an instance of a running program is represented by a process, for which resources
    are allocated by the operating system

   application layer believe processes on hosts are connected by a data stream

    multiplexing -  of different applications over a network which is running on a host.
    The transport layer enables sending packet streams from various applications simultaneously over a network.

    A socket is an endpoint for communication between two machines.
    In application layer, each running program is represented by a process that executes
    operations
    There are 2 kinds of sockets:
    1. Server Socket - listen and accept incoming connection
    2. Operational Socket (known as a Client Socket) - read from/write to a data stream

    Each socket is associated with a transport protocol (TCP/UDP) and local information (ip and port).
    Port is used to distinguish between different types of data transmitted over the same data-line

    Real-life example:
    enter a url: https://www.ynet.co.il in chrome's address bar and press enter.
    1. we want to send an HTTP GET request and get ynet's homepage
    2. no ip address in cache
    3. figure out what is the ip address of ynet
    4. chrome needs to send a DNS request (A-Record) to figure out ynet's ip address.
    DNS Request is done in application layer
    5. DNS Request is done over either UDP/TCP.
    6. chrome needs to ask the operating system to open UDP socket
    7. after UDP socket creation bound to DNS protocol number (port), chrome send DNS request
    8. chrome receives ip address associated with ynet.
    9. chrome again needs services from OS - open TCP socket
    10. only now can chrome send the HTTP GET request

    How are sockets involved in the process?
    What is the role of transport layer?
     */
    /*
    Server:
    1. create a server socket
    2. Bind to a port number.
    3. Listens to incoming connections from clients
    4. If a request is accepted, return an operational socket for that specific connection
    5. Handle each client in a separate thread
    6. Extract tasks from input stream
    7. Tasks are handled according to a concrete strategy
    8. Result is written using socket's OutputStream
     */

    private final int port;
    private volatile boolean stopServer;
    private ThreadPoolExecutor threadPool;
    private IHandler requestHandler;

    public TcpServer(int port) {
        this.port = port;
        // initialize data members (although they are initialized by default)
        stopServer = false;
        threadPool = null;
        requestHandler = null;
    }

    // listen to incoming connections, accept if possible and handle clients
    public void supportClients(IHandler concreteHandler) {
        this.requestHandler = concreteHandler;

        new Thread(() -> {
            // lazy loading
            threadPool = new ThreadPoolExecutor(3, 5, 10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            try {
                /*
                 if no port is specified - one will be automatically allocated by OS
                 backlog parameter- number of maximum pending requests
                 ServerSocket constructor - socket creation + bind to a specific port
                 Server Socket API:
                 1. create socket
                 2. bind to a specific port number
                 3. listen for incoming connections (a client initiates a tcp connection with server)
                 4. try to accept (if 3-way handshake is successful)
                 5. return operational socket (2 way pipeline)
                 */
                ServerSocket serverSocket = new ServerSocket(port);
                while (!stopServer) {
                    Socket serverToSpecificClient = serverSocket.accept(); // 2 operations: listen()+accept()
                /*
                 server will handle each client in a separate thread
                 define every client as a Runnable task to execute
                 */
                    Runnable clientHandling = () -> {
                        try {
                            requestHandler.handle(serverToSpecificClient.getInputStream(),
                                    serverToSpecificClient.getOutputStream());
                            // finished handling client. now close all streams
                            serverToSpecificClient.getInputStream().close();
                            serverToSpecificClient.getOutputStream().close();
                            serverToSpecificClient.close();
                        } catch (IOException | ClassNotFoundException ioException) {
                            System.err.println(ioException.getMessage());
                        }
                    };

                    threadPool.execute(clientHandling);
                }
                serverSocket.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
    }
        public void stop(){
            if(!stopServer)
                stopServer = true;
            if(threadPool!=null) threadPool.shutdown();
        }

        public void jvmInfo(){
            System.out.println(ProcessHandle.current().pid());
            System.out.println(Runtime.getRuntime().maxMemory());
    }

    public static void main(String[] args) {
        TcpServer matrixServer = new TcpServer(8010);
        matrixServer.supportClients(new MatrixIHandler());

    }

}


