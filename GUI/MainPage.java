package GUI;

import src.Comment;
import src.Constants;
import src.User;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;


/**
 * Class to describe the Main Menu Page of the GUI
 * Handles Post Selection, Adding/Removing/Blocking Friends
 * Can View Posts from Feed
 *
 * @author CS180 Team 5
 * @version 1
 */

public class MainPage extends JFrame implements MainPageInterface {

    private JPanel appPanel;

    // Panel to display everything
    private JTextPane displayArea; // Displays the server output

    private JButton createPostButton; // Button to create posts
    private JButton selectPostButton; // Button to select a post
    private JButton logoutButton; // Button to log out
    private JButton searchUserButton;
    private JButton myProfileButton;

    private JPanel feedPanel;
    private JScrollPane feedScrollPane;
    private SocialMedia sm; // Reference to Main Social Media App


    public MainPage(SocialMedia sm) {
        this.sm = sm; // Connect to Social Media Context
        this.init(); // Create GUI environment

    }

    // Creates GUI environment
    public void init() {
        appPanel = new JPanel(new BorderLayout());

        // Display area for server responses
        displayArea = new JTextPane(); // Changed to JTextPane
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        appPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());

        // Replace display area with interactive panels
        feedPanel = new JPanel(new GridLayout(0, 1));
        feedScrollPane = new JScrollPane(feedPanel);
        appPanel.add(feedScrollPane, BorderLayout.CENTER);

        // Example: Add dummy posts to the feed
//        feedPanel.add(createPostPanel("Author1", "This is a description", null, "2024-12-01", 0, 0, new ArrayList<>() {
//        feedPanel.add(createPostPanel("Author2", "Another description", null, "2024-12-02", 0, 0, new ArrayList<>()));

        appPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel for command buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        createPostButton = new JButton("Create Post");
        selectPostButton = new JButton("Select Post");
        logoutButton = new JButton("Logout");

        searchUserButton = new JButton("Search User");
        myProfileButton = new JButton("My Profile");


        // Add action listeners for command buttons
        createPostButton.addActionListener(e -> createPost());
        selectPostButton.addActionListener(e -> selectPost());
        logoutButton.addActionListener(e -> logout());


        searchUserButton.addActionListener(e -> searchUser());
        myProfileButton.addActionListener(e -> goToMyProfile());


        // Add buttons to the panel
        buttonPanel.add(createPostButton);
        buttonPanel.add(logoutButton);

        buttonPanel.add(myProfileButton);
        buttonPanel.add(searchUserButton);

        // Add button panel to the app panel
        appPanel.add(buttonPanel, BorderLayout.NORTH);


        this.setContentPane(appPanel);
        this.setSize(1000, 800);

        // Set the title of the Page to the username
        if (sm.getActiveUser() == null) {
            this.setTitle("null");
        } else {
            this.setTitle("Welcome, " + sm.getActiveUser() + "!");
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

    }


    // Creates a post after button click
    public void createPost() {
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
            String imgMsg = "NO_IMAGE";
            // Send image data
            if (imageFile[0] != null) {
                try {
                    // Read the image file into a byte array
                    byte[] imageBytes = Files.readAllBytes(imageFile[0].toPath());

                    // Encode the image bytes as a Base64 string
                    String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                    // Send a flag indicating that an image is included
                    imgMsg = "IMAGE_ATTACHED";

                    // Send the encoded image
                    imgMsg += Constants.DELIMITER + encodedImage;
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to read the image file.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Indicate that no image is attached
                imgMsg = "NO_IMAGE";
            }
            sm.sendMessage(postContentArea.getText() + Constants.DELIMITER + imgMsg);

            refresh();
            sm.resetFeed();
        }
    }

    public void goToMyProfile() {
        sm.goToProfilePage(sm.getActiveUser());

    }

