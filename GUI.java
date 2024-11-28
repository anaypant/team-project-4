import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * A GUI client for the social media application with a login screen.
 * Displays only server messages to the user without adding any hardcoded messages.
 * After successful login, it shows the main application with additional commands and the feed.
 *
 * @version 1.4
 */
public class GUI extends JFrame {
    private static final String SERVER_HOST_NAME = "localhost";
    private static final int PORT_NUMBER = 4343; // Replace with your actual port number

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Main panels
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login panel components
    private JPanel loginPanel;
    private JButton createUserButton;
    private JButton loginUserButton;
    private JTextArea loginDisplayArea; // Display server messages on login screen

    // Main application panel components
    private JPanel appPanel;
    private JTextArea displayArea;
    private JTextField inputField;
    private JButton sendButton;

    // Command buttons
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

    private String activeUser; // To keep track of the logged-in user
    private String currentCard = "Login"; // Track the currently displayed card

    public GUI() {
        // Initialize the GUI components
        setTitle("Social Media App");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        connectToServer();
    }

    private void initComponents() {
        // Use CardLayout to switch between login screen and main application
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize login panel
        initLoginPanel();

        // Initialize main application panel
        initAppPanel();

        // Add panels to main panel
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(appPanel, "App");

        // Show login panel initially
        cardLayout.show(mainPanel, "Login");
        currentCard = "Login"; // Set the current card

        // Add main panel to frame
        add(mainPanel);
    }

    private void initLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create User button
        createUserButton = new JButton("Create User");
        createUserButton.setPreferredSize(new Dimension(150, 30));
        createUserButton.addActionListener(e -> createUser());

        // Login User button
        loginUserButton = new JButton("Login User");
        loginUserButton.setPreferredSize(new Dimension(150, 30));
        loginUserButton.addActionListener(e -> loginUser());

        // Add buttons to the button panel
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(createUserButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(loginUserButton, gbc);

        // Add button panel to the top of login panel
        loginPanel.add(buttonPanel, BorderLayout.NORTH);

        // Login display area to show server messages
        loginDisplayArea = new JTextArea();
        loginDisplayArea.setEditable(false);
        JScrollPane loginScrollPane = new JScrollPane(loginDisplayArea);
        loginPanel.add(loginScrollPane, BorderLayout.CENTER);
    }

    private void initAppPanel() {
        appPanel = new JPanel(new BorderLayout());

        // Display area for server responses
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        appPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add action listener for the send button
        sendButton.addActionListener(e -> sendMessage());

        // Add input panel to the app panel
        appPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel for command buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        createPostButton = new JButton("Create Post");
        selectPostButton = new JButton("Select Post");
        upvoteButton = new JButton("Upvote");
        downvoteButton = new JButton("Downvote");
        commentButton = new JButton("Comment");
        logoutButton = new JButton("Logout");

        // Add action listeners for command buttons
        createPostButton.addActionListener(e -> createPost());
        selectPostButton.addActionListener(e -> selectPost());
        upvoteButton.addActionListener(e -> sendCommand("upvote"));
        downvoteButton.addActionListener(e -> sendCommand("downvote"));
        commentButton.addActionListener(e -> commentOnPost());
        logoutButton.addActionListener(e -> logout());

        // Add buttons to the panel
        buttonPanel.add(createPostButton);
        buttonPanel.add(selectPostButton);
        buttonPanel.add(upvoteButton);
        buttonPanel.add(downvoteButton);
        buttonPanel.add(commentButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the app panel
        appPanel.add(buttonPanel, BorderLayout.NORTH);
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

            // Since we're in the login screen, append message to loginDisplayArea
            // Remove hardcoded message, rely on server to send any messages if needed
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to server.", "Error",
                    JOptionPane.ERROR_MESSAGE);
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
                processServerResponse(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection to server lost.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            responseTimer.stop();
        }
    }

    private void processServerResponse(String response) {
        // Check if we're on the login screen or the app screen
        if (currentCard.equals("Login")) {
            // We're on the login screen
            if (response.contains("Login successful.")) {
                // Show the app panel
                cardLayout.show(mainPanel, "App");
                currentCard = "App"; // Update current card

                // Display the server's response in the main display area
                displayArea.append(response);
                displayArea.setCaretPosition(displayArea.getDocument().getLength());
            } else {
                // Display server messages on the login screen
                loginDisplayArea.append(response);
                loginDisplayArea.setCaretPosition(loginDisplayArea.getDocument().getLength());
            }
        } else {
            // We're on the app screen
            // Append server responses to the display area
            displayArea.append(response);
            displayArea.setCaretPosition(displayArea.getDocument().getLength());
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
            responseTimer.stop();
            // Remove hardcoded message, rely on server to send any messages if needed
            // Reset the GUI to the login screen
            cardLayout.show(mainPanel, "Login");
            currentCard = "Login"; // Update current card
            displayArea.setText("");
            loginDisplayArea.setText("");
            activeUser = null;
            connectToServer(); // Reconnect to the server for a new session
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
            activeUser = usernameField.getText(); // Store the active user
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
