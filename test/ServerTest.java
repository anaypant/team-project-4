package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import src.Constants;
import src.Server;
import src.PostDBDatabase;
import src.UserDBDatabase;

import java.io.IOException;
import java.lang.constant.Constable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Test file for Server class setup and basic functionality.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 17th, 2024
 **/
public class ServerTest {
    private static final String USER_DB_PATH = Constants.USER_DB;
    private static final String POST_DB_PATH = Constants.POST_DB;
    private Server server;
    private ServerSocket serverSocket;


    @Before
    public void setUp() {
        try (Connection userConn = DriverManager.getConnection(USER_DB_PATH);
             Statement userStmt = userConn.createStatement();
             Connection postConn = DriverManager.getConnection(POST_DB_PATH);
             Statement postStmt = postConn.createStatement()) {

            // Reset the users table
            userStmt.execute("DELETE FROM users");
            System.out.println("All rows deleted from users table.");

            // Reset the posts table
            postStmt.execute("DELETE FROM posts");
            System.out.println("All rows deleted from posts table.");

            serverSocket = new ServerSocket(Constants.PORT_NUMBER + 1); // Use a random
            // available port
            Socket socket = new Socket("localhost", Constants.PORT_NUMBER + 1);
            server = new Server(socket);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Closes the server after each run
    @After
    public void cleanUp() {
        // check if server is running and close it
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testServerSetup() {
        assertNotNull("Server instance should not be null", server);
        assertNotNull("Server socket should be initialized", serverSocket);
    }

    @Test
    public void testBasicCommandProcessing() {
        // Simulate basic server commands via database interactions
        UserDBDatabase.createUser("testUser", "password123");
        assertNotNull("User creation should succeed", UserDBDatabase.loginUser("testUser", "password123"));

        PostDBDatabase.createPost("testUser", "Test Caption", "http://test.url");
        assertFalse("Post list should not be empty", PostDBDatabase.getPostsByUsername("testUser").isEmpty());
    }
}
