package GUI;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class MainPage extends JFrame{
    private JPanel appPanel;
    private JTextPane displayArea; // Changed to JTextPane
    private JTextField inputField;
    private JButton sendButton;

    private JButton createPostButton;
    private JButton selectPostButton;
    private JButton logoutButton;
    private JButton addFriendButton;
    private JButton removeFriendButton;
    private JButton blockButton;
    private JButton removeSelfButton;


    private SocialMedia sm;


    public MainPage(SocialMedia sm){
        this.sm = sm;
        this.init();

    }

    public void init() {
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
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        createPostButton = new JButton("Create Post");
        selectPostButton = new JButton("Select Post");
        logoutButton = new JButton("Logout");

        addFriendButton = new JButton("Add Friend");
        removeFriendButton = new JButton("Remove Friend");
        blockButton = new JButton("Block Friend");
        removeSelfButton = new JButton("Delete Account");


        // Add action listeners for command buttons
        createPostButton.addActionListener(e -> createPost());
        selectPostButton.addActionListener(e -> selectPost());
        logoutButton.addActionListener(e -> logout());

        addFriendButton.addActionListener(e -> addFriend());
        removeFriendButton.addActionListener(e -> removeFriend());
        blockButton.addActionListener(e -> block());
        removeSelfButton.addActionListener(e -> removeSelf());


        // Add buttons to the panel
        buttonPanel.add(createPostButton);
        buttonPanel.add(selectPostButton);
        buttonPanel.add(logoutButton);

        buttonPanel.add(addFriendButton);
        buttonPanel.add(removeFriendButton);
        buttonPanel.add(blockButton);
        buttonPanel.add(removeSelfButton);

        // Add button panel to the app panel
        appPanel.add(buttonPanel, BorderLayout.NORTH);

        this.setContentPane(appPanel);
        if (sm.getActiveUser() == null) {
            this.setTitle("null");
        } else {
            this.setTitle(sm.getActiveUser());
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            sm.sendMessage(msg);
            inputField.setText("");
        }
    }

    private void createPost() {
        // Dialog to input post content and accept image upload
        JPanel panel = new JPanel(new BorderLayout());

        // Text area for post content
        JTextArea postContentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(postContentArea);
        panel.add(new JLabel("Post Content:"), BorderLayout.NORTH);
        panel.add(contentScrollPane, BorderLayout.CENTER);

        // Panel for upload button
        JPanel uploadPanel = new JPanel();
        uploadPanel.setBorder(BorderFactory.createTitledBorder("Upload an image"));
        JButton uploadButton = new JButton("Upload Image");
        JLabel imageLabel = new JLabel("No image selected");
        uploadPanel.add(uploadButton);
        uploadPanel.add(imageLabel);
        panel.add(uploadPanel, BorderLayout.SOUTH);

        // Variable to hold the selected image file
        final File[] imageFile = new File[1];

        // Add action listener for the upload button
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (isImageFile(selectedFile)) {
                    imageFile[0] = selectedFile;
                    imageLabel.setText("Selected: " + selectedFile.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a valid image file.",
                            "Invalid File", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Post",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create post commands to server
            sm.sendMessage("create post");

            // Send post content
            sm.sendMessage(postContentArea.getText());

            // Send image data
            if (imageFile[0] != null) {
                try {
                    // Read the image file into a byte array
                    byte[] imageBytes = Files.readAllBytes(imageFile[0].toPath());

                    // Encode the image bytes as a Base64 string
                    String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                    // Send a flag indicating that an image is included
                    sm.sendMessage("IMAGE_ATTACHED");

                    // Send the encoded image
                    sm.sendMessage(encodedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to read the image file.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Indicate that no image is attached
                sm.sendMessage("NO_IMAGE");
            }
        }
    }

    // Helper method to check if the file is an image
    private boolean isImageFile(File file) {
        String[] imageExtensions = {"png", "jpg", "jpeg", "gif", "bmp"};
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public StyledDocument getStyledDocument() {
        return displayArea.getStyledDocument();
    }

    public void setCaretPosition(int position) {
        displayArea.setCaretPosition(position);
    }

    private void selectPost() {
        // Input username to view posts from
        String username = JOptionPane.showInputDialog(this, "Enter username to view posts from:",
                "Select Post", JOptionPane.PLAIN_MESSAGE);

        if (username != null && !username.trim().isEmpty()) {
            sm.sendMessage("select post");
            sm.sendMessage(username.trim());
        }
    }

    private void logout() {
        try {
            displayArea.setText("");
            sm.reset(false);
        } catch (Exception e) {
            this.setVisible(false);
        }
    }

    private void addFriend() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JLabel("Enter Friend Username: "));
        JTextField field = new JTextField();
        panel.add(field);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Friend",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create user commands to server
            sm.handleAddFriend(sm.getActiveUser(), field.getText());
        }
    }

    private void removeFriend() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JLabel("Enter Friend Username: "));
        JTextField field = new JTextField();
        panel.add(field);

        int result = JOptionPane.showConfirmDialog(this, panel, "Remove Friend",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create user commands to server
            sm.handleRemoveFriend(sm.getActiveUser(), field.getText());
        }
    }

    private void block() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JLabel("Enter Username: "));
        JTextField field = new JTextField();
        panel.add(field);

        int result = JOptionPane.showConfirmDialog(this, panel, "Block User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create user commands to server
            sm.handleBlockUser(sm.getActiveUser(), field.getText());
        }
    }

    private void removeSelf() {
        sm.reset(true);
    }


    public void insertComponent(JLabel imageLabel) {
        this.displayArea.insertComponent(imageLabel);
    }
}
