package GUI;

import java.io.IOException;

/**
 * Interface that serves as blueprint for the Connection class
 * Skeleton methods for printing messages to the server, reading lines from the server
 */

public interface ConnectionInterface {
    //constants for the server host name and port number used to connect to server
    String SERVER_HOST_NAME = "localhost";
    int PORT_NUMBER = 4343;

    // method used to write to the server
    void println(String message);

    //method that makes sure we are ready to right to the server
    boolean ready() throws IOException;

    //method the reads the lines from the server so the server and client can interact
    String readLine() throws IOException;
}
