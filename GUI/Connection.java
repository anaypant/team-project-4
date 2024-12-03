package GUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Connection {
    private static final String SERVER_HOST_NAME = "localhost";
    private static final int PORT_NUMBER = 4343; // Replace with your actual port number

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public Connection() throws Exception {
        try {
            // Connect to the server
            socket = new Socket(SERVER_HOST_NAME, PORT_NUMBER);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Start a timer to poll for server responses

            // Since we're in the login screen, rely on server to send any messages if needed
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to connect to server");
        }

    }

    public void println(String message) {
        out.println(message);
    }

    public boolean ready() throws IOException {
        return this.in.ready();
    }

    public String readLine() throws IOException {
        return in.readLine();
    }
}
