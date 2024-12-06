package GUI;

import java.io.*;
import java.net.Socket;

/**
 * Class to handle the Server/GUI Connection.
 * Managed by SocialMedia Class
 *
 * @author CS180 Team 5
 * @version 1
 */

public class Connection implements ConnectionInterface {

    // Sockets, writers, readers to use to write/read from server
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    // Creates a connection
    public Connection() throws Exception {
        try {
            // Connect to the server
            socket = new Socket(SERVER_HOST_NAME, PORT_NUMBER);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            // Server did not connect
            e.printStackTrace();
            throw new Exception("Failed to connect to server");
        }

    }

    // Sends a message to the server
    public void println(String message) {
        out.println(message);
        out.flush();
    }

    // Checks if a new message has been sent from the server
    public boolean ready() throws IOException {
        return this.in.ready();
    }

    // Reads a line from the server
    public String readLine() throws IOException {
        return in.readLine();
    }
}
