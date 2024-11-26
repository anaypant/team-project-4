package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import src.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Unit tests for server-client interactions in the SocialMedia application.
 *
 * @author CS180 L2 Team 5
 *
 * @version November 17th, 2024
 **/
public class SocialMediaTest {
    private ServerSocket serverSocket; // Simulates the server
    private Socket clientSocket;       // Simulates the client
    private PrintWriter serverWriter;  // Writes responses to the client
    private BufferedReader serverReader; // Reads input from the client

    @Before
    public void setUp() {
        try {
            serverSocket = new ServerSocket(Constants.PORT_NUMBER + 1);
            Thread serverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        clientSocket = serverSocket.accept();
                        serverWriter = new PrintWriter(clientSocket.getOutputStream(),
                                true);
                        serverReader = new BufferedReader(new InputStreamReader(
                                clientSocket.getInputStream()));
                    } catch (Exception e) {

                    }
                }
            });
            serverThread.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up server: " + e.getMessage(), e);
        }
    }

    @After
    public void tearDown() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConnectionToServer() {
        try {
            Socket client = new Socket("localhost", Constants.PORT_NUMBER + 1);
            assertNotNull("Client should connect to the server successfully", client);
            client.close();
        } catch (Exception e) {
            fail("Client failed to connect to the server: " + e.getMessage());
        }
    }

    @Test
    public void testSendAndReceiveMessage() {
        try {
            Socket client = new Socket("localhost", Constants.PORT_NUMBER + 1);
            PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true);
            BufferedReader clientReader = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            Thread serverResponseThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String receivedMessage = serverReader.readLine();
                        assertEquals(
                                "Server should receive the correct message",
                                "test message", receivedMessage);

                        serverWriter.println("Server response");
                        serverWriter.println("EOM"); // End of message marker
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            serverResponseThread.start();

            clientWriter.println("test message");

            StringBuilder response = new StringBuilder();
            String line = clientReader.readLine();
            while (line != null && !line.equals("EOM")) {
                response.append(line).append("\n");
                line = clientReader.readLine();
            }

            assertEquals("Client should receive the correct response",
                    "Server response\n", response.toString());

            client.close();
        } catch (Exception e) {
            fail("Send/receive test failed: " + e.getMessage());
        }
    }
}
