import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static src.Constants.PORT_NUMBER;
import static src.Constants.SERVER_HOST_NAME;

public class GUI {
    private JButton createUserButton; // Declare the button as a class field

    public GUI() {
        createUserButton = new JButton("Create User"); // Initialize the button
    }

    public static void main(String[] args) {
        try {
            // Establish a connection to the server
            Socket socket = new Socket(SERVER_HOST_NAME, PORT_NUMBER);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Create the GUI object
            GUI gui = new GUI();

            // Create the main frame
            JFrame frame = new JFrame("Social Media");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            // Create the CardLayout
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Screen 1
            JPanel screen1 = new JPanel();
            screen1.setBackground(Color.CYAN);
            JButton goToScreen2 = new JButton("Go to Screen 2");
            screen1.add(goToScreen2);

            // Add the Create User button to screen1
            screen1.add(gui.createUserButton);

            // Screen 2
            JPanel screen2 = new JPanel();
            screen2.setBackground(Color.ORANGE);
            JButton goToScreen1 = new JButton("Go to Screen 1");
            screen2.add(goToScreen1);

            // Add screens to the main panel
            mainPanel.add(screen1, "Screen1");
            mainPanel.add(screen2, "Screen2");

            // Add action listeners for screen navigation
            goToScreen2.addActionListener(new ScreenSwitchAction(cardLayout, mainPanel, "Screen2"));
            goToScreen1.addActionListener(new ScreenSwitchAction(cardLayout, mainPanel, "Screen1"));

            // Add action listener for the Create User button
            gui.createUserButton.addActionListener(new CreateUserAction(frame, out, in));

            // Add the main panel to the frame
            frame.add(mainPanel);

            // Display the frame
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing GUI: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ActionListener for switching screens
    static class ScreenSwitchAction implements ActionListener {
        private CardLayout cardLayout;
        private JPanel mainPanel;
        private String targetScreen;

        public ScreenSwitchAction(CardLayout cardLayout, JPanel mainPanel, String targetScreen) {
            this.cardLayout = cardLayout;
            this.mainPanel = mainPanel;
            this.targetScreen = targetScreen;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(mainPanel, targetScreen);
        }
    }

    // ActionListener for creating a user
    static class CreateUserAction implements ActionListener {
        private JFrame frame;
        private PrintWriter out;
        private BufferedReader in;

        public CreateUserAction(JFrame frame, PrintWriter out, BufferedReader in) {
            this.frame = frame;
            this.out = out;
            this.in = in;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Send "create user" command to the server
                out.println("create user");
                out.flush();

                boolean interactionComplete = false; // Track end of interaction

                while (!interactionComplete) {
                    String serverPrompt = in.readLine();
                    if (serverPrompt == null) {
                        JOptionPane.showMessageDialog(frame, "Server disconnected.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (serverPrompt.equalsIgnoreCase("EOM")) {
                        // End of interaction
                        interactionComplete = true;
                        break;
                    }

                    // Show input dialog for server prompt
                    String userInput = JOptionPane.showInputDialog(frame, serverPrompt,
                            "User Creation", JOptionPane.QUESTION_MESSAGE);

                    if (userInput == null || userInput.trim().isEmpty()) {
                        // User canceled
                        JOptionPane.showMessageDialog(frame, "User creation canceled.",
                                "Canceled", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Send user input to the server
                    out.println(userInput);
                    out.flush();

                    // Process server's response
                    String serverResponse = in.readLine();
                    if (serverResponse != null) {
                        if (serverResponse.equalsIgnoreCase("User created successfully")) {
                            // Show success message dialog
                            JOptionPane.showMessageDialog(frame, serverResponse,
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            interactionComplete = true; // Mark interaction as complete
                            break;
                        } else if (serverResponse.equalsIgnoreCase("Password is too short.") ||
                                serverResponse.equalsIgnoreCase("User creation failed.")) {
                            // Show error message
                            JOptionPane.showMessageDialog(frame, serverResponse,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error communicating with server: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
