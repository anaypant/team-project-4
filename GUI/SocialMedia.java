package GUI;

import src.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A client class that handles the GUI.
 * Keeps track of the pages (Sign In, Posts, and Main)
 *
 * @author CS180 L2 Team 5
 * @version 2.0
 **/


public class SocialMedia implements SocialMediaInterface {
    private JFrame current;
    private SignInPage signInPage;
    private MainPage mainPage;
    private Connection connection;
    private String activeUser; // To keep track of the logged-in user
    private String selectedPost;
    private String currentTitle = "Login";

    private Timer responseTimer;
    private ArrayList<PostGUI> posts;
    private ArrayList<User> allUsers;
    private ProfilePage profilePage;


    public SocialMedia() throws Exception {
        this.connection = new Connection();
        signInPage = new SignInPage(this);
        mainPage = new MainPage(this);
        profilePage = new ProfilePage(this, null);
        current = signInPage;
        current.setSize(600, 500);
        current.setVisible(true);
        responseTimer = new Timer(300, e -> pollServer());
        responseTimer.start();
        selectedPost = null;
        this.posts = new ArrayList<>();
        allUsers = new ArrayList<>();

    }

    // Creating a User
    // Dialog to input username and password
    public void handleCreateUser(String username, String password) {
        connection.println("create user");
        connection.println(username + Constants.DELIMITER + password);
    }

    // Logging in User
    public void handleLoginUser(String username, String password) {
        connection.println("login user");
        connection.println(username + Constants.DELIMITER + password);
        activeUser = username;

    }

    public void handleAddFriend(String active, String target) {
        connection.println("add friend");
        connection.println(target);
        resetFeed();
    }

    public void handleRemoveFriend(String active, String target) {
        connection.println("remove friend");
        connection.println(target);
        resetFeed();
    }

    public void handleBlockUser(String active, String target) {
        connection.println("block");
        connection.println(target);
        resetFeed();
    }

    public void handleUpvote() {
        connection.println("upvote");
        resetFeed();
    }

    public void handleDownvote() {
        connection.println("downvote");
        resetFeed();
    }

    public void handleComment(String comment) {
        connection.println("comment");
        connection.println(comment);
        resetFeed();
    }

    public void handleSelectComment() {
        connection.println("select comment");
        resetFeed();
    }

    public void handleUpVoteComment() {
        connection.println("upvote comment");
        resetFeed();
    }

    public void handleDownVoteComment() {
        connection.println("downvote comment");
        resetFeed();
    }

    public void handleHidePost(String postId) {
        connection.println("hide post");
        connection.println(postId);
        resetFeed();
    }

    public void handleUnhidePost(String postId) {
        connection.println("unhide post");
        connection.println(postId);

        resetFeed();
    }

    public void handleEnableComments(String postId) {
        connection.println("enable comments");
        connection.println(postId);

        resetFeed();
    }

    public void handleDisableComments(String postId) {
        connection.println("disable comments");
        connection.println(postId);

        resetFeed();
    }

    public void handleAddComment(String comment, String postId) {
        connection.println("add comment");
        connection.println(postId);
        connection.println(comment);
        resetFeed();
    }

    public void handleDeleteComment(String postId, Comment comment) {
        connection.println("delete comment");
        connection.println(postId + Constants.DELIMITER + comment.getCreator() +
                Constants.DELIMITER + comment.getComment());
        resetFeed();
    }


    public void handleFetchPosts() {
        connection.println("fetch posts");
    }

    public void handleFetchComments(String postId) {
        connection.println("fetch comments");
        connection.println(postId);
    }

    public void handleVotePost(String postId, boolean isUpvote) {
        connection.println(isUpvote ? "upvote post" : "downvote post");
        connection.println(postId);
        resetFeed();
    }

    public void handleVoteComment(String commentId, boolean isUpvote) {
        connection.println(isUpvote ? "upvote comment" : "downvote comment");
        connection.println(commentId);
        resetFeed();
    }

    public void handleDeletePost(String postId) {
        connection.println("abcde");
        connection.println(postId);
        resetFeed();
    }

    public ArrayList<User> getUsersThatContain(String key) {
        ArrayList<User> us = new ArrayList<>();

        if (key == null || key.equals("")) {
            for (User u : this.allUsers) {
                if (!(u.getBlockedList().contains(activeUser))) {
                    us.add(u);
                }
            }
            return us;
        }
        for (User u : this.allUsers) {
            if (u.getUsername().contains(key) && !(u.getBlockedList().contains(activeUser))) {
                us.add(u);
            }
        }
        return us;
    }

    public void fetchAllUsers() {
        connection.println("get all users");
    }


