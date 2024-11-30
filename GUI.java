import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.awt.datatransfer.DataFlavor;
import javax.swing.TransferHandler;
import java.util.List;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;

/**
 * A GUI client for the social media application with a login screen.
 * Displays only server messages to the user without adding any hardcoded messages.
 * After successful login, it shows the main application with additional commands and the feed.
 *
 * @version 1.5
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
    private JTextPane loginDisplayArea; // Changed to JTextPane

    // Main application panel components
    private JPanel appPanel;
    private JTextPane displayArea; // Changed to JTextPane
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
        loginDisplayArea = new JTextPane(); // Changed to JTextPane
        loginDisplayArea.setEditable(false);
        JScrollPane loginScrollPane = new JScrollPane(loginDisplayArea);
        loginPanel.add(loginScrollPane, BorderLayout.CENTER);
    }

    private void initAppPanel() {
        appPanel = new JPanel(new BorderLayout());

        // Display area for server responses
        displayArea = new JTextPane(); // Changed to JTextPane
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

            // Since we're in the login screen, rely on server to send any messages if needed
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
            try {
                if (response.contains("Login successful.")) {
                    // Switch to the main application screen
                    cardLayout.show(mainPanel, "App");
                    currentCard = "App"; // Update current card

                    // Clear the login display area (optional)
                    loginDisplayArea.setText("");

                    // Display the server's response in the main display area
                    StyledDocument doc = displayArea.getStyledDocument();
                    doc.insertString(doc.getLength(), response, null);
                    displayArea.setCaretPosition(doc.getLength());
                } else {
                    // Display server messages on the login screen
                    StyledDocument doc = loginDisplayArea.getStyledDocument();
                    doc.insertString(doc.getLength(), response, null);
                    loginDisplayArea.setCaretPosition(doc.getLength());
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            // We're on the app screen
            StyledDocument doc = displayArea.getStyledDocument();

            try {
                // Split the response into lines to handle multiple messages
                String[] lines = response.split("\n");
                for (String line : lines) {
                    if (line.startsWith("IMAGE_URL:")) {
                        String imageUrl = line.substring("IMAGE_URL:".length()).trim();

                        // Load image from URL
                        URL url = new URL(imageUrl);
                        ImageIcon imageIcon = new ImageIcon(url);

                        // Optionally, scale the image to fit the display area
                        Image image = imageIcon.getImage();
                        Image scaledImage = image.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(scaledImage);

                        JLabel imageLabel = new JLabel(imageIcon);

                        // Insert a newline before the image
                        doc.insertString(doc.getLength(), "\n", null);

                        // Insert the image component
                        displayArea.setCaretPosition(doc.getLength());
                        displayArea.insertComponent(imageLabel);

                        // Insert a newline after the image
                        doc.insertString(doc.getLength(), "\n", null);
                    } else {
                        // Append the text response
                        doc.insertString(doc.getLength(), line + "\n", null);
                    }
                }
                displayArea.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    doc.insertString(doc.getLength(), "Failed to load image from URL.\n", null);
                    displayArea.setCaretPosition(doc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
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
        // Dialog to input post content and accept image drop
        JPanel panel = new JPanel(new BorderLayout());

        // Text area for post content
        JTextArea postContentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(postContentArea);
        panel.add(new JLabel("Post Content:"), BorderLayout.NORTH);
        panel.add(contentScrollPane, BorderLayout.CENTER);

        // Panel for image drop
        JPanel imageDropPanel = new JPanel();
        imageDropPanel.setBorder(BorderFactory.createTitledBorder("Drag and drop an image here"));
        imageDropPanel.setPreferredSize(new Dimension(200, 200));
        panel.add(imageDropPanel, BorderLayout.SOUTH);

        // Variable to hold the image file
        final File[] imageFile = new File[1];

        // Enable drag and drop on the image panel
        imageDropPanel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    List<File> files = (List<File>) support.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.size() > 0) {
                        File file = files.get(0);
                        // Check if the file is an image
                        if (isImageFile(file)) {
                            imageFile[0] = file;
                            // Display the image in the panel (optional)
                            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                            JLabel imageLabel = new JLabel(icon);
                            imageDropPanel.removeAll();
                            imageDropPanel.add(imageLabel);
                            imageDropPanel.revalidate();
                            imageDropPanel.repaint();
                            return true;
                        } else {
                            JOptionPane.showMessageDialog(panel, "Please drop an image file.");
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Post",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create post commands to server
            out.println("create post");

            // Send post content
            out.println(postContentArea.getText());

            // Send image data
            if (imageFile[0] != null) {
                try {
                    // Read the image file into a byte array
                    byte[] imageBytes = Files.readAllBytes(imageFile[0].toPath());

                    // Encode the image bytes as a Base64 string
                    String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                    // Send a flag indicating that an image is included
                    out.println("IMAGE_ATTACHED");

                    // Send the encoded image
                    out.println(encodedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to read the image file.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Indicate that no image is attached
                out.println("NO_IMAGE");
            }
        }
    }

    // Helper method to check if the file is an image
    private boolean isImageFile(File file) {
        String[] imageExtensions = { "png", "jpg", "jpeg", "gif", "bmp" };
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
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
