package GUI;

import src.Post;
import src.PostDBDatabase;
import src.PostGUI;
import src.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Class to describe the Profile Page of the GUI
 * Handles Adding/Removing/Blocking Friends
 * Can View Posts from User
 *
 * @author CS180 Team 5
 * @version 1
 */

public class ProfilePage extends JFrame implements ProfilePageInterface {
    private JPanel appPanel;

    // Panel to display everything
    private JTextPane displayArea; // Displays the server output

    private JButton addFriendButton; // Button to add friend
    private JButton removeFriendButton; // Button to remove friend
    private JButton blockButton; // Button to block user
    private JButton removeSelfButton; // Button to permanently delete user
    private JButton retButton;

    private SocialMedia sm; // Reference to Main Social Media App
    private User user;
    private boolean adminMode;


    public ProfilePage(SocialMedia sm, User u) {
        this.sm = sm; // Connect to Social Media Context
        this.user = u;
        if (u != null) {
            adminMode = (u.getUsername().equals(sm.getActiveUser()));
        } else {
            adminMode = false;
        }

        this.init(); // Create GUI environment

    }

    // Creates GUI environment
    public void init() {
        appPanel = new JPanel(new BorderLayout());

        // Panel for command buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        addFriendButton = new JButton("Add Friend");
        removeFriendButton = new JButton("Remove Friend");
        blockButton = new JButton("Block User");
        removeSelfButton = new JButton("Delete Account");
        retButton = new JButton("Return");

        // Add action listeners for command buttons
        addFriendButton.addActionListener(e -> addFriend());
        removeFriendButton.addActionListener(e -> removeFriend());
        blockButton.addActionListener(e -> block());
        removeSelfButton.addActionListener(e -> removeSelf());
        retButton.addActionListener(e -> ret());

        // Add buttons to the panel
        if (adminMode) {
            buttonPanel.add(addFriendButton);
            buttonPanel.add(removeFriendButton);
            buttonPanel.add(blockButton);
            buttonPanel.add(removeSelfButton);
        }

        buttonPanel.add(retButton);

        // Add button panel to the app panel
        appPanel.add(buttonPanel, BorderLayout.NORTH);

        if (user != null) {
            // Add username and posts
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

            // Add username to the center panel
            JLabel usernameLabel = new JLabel("Profile: " + user.getUsername());
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(usernameLabel);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing

            // Add user's posts to the center panel
            ArrayList<Post> userPosts = PostDBDatabase.getPostsByUsername(
                    user.getUsername(), sm.getActiveUser());
            if (!userPosts.isEmpty()) {
                for (Post post : userPosts) {
                    PostGUI postGUI = new PostGUI(post, 0, this.sm, true);

                    centerPanel.add(postGUI.getPostPanel());
                    centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    // Add spacing between posts
                }
            } else {
                JLabel noPostsLabel = new JLabel("No posts to display.");
                noPostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                centerPanel.add(noPostsLabel);
            }

            appPanel.add(new JScrollPane(centerPanel), BorderLayout.CENTER);

            this.setContentPane(appPanel);

        }


        this.setSize(500, 600);

        // Set the title of the Page to the username
        if (sm.getActiveUser() == null) {
            this.setTitle("null");
        } else {
            this.setTitle("Welcome, " + sm.getActiveUser() + "!");
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

    }

    // Add friend button clicked
    public void addFriend() {
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

    // Remove friend button clicked
    public void removeFriend() {
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

    // Block user button clicked
    public void block() {
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

    // Permanently delete user
    public void removeSelf() {
        sm.reset(true); // Calls reset() with true boolean, indicating permanent delete user
    }

    public void ret() {
        sm.returnMainPage();
    }
}
