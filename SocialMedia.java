import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class SocialMedia {
    public static void main(String[] args) {
        try {
            // Specify the server's hostname and port number
            String serverHostname = Constants.SERVER_HOST_NAME;
            int portNumber = Constants.PORT_NUMBER;

            // Create a socket to connect to the server
            Socket socket = new Socket(serverHostname, portNumber);
            System.out.println("Connected to server on port " + portNumber);

            // Set up input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Loop for continuous input and output
            System.out.println("Welcome to the social media app. Please enter a command: ");

            while (true) {
                System.out.print("You: ");
                String msg = scanner.nextLine();

                // Exit condition
                if (msg.equalsIgnoreCase("exit")) {
                    break;
                }

                // Send message to the server
                out.println(msg);

                // Read response from server
                String response = in.readLine();
                System.out.println(response);
            }

            // Close resources
            scanner.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
