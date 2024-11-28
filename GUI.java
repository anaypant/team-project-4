import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * A GUI client for the social media application without using additional threads.
 * Allows users to interact with the server using a graphical interface.
 *
 * @author
 * @version 1.0
 */
public class GUI extends JFrame {
    private static final String SERVER_HOST_NAME = "localhost";
    private static final int PORT_NUMBER = 4343; // Replace with your actual port number

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JTextArea displayArea;
    private JTextField inputField;
    private JButton sendButton;

    // Command buttons
    private JButton createUserButton;
    private JButton loginUserButton;
    private JButton createPostButton;
    private JButton selectPostButton;
    private JButton upvoteButton;
    private JButton downvoteButton;
    private JButton commentButton;
    private JButton logoutButton;

    // Dialog fields
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField postContentField;
    private JTextField postUrlField;

    // Timer for polling server responses
    private Timer responseTimer;

    public GUI() {
        // Initialize the GUI components
        setTitle("Social Media App");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        connectToServer();
    }

    private void initComponents() {
        // Set up the main layout
        setLayout(new BorderLayout());

        // Display area for server responses
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add action listener for the send button
        sendButton.addActionListener(e -> sendMessage());

        // Add input panel to the frame
        add(inputPanel, BorderLayout.SOUTH);

        // Panel for command buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        createUserButton = new JButton("Create User");
        loginUserButton = new JButton("Login User");
        createPostButton = new JButton("Create Post");
        selectPostButton = new JButton("Select Post");
        upvoteButton = new JButton("Upvote");
        downvoteButton = new JButton("Downvote");
        commentButton = new JButton("Comment");
        logoutButton = new JButton("Logout");

        // Add action listeners for command buttons
        createUserButton.addActionListener(e -> createUser());
        loginUserButton.addActionListener(e -> loginUser());
        createPostButton.addActionListener(e -> createPost());
        selectPostButton.addActionListener(e -> selectPost());
        upvoteButton.addActionListener(e -> sendCommand("upvote"));
        downvoteButton.addActionListener(e -> sendCommand("downvote"));
        commentButton.addActionListener(e -> commentOnPost());
        logoutButton.addActionListener(e -> logout());

        // Add buttons to the panel
        buttonPanel.add(createUserButton);
        buttonPanel.add(loginUserButton);
        buttonPanel.add(createPostButton);
        buttonPanel.add(selectPostButton);
        buttonPanel.add(upvoteButton);
        buttonPanel.add(downvoteButton);
        buttonPanel.add(commentButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the frame
        add(buttonPanel, BorderLayout.NORTH);
    }

    private void connectToServer() {
        try {
            // Connect to the server
            socket = new Socket(SERVER_HOST_NAME, PORT_NUMBER);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Start a timer to poll for server responses
            responseTimer = new Timer(100, e -> pollServer());
            responseTimer.start();

            displayArea.append("Connected to server on port " + PORT_NUMBER + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            displayArea.append("Failed to connect to server.\n");
        }
    }

    private void pollServer() {
        try {
            // Check if data is available without blocking
            if (in.ready()) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null && !line.equals("EOM")) {
                    response.append(line).append("\n");
                }
                displayArea.append(response.toString());
                // Auto-scroll to the bottom
                displayArea.setCaretPosition(displayArea.getDocument().getLength());
            }
        } catch (IOException e) {
            e.printStackTrace();
            displayArea.append("Connection to server lost.\n");
            responseTimer.stop();
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);
            inputField.setText("");
        }
    }

    private void sendCommand(String command) {
        out.println(command);
    }

    private void logout() {
        sendCommand("exit");
        try {
            socket.close();
            displayArea.append("Disconnected from server.\n");
            responseTimer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUser() {
        // Dialog to input username and password
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create user commands to server
            out.println("create user");
            out.println(usernameField.getText());
            out.println(passwordField.getText());
        }
    }

    private void loginUser() {
        // Dialog to input username and password
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send login user commands to server
            out.println("login user");
            out.println(usernameField.getText());
            out.println(passwordField.getText());
        }
    }

    private void createPost() {
        // Dialog to input post content and URL
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Post Content:"));
        postContentField = new JTextField();
        panel.add(postContentField);
        panel.add(new JLabel("Image URL (optional):"));
        postUrlField = new JTextField();
        panel.add(postUrlField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Post",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create post commands to server
            out.println("create post");
            out.println(postContentField.getText());
            out.println(postUrlField.getText());
        }
    }

    private void selectPost() {
        // Input username to view posts from
        String username = JOptionPane.showInputDialog(this, "Enter username to view posts from:",
                "Select Post", JOptionPane.PLAIN_MESSAGE);

        if (username != null && !username.trim().isEmpty()) {
            out.println("select post");
            out.println(username.trim());
        }
    }

    private void commentOnPost() {
        String comment = JOptionPane.showInputDialog(this, "Enter your comment:",
                "Add Comment", JOptionPane.PLAIN_MESSAGE);

        if (comment != null && !comment.trim().isEmpty()) {
            out.println("comment");
            out.println(comment.trim());
        }
    }

    public static void main(String[] args) {
        // Set the Swing look and feel to match the system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Create and display the GUI
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }
}
