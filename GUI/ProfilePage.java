package GUI;

import src.*;

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
    private JScrollPane scrollPane;
    private JPanel centerPanel;


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
        if (!adminMode) {
            buttonPanel.add(addFriendButton);
            buttonPanel.add(removeFriendButton);
            buttonPanel.add(blockButton);
        } else {
            buttonPanel.add(removeSelfButton);
        }

        buttonPanel.add(retButton);

        // Add button panel to the app panel
        appPanel.add(buttonPanel, BorderLayout.NORTH);

        if (user != null) {
            // Add username and posts
            refreshFeed();
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

    public void refreshFeed() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Add username to the center panel
        if (user != null) {
            JLabel usernameLabel = new JLabel("Profile: " + user.getUsername());
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(usernameLabel);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing

            // Add user's posts to the center panel
            ArrayList<Post> userPosts = PostDBDatabase.getPostsByUsername(
                    user.getUsername(), sm.getActiveUser());
            User u = UserDBDatabase.getUserByUsername(sm.getActiveUser());
            if (user != null && u != null && (u.getFriendsList().contains(user.getUsername())
                    || u.getUsername().equals(user.getUsername()))) {
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
            }

        }


        scrollPane = new JScrollPane(centerPanel);
        appPanel.add(scrollPane, BorderLayout.CENTER);


        this.setContentPane(appPanel);
        appPanel.revalidate(); // Revalidate the panel to account for the new component
        appPanel.repaint();    // Repaint the panel to update the display


    }

    // Add friend button clicked
    public void addFriend() {
        if (user == null) {
            return;
        }
        sm.handleAddFriend(sm.getActiveUser(), user.getUsername());
        ret();


    }

    // Remove friend button clicked
    public void removeFriend() {
        if (user == null) {
            return;
        }
        sm.handleRemoveFriend(sm.getActiveUser(), user.getUsername());
        ret();
    }

    // Block user button clicked
    public void block() {
        if (user == null) {
            return;
        }
        sm.handleBlockUser(sm.getActiveUser(), user.getUsername());
        ret();

    }

    // Permanently delete user
    public void removeSelf() {
        sm.reset(true); // Calls reset() with true boolean, indicating permanent delete user
    }

    public void ret() {
        sm.returnMainPage();
    }
}
