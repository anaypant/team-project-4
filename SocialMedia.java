import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


/**
 * A class that defines the menu where the user inputs commands for the social media platform.
 * Connects to the server via host name and port number (localhost and specified port number)
 * Interacts with Server in continuous loop, 'exit' to quit program
 * This is where users actually interact with the platform
 *
 * @author CS180 L2 Team 5
 *
 * @version 2.0
 **/

public class SocialMedia implements SocialMediaInterface {
    public static void main(String[] args) {
        try {
            // Specify the server's hostname and port number


            // Create a socket to connect to the server
            Socket socket = new Socket(SERVER_HOST_NAME, PORT_NUMBER);
            //JOptionPane.showMessageDialog(null, "Connected to server on port" + portNumber);
            System.out.println("Connected to server on port " + PORT_NUMBER);

            // Set up input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Loop for continuous input and output

            System.out.println("Welcome to the social media app. Please enter a command: ");

            while (true) {
                System.out.print("You: ");
                String msg = scanner.nextLine();
                System.out.println();

                // Exit condition
                if (msg.equalsIgnoreCase("exit")) {
                    break;
                }

                // Send message to the server
                out.println(msg);

                // Read the entire response from server
                StringBuilder response = new StringBuilder();
                String line = in.readLine();
                while (line != null && !line.equals("EOM")) {
                    response.append(line).append("\n");
                    line = in.readLine();
                }

                System.out.print(response + "\n");
            }

            // Close resources
            scanner.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}