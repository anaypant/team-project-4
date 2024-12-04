package GUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * A client class that handles the GUI..
 * Keeps track of the pages (Sign In, Posts, and Main)
 *
 * @author CS180 L2 Team 5
 * @version 2.0
 **/


public class SocialMedia implements SocialMediaInterface{
    private JFrame current;
    private SignInPage signInPage;
    private MainPage mainPage;
    private PostPage postPage;
    private Connection connection;
    private String activeUser; // To keep track of the logged-in user
    private String selectedPost;
    private String currentTitle = "Login";

    private Timer responseTimer;
    private String selectedComment;


    public SocialMedia() throws Exception {
        this.connection = new Connection();
        signInPage = new SignInPage(this);
        mainPage = new MainPage(this);
        postPage = new PostPage(this);
        current = signInPage;
        current.setSize(600, 500);
        current.setVisible(true);
        responseTimer = new Timer(300, e -> pollServer());
        responseTimer.start();
        selectedPost = null;
        selectedComment = null;

    }

    // Creating a User
    // Dialog to input username and password
    public void handleCreateUser(String username, String password) {
        connection.println("create user");
        connection.println(username);
        connection.println(password);
    }

    // Logging in User
    public void handleLoginUser(String username, String password) {
        connection.println("login user");
        connection.println(username);
        connection.println(password);
        activeUser = username;
    }

    public void handleAddFriend(String active, String target) {
        connection.println("add friend");
        connection.println(target);
    }

    public void handleRemoveFriend(String active, String target) {
        connection.println("remove friend");
        connection.println(target);
    }

    public void handleBlockUser(String active, String target) {
        connection.println("block");
        connection.println(target);
    }

    public void handleUpvote() {
        connection.println("upvote");
    }

    public void handleDownvote() {
        connection.println("downvote");
    }

    public void handleComment(String comment){
        connection.println("comment");
        connection.println(comment);
    }

    public void handleSelectComment(){
        connection.println("select comment");
    }

    public void handleUpVoteComment() {
        connection.println("upvote comment");
    }

    public void handleDownVoteComment() {
        connection.println("downvote comment");
    }

    public void handleHidePost() {
        connection.println("hide post");
    }

    public void handleUnhidePost() {
        connection.println("unhide post");
    }

    public void handleEnableComments() {
        connection.println("enable comments");
    }

    public void handleDisableComments() {
        connection.println("disable comments");
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

    public void displayImg(String imagePath, boolean postPage) {
        try {
            StyledDocument doc;
            if (postPage) {
                doc = this.postPage.getStyledDocument();
            } else {
                doc = this.mainPage.getStyledDocument();
            }

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

                if (postPage) {
                    this.postPage.setCaretPosition(doc.getLength());
                    this.postPage.insertComponent(imageLabel);
                } else {
                    mainPage.setCaretPosition(doc.getLength());
                    mainPage.insertComponent(imageLabel);
                }

                // Insert a newline after the image for spacing
                doc.insertString(doc.getLength(), "\n", null);
            } else {
                if (postPage) {
                    doc = this.postPage.getStyledDocument();
                } else {
                    doc = this.mainPage.getStyledDocument();
                }

                if (imagePath.equals("null")) {
                    doc.insertString(doc.getLength(), "No associated Image.\n", null);
                } else {
                    doc.insertString(doc.getLength(), "Image file not found: " + imagePath + "\n", null);
                }
                if (postPage) {
                    this.postPage.setCaretPosition(doc.getLength());
                } else {
                    mainPage.setCaretPosition(doc.getLength());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                StyledDocument doc;
                if (postPage) {
                    doc = this.postPage.getStyledDocument();
                } else {
                    doc = this.mainPage.getStyledDocument();
                }
                doc.insertString(doc.getLength(), "Failed to load image from path: " + imagePath + "\n", null);
                if (postPage) {
                    this.postPage.setCaretPosition(doc.getLength());
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
                // move to main page

                current.setVisible(false);
                mainPage = new MainPage(this);
                current = mainPage;
                current.setSize(600, 500);
                currentTitle = "Main Page";
                current.setVisible(true);


            } else {
                if (response.contains("Login failed.")) {
                    activeUser = null;
                }
                StyledDocument doc = signInPage.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), response, null);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
                signInPage.setCaretPosition(doc.getLength());

            }

        }
        if (currentTitle.equals("Main Page")) {
            StyledDocument doc = mainPage.getStyledDocument();
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("IMAGE_URL:")) {
                    String imagePath = line.substring("IMAGE_URL:".length()).trim();
                    this.displayImg(imagePath, false);

                } else if (line.startsWith("Post selected.")) {
                    // Move to post selected page
                    current.setVisible(false);
                    selectedPost = line.substring("Post selected.".length()).trim();
                    System.out.println("Selected post: " + selectedPost);
                    postPage = new PostPage(this);
                    current = postPage;
                    currentTitle = "Post Page";

                    current.setVisible(true);

                } else {
                    doc.insertString(doc.getLength(), line + "\n", null);
                }
            }

            if (currentTitle.equals("Post Page")) {
                connection.println("display post");
            }

        }
        if (currentTitle.equals("Post Page")) {
            StyledDocument doc = postPage.getStyledDocument();

            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("IMAGE_URL:")) {
                    String imagePath = line.substring("IMAGE_URL:".length()).trim();
                    this.displayImg(imagePath, true);

                }

                if (line.startsWith("Selected comment: ")) {
                    selectedComment = line.substring("Selected comment: ".length()).trim();
                }
                try {
                    doc.insertString(doc.getLength(), line + "\n", null);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
                postPage.setCaretPosition(doc.getLength());
            }


        }
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
        this.current = mainPage;
        this.selectedPost = null;
        this.selectedComment = null;
        this.currentTitle = "Main Page";
        this.current.setVisible(true);
    }

    public String getActiveUser() {
        return activeUser;
    }

    public String getSelectedPost() {
        return selectedPost;
    }
}