    public void pollServer() {
        try {
            // Check if data is available without blocking
            if (connection.ready()) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = connection.readLine()) != null && !line.equals("EOM")) {
                    response.append(line).append("\n");
                }
                processServerResponse(response.toString());
            }
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
            responseTimer.stop();
        }
    }

    public void resetFeed() {
        connection.println("fetch posts");
    }

    public void displayImg(String imagePath, boolean postPage) {
        try {
            StyledDocument doc;
            doc = this.mainPage.getStyledDocument();

            // Construct the full path to the image file
            File imageFile = new File(imagePath);

            // Check if the file exists
            if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());

                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(scaledImage);

                // Insert the image into the JTextPane
                JLabel imageLabel = new JLabel(imageIcon);
                doc.insertString(doc.getLength(), "\n", null);

                mainPage.setCaretPosition(doc.getLength());
                mainPage.insertComponent(imageLabel);

                // Insert a newline after the image for spacing
                doc.insertString(doc.getLength(), "\n", null);
            } else {
                if (postPage) {
                } else {
                    doc = this.mainPage.getStyledDocument();
                }

                if (imagePath.equals("null")) {
                    doc.insertString(doc.getLength(), "No associated Image.\n", null);
                } else {
                    doc.insertString(doc.getLength(), "Image file not found: " +
                            imagePath + "\n", null);
                }
                if (postPage) {
                } else {
                    mainPage.setCaretPosition(doc.getLength());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                StyledDocument doc;
                doc = this.mainPage.getStyledDocument();
                doc.insertString(doc.getLength(), "Failed to load image from path: " +
                        imagePath + "\n", null);
                if (postPage) {
                } else {
                    mainPage.setCaretPosition(doc.getLength());
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void processServerResponse(String response) throws BadLocationException {
        // Check if we're on the login screen or the app screen
        if (currentTitle.equals("Login")) {
            if (response.contains("Login successful.")) {
                // Move to main page
                current.setVisible(false);
                mainPage = new MainPage(this);
                current = mainPage;
                currentTitle = "Main Page";
                current.setVisible(true);
            } else if (response.contains("User created successfully.")) {
                displayMessage("User created successfully.");
            } else if (response.contains("Login failed.")) {
                activeUser = null;
                displayMessage("Login failed.");
            } else if (response.contains("User creation failed.")) {
                displayMessage("User creation failed.");
            }

            if (currentTitle.equals("Main Page")) {
                connection.println("fetch posts");
                mainPage.refresh();
                fetchAllUsers();

            }

        } else if (currentTitle.equals("Main Page")) {
            String[] lines = response.split("\n");
            System.out.println(Arrays.toString(lines));
            for (String line : lines) {
                if (line.startsWith("POST_LIST:") && !line.contains("No~~~posts~~~available.")) {
                    // Handle incoming posts for the feed
                    String[] posts = line.substring("POST_LIST:".length()).split("\\|");
                    this.posts.clear();
                    this.mainPage.refresh();
                    int counter = 0;
                    for (String post : posts) {
                        String[] postDetails = post.split(Constants.DELIMITER);
                        System.out.println(Arrays.toString(postDetails));
                        Post p = new Post(postDetails[0], postDetails[1], postDetails[2],
                                postDetails[3], postDetails[4], Integer.parseInt(postDetails[5]),
                                Integer.parseInt(postDetails[6]),
                                Utils.arrayCommentFromString(postDetails[7]),
                                Boolean.parseBoolean(postDetails[8]), Boolean.parseBoolean(postDetails[9]));
                        this.posts.add(new PostGUI(p, counter, this, false));
                        mainPage.addPostToFeed(this.posts.get(counter).getPostPanel());
                        counter += 1;
                    }
                    System.out.println(this.posts);
                } else if (line.startsWith("POST_LIST:")) {
                    this.posts.clear();
                    this.mainPage.refresh();
                    System.out.println("No posts");
                    displayMessage("No posts on your feed.");


                } else if (line.startsWith("IMAGE_URL:")) {
                    String imagePath = line.substring("IMAGE_URL:".length()).trim();
                    this.displayImg(imagePath, false);
                } else if (line.startsWith("USER_LIST:")) {
                    // Handle incoming posts for the feed
                    String[] users = line.substring("USER_LIST:".length()).split("\\|");
                    this.allUsers.clear();
                    for (String u : users) {
                        User us = User.parseUser(u);
                        this.allUsers.add(us);
                    }

                } else {
                    StyledDocument doc = mainPage.getStyledDocument();
                    doc.insertString(doc.getLength(), line + "\n", null);
                    mainPage.setCaretPosition(doc.getLength());
                }
            }
        }
    }

    private void displayMessage(String message) {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel(message));

        JOptionPane.showMessageDialog(current, panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SocialMedia sm = null;
            try {
                sm = new SocialMedia();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void sendMessage(String message) {
        this.connection.println(message);
    }

    public void reset(boolean delete) {
        this.current.setVisible(false);
        this.current = signInPage;
        if (delete) {
            this.connection.println("remove user");
        }
        this.activeUser = null;
        this.currentTitle = "Login";
        this.selectedPost = null;
        this.current.setVisible(true);
    }

    public void returnMainPage() {
        this.current.setVisible(false);
        mainPage = new MainPage(this);
        this.current = mainPage;
        this.selectedPost = null;
        this.currentTitle = "Main Page";
        this.current.setVisible(true);
        this.connection.println("fetch posts");
        this.mainPage.refresh();

    }

    public String getActiveUser() {
        return activeUser;
    }

    public String getSelectedPost() {
        return selectedPost;
    }

    public void goToProfilePage(String user) {
        this.current.setVisible(false);
        profilePage = new ProfilePage(this, UserDBDatabase.getUserByUsername(user));
        this.current = profilePage;
        this.selectedPost = null;
        this.currentTitle = "Profile Page";
        this.current.setVisible(true);


    }


}