    // Helper method to check if the file is an image
    public boolean isImageFile(File file) {
        String[] imageExtensions = {"png", "jpg", "jpeg", "gif", "bmp"};
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    // Gets the styled document for Social Media App
    public StyledDocument getStyledDocument() {
        return displayArea.getStyledDocument();
    }

    // Update the location to write the next message
    public void setCaretPosition(int position) {
        displayArea.setCaretPosition(position);
    }

    // Select post logic after button is clicked
    public void selectPost() {
        // Input username to view posts from
        String username = JOptionPane.showInputDialog(this, "Enter username to view posts from:",
                "Select Post", JOptionPane.PLAIN_MESSAGE);

        // Ensures that a username is selected
        if (username != null && !username.trim().isEmpty()) {
            sm.sendMessage("select post");
            sm.sendMessage(username.trim());
        }
    }

    // Logs out after button is clicked
    public void logout() {
        try {
            displayArea.setText("");
            sm.reset(false); // Calls reset() function which resets to login page
        } catch (Exception e) {
            this.setVisible(false);
        }
    }


    // Insert an image into the display area (for posts)
    public void insertComponent(JLabel imageLabel) {
        this.displayArea.insertComponent(imageLabel);
    }

    public void addPostToFeed(JPanel post) {
        feedPanel.add(post);
        System.out.println(feedPanel.getComponentCount());
        System.out.println(feedPanel.getComponent(0));
    }

    public void refresh() {
        feedPanel.removeAll();
        feedPanel.revalidate(); // Revalidate the panel to account for the new component
        feedPanel.repaint();    // Repaint the panel to update the display
    }

    private JPanel createUserPanel(JPanel panel, User user) {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        userPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewProfileButton = new JButton("View Profile");
        viewProfileButton.setFont(new Font("Arial", Font.PLAIN, 12));
        viewProfileButton.setBackground(new Color(220, 220, 220));
        viewProfileButton.setFocusable(false);

        // Add action listener to navigate to the user's profile
        viewProfileButton.addActionListener(e -> {
            panel.setVisible(false);
            goToUserProfile(user);
        });

        userPanel.add(usernameLabel, BorderLayout.CENTER);
        userPanel.add(viewProfileButton, BorderLayout.EAST);

        return userPanel;
    }


    public void searchUser() {
        JPanel panel = new JPanel(new BorderLayout());
        sm.fetchAllUsers();

        // Text field for searching users
        JTextField searchUserArea = new JTextField();
        panel.add(searchUserArea, BorderLayout.NORTH);

        // Panel to display all users
        JPanel allUsersPanel = new JPanel();
        allUsersPanel.setLayout(new BoxLayout(allUsersPanel, BoxLayout.Y_AXIS));


        // Populate the initial list of all users
        ArrayList<User> allUsers = sm.getUsersThatContain(null);
        System.out.println("all users:");
        System.out.println(allUsers);
        populateUserList(allUsersPanel, allUsers, panel);
        System.out.println("components");
        System.out.println(Arrays.toString(allUsersPanel.getComponents()));

        JScrollPane scrollPane = new JScrollPane(allUsersPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add a DocumentListener to dynamically filter users
        searchUserArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateMatches();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateMatches();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateMatches();
            }

            private void updateMatches() {
                String text = searchUserArea.getText().trim();
                ArrayList<User> matches = sm.getUsersThatContain(text);
                System.out.println("matches");
                System.out.println(matches);
                populateUserList(allUsersPanel, matches, panel);
            }
        });

        panel.setPreferredSize(new Dimension(300, 600));

        int result = JOptionPane.showConfirmDialog(this, panel, "View Users",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            refresh();
            sm.resetFeed();
        }
    }

    private void populateUserList(JPanel userPanel, ArrayList<User> users, JPanel parentPanel) {
        userPanel.removeAll();

        for (User user : users) {
            JPanel singleUserPanel = createUserPanel(parentPanel, user);
            userPanel.add(singleUserPanel);
        }
        System.out.println("Component count");
        System.out.println(userPanel.getComponentCount());

        userPanel.revalidate();
        userPanel.repaint();
    }


    private void goToUserProfile(User user) {
        sm.goToProfilePage(user.getUsername());
    }


}
