package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * A class that establishes a network connection.
 * Creates the interface for the user to call commands.
 * Creates a Thread for each active user
 *
 * @author CS180 L2 Team 5
 * @version 2.0
 **/

public class Server implements Runnable, ServerInterface {

    private final Socket socket;

    // Individual Client parameters
    private Post selectedPost;
    private State s;
    private String activeUser = null;
    private String selectedUsername = null; // Username for viewing posts
    private String createPostDesc = null;
    private ArrayList<Comment> comments = null; // Stores the comments for upvoting, downvoting, etc.
    private Comment selectedComment = null;

    // Creates a new server that connects to a client
    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.selectedPost = null;
        this.s = State.IDLE;
    }

    private void reset() {
        this.selectedPost = null;
        this.s = State.IDLE;
        this.comments = null;
        this.createPostDesc = null;
        this.selectedComment = null;
    }

    // Run method, run in each serve
    @Override
    public void run() {
        try {
            // Creates a reader and writer to respond to the client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            String line; // the reader's input and output message
            String tempUsername = null; // Temporary storage for username during creation
            String loginUsername = null; // tracks the username after the password is entered

            while ((line = in.readLine()) != null) {
                line = line.trim();
                System.out.println(line);
                String msg = ""; // output message
                System.out.println("You: " + line);

                // if the user wants help
                if (line.equals("help") && s == State.IDLE) {
                    msg = "Commands: ";
                    for (String item : COMMANDS) {
                        msg += item + ", ";
                    }
                    msg = msg.substring(0, msg.length() - 2); // return the list of commands,
                    // with the last comma removed
                } else {
                    switch (s) {

                        // IDLE: main menu, no command has been entered
                        case IDLE:
                            // based on the command, check to see what command they want
                            switch (line.toLowerCase()) {
                                case "create user":
                                    s = State.CREATE_USER;
                                    msg = "Please enter a username: ";
                                    break;
                                case "login user":
                                    s = State.LOGIN_USER;
                                    msg = "Please enter your username: ";
                                    break;
                                case "create post":
                                    s = State.CREATE_POST_IMG;
                                    msg = "Please enter post content: ";
                                    break;
                                case "select post":
                                    s = State.SELECT_POST_USERNAME;
                                    msg = "Please enter the username to view posts from: ";
                                    break;
                                case "upvote":
                                    System.out.println(selectedPost);
                                    if (selectedPost != null &&
                                            PostDBDatabase.upvotePost(selectedPost.getId())) {
                                        msg = "Post upvoted!";
                                    } else {
                                        msg = "No post selected to upvote.";
                                    }
                                    break;
                                case "delete post":
                                    if (selectedPost != null) {
                                        if (selectedPost.getCreator().equals(activeUser)) {
                                            boolean res = PostDBDatabase.deletePost(selectedPost.getId());
                                            if (res) {
                                                msg = "Successfully deleted post.";
                                            } else {
                                                msg = "Failed to delete post.";
                                            }
                                        } else {
                                            msg = "Only the creator of the post can delete a post.";
                                        }
                                    } else {
                                        msg = "No post selected to delete.";
                                    }
                                    break;
                                case "downvote":
                                    if (selectedPost != null &&
                                            PostDBDatabase.downvotePost(selectedPost.getId())) {
                                        msg = "Post downvoted!";
                                    } else {
                                        msg = "No post selected to downvote.";
                                    }
                                    break;
                                case "comment":
                                    if (selectedPost != null) {
                                        if (!selectedPost.isCommentsEnabled()) {
                                            msg = "Selected post owner has disabled comments";
                                            break;
                                        }
                                        s = State.ADD_COMMENT;
                                        msg = "Enter your comment: ";
                                    } else {
                                        msg = "No post selected to comment on.";
                                    }
                                    break;
                                case "select comment":
                                    if (selectedPost != null) {
                                        // display all comments from selected post
                                        comments = PostDBDatabase.getCommentsFromPost(selectedPost.getId());
                                        if (comments == null || comments.isEmpty()) {
                                            msg = "No comments on this post";
                                        } else {
                                            s = State.SELECT_COMMENT;
                                            int counter = 0;
                                            for (Comment comment : comments) {
                                                msg += counter + " ---> " + comment + "\n";
                                                msg += "Upvotes: " + comment.getUpvotes() + "   Downvotes: " +
                                                        comment.getDownvotes() + "\n";
                                                counter++;
                                            }
                                            msg += "Enter the index of the selected comment: ";

                                        }
                                    } else {
                                        msg = "No post selected to comment on.";
                                    }
                                    break;
                                case "add friend":
                                    if (activeUser == null) {
                                        msg = "Not logged in.";
                                    } else {
                                        s = State.ADD_FRIEND;
                                        msg = "Please enter username of friend to add: ";
                                    }
                                    break;
                                case "block":
                                    if (activeUser == null) {
                                        msg = "Not logged in.";
                                    } else {
                                        s = State.BLOCK;
                                        msg = "Please enter username of user to block: ";
                                    }
                                    break;
                                case "remove friend":
                                    if (activeUser == null) {
                                        msg = "Not logged in.";
                                    } else {
                                        s = State.REMOVE_FRIEND;
                                        msg = "Please enter username of friend to remove: ";
                                    }
                                    break;
                                case "remove user":
                                    if (activeUser == null) {
                                        msg = "Not logged in.";
                                    } else {
                                        boolean res = UserDBDatabase.deleteUser(activeUser);
                                        if (res) {
                                            msg = "Successfully removed user.";
                                        } else {
                                            msg = "Unable to remove user.";
                                        }
                                    }
                                    break;
                                case "enable comments":
                                    if (selectedPost != null) {
                                        String creator = PostDBDatabase.getCreatorOfPost(selectedPost.getId());
                                        if (creator == null || !creator.equals(activeUser)) {
                                            msg = "You are not authorized to enable comments on this post.";
                                        } else {
                                            boolean result = PostDBDatabase.enableComments(selectedPost.getId());
                                            msg = (result) ? "Comments enabled for this post." : "Failed to enable` comments.";
                                        }
                                    } else {
                                        msg = "No post selected.";
                                    }
                                    break;
                                case "disable comments":
                                    if (selectedPost != null) {
                                        String creator = PostDBDatabase.getCreatorOfPost(selectedPost.getId());
                                        if (creator == null || !creator.equals(activeUser)) {
                                            msg = "You are not authorized to disable comments on this post.";
                                        } else {
                                            boolean result = PostDBDatabase.disableComments(selectedPost.getId());
                                            msg = (result) ? "Comments disabled for this post." : "Failed to disable comments.";
                                        }
                                    } else {
                                        msg = "No post selected.";
                                    }
                                    s = State.IDLE;
                                    break;
                                case "downvote comment":
                                    if (selectedPost != null && comments != null) {
                                        try {
                                            boolean result = PostDBDatabase.downvoteComment(selectedPost.getId(), selectedComment);
                                            msg = result ? "Comment downvoted successfully." : "Failed to downvote comment.";
                                        } catch (Exception e) {
                                            msg = "Error downvoting comment: " + e.getMessage();
                                        }
                                    } else {
                                        msg = "No comment or post selected.";
                                    }
                                    s = State.IDLE;
                                    break;
                                case "upvote comment":
                                    if (selectedPost != null && comments != null) {
                                        try {
                                            boolean result = PostDBDatabase.upvoteComment(selectedPost.getId(), selectedComment);
                                            msg = result ? "Comment upvoted successfully." : "Failed to upvote comment.";
                                        } catch (Exception e) {
                                            msg = "Error upvoting comment: " + e.getMessage();
                                        }
                                    } else {
                                        msg = "No comment or post selected.";
                                    }
                                    s = State.IDLE;
                                    break;
                                case "delete comment":
                                    if (selectedPost != null && comments != null) {
                                        try {
                                            if (selectedComment.getCreator().equals(activeUser) || selectedPost.getCreator().equals(activeUser)) {
                                                boolean result = PostDBDatabase.deleteComment(selectedPost.getId(),
                                                        selectedComment.getCreator(), selectedComment.getComment());
                                                if (result) {
                                                    msg = "Comment deleted successfully.";
                                                } else {
                                                    msg = "Failed to delete comment.";
                                                }
                                            } else {
                                                msg = "You are not authorized to delete comments on this post.";
                                            }

                                        } catch (Exception e) {
                                            msg = "Error deleting comment: " + e.getMessage();
                                        }
                                    } else {
                                        msg = "No comment or post selected.";
                                    }
                                    break;
                                case "hide post":
                                    if (selectedPost != null) {
                                        if (selectedPost.getCreator().equals(activeUser)) {
                                            boolean result = PostDBDatabase.hidePost(selectedPost.getId());
                                            if (result) {
                                                msg = "Successfully hid post";
                                            } else {
                                                msg = "Failed to hide post.";
                                            }
                                        } else {
                                            msg = "You can only hide posts you created.";
                                        }
                                    } else {
                                        msg = "No post selected.";
                                    }
                                    break;

                                case "unhide post":
                                    if (selectedPost != null) {
                                        if (selectedPost.getCreator().equals(activeUser)) {
                                            boolean result = PostDBDatabase.unhidePost(selectedPost.getId());
                                            if (result) {
                                                msg = "Successfully unhid post";
                                            } else {
                                                msg = "Failed to unhide post.";
                                            }
                                        } else {
                                            msg = "You can only unhide posts you created.";
                                        }
                                    } else {
                                        msg = "No post selected.";
                                    }
                                    break;

                                case "display post":
                                    if (selectedPost != null) {
                                        msg = selectedPost.display();
                                    }
                                    else {
                                        msg = "No post selected.";
                                    }
                                    break;

                                default:
                                    msg = "Invalid command. Please try again.";
                                    break;
                            }
                            break;

                        case CREATE_USER: // user is being created, username already entered
                            if (tempUsername == null) {
                                // Expecting username
                                tempUsername = line;
                                msg = "Please enter a password: ";
                            } else {
                                // Expecting password
                                String password = line;
                                if (password.isEmpty() || password.length() < 5) {
                                    msg = "Password is too short.";
                                    tempUsername = null;
                                    s = State.IDLE;
                                    break;
                                }
                                if (UserDBDatabase.createUser(tempUsername, password)) {
                                    msg = "User created successfully.";
                                } else {
                                    msg = "User creation failed.";
                                }
                                tempUsername = null; // Reset for next user creation
                                s = State.IDLE;
                            }
                            break;

                        case LOGIN_USER: // logging in a user, asking for password
                            loginUsername = line;
                            msg = "Please enter your password: ";
                            s = State.LOGIN_USER_PASSWORD; // Move to a new sub-state
                            break;

                        case LOGIN_USER_PASSWORD: // logging in with username and password
                            String password = line;
                            if (loginUsername != null) {
                                User u = UserDBDatabase.loginUser(loginUsername, password);
                                if (u != null) {
                                    this.activeUser = u.getUsername();
                                    msg = "Login successful.";

                                    reset();

                                    // If login is successful, show feed of all friends
                                    // Get all Friends and get posts by friends
                                    ArrayList<Post> feed = new ArrayList<>();
                                    for (String friend : u.getFriendsList()) {
                                        ArrayList<Post> posts =
                                                PostDBDatabase.getPostsByUsername(friend, activeUser);
                                        feed.addAll(posts);
                                    }
                                    feed = Utils.sortPostsByDateDesc(feed);

                                    if (feed.size() == 0) {
                                        msg += "\n No posts to show on feed.";
                                    } else {
                                        msg += "\n Feed: ";
                                        // When sending posts to the client
                                        for (int i = 0; i < feed.size(); i++) {
                                            Post post = feed.get(i);
                                            msg += post.display();

                                            // Include the image URL if the post has an image
//                                            if (post.getUrl() != null && !post.getUrl().isEmpty()) {
//                                                // Construct the full image URL accessible by the client
//                                                String imageUrl = "http://your_server_ip_or_domain/images/" + new File(post.getUrl()).getName();
//                                                msg += "\nIMAGE_URL:" + imageUrl + "\n";
//                                            }
                                        }

                                    }


                                } else {
                                    msg = "Login failed.";
                                }
                                s = State.IDLE;
                            } else {
                                msg = "Fatal: loginUsername is null (LOGIN_USER_PASSWORD)";
                                s = State.IDLE;
                            }
                            break;

                        case CREATE_POST_IMG: // asking for image url
                            createPostDesc = line;
                            msg = "Please enter valid URL (empty for no URL)";
                            s = State.CREATE_POST;
                            break;

                        case CREATE_POST: // creating a new post, expecting image data or NO_IMAGE flag
                            if (activeUser != null) {
                                String imageFlag = line;
                                if (imageFlag.equals("IMAGE_ATTACHED")) {
                                    // Expect the Base64-encoded image data
                                    String encodedImage = in.readLine();
                                    // Decode the image data
                                    byte[] imageBytes = Base64.getDecoder().decode(encodedImage);

                                    // Save the image to the server's file system
                                    String imageFileName = saveImageToServer(imageBytes);

                                    // Create the post with the image file name as the URL/path
                                    PostDBDatabase.createPost(activeUser, createPostDesc, imageFileName);
                                } else if (imageFlag.equals("NO_IMAGE")) {
                                    // Create the post without an image
                                    PostDBDatabase.createPost(activeUser, createPostDesc, null);
                                } else {
                                    msg = "Invalid image flag.";
                                    s = State.IDLE;
                                    break;
                                }
                                msg = "Post created. Returning to IDLE.";
                            } else {
                                msg = "Please log in to create a post.";
                            }
                            s = State.IDLE;
                            break;


                        case SELECT_POST_USERNAME:
                            // The user inputs the username to view posts from
                            selectedUsername = line;
                            if (!UserDBDatabase.isFriend(activeUser, selectedUsername) &&
                                    !Objects.equals(activeUser, selectedUsername)) {
                                msg = line + " is not your friend. You can only view posts from friends.";
                                s = State.IDLE;
                            } else {
                                List<Post> userPosts = PostDBDatabase.getPostsByUsername(selectedUsername, activeUser);
                                if (userPosts.isEmpty()) {
                                    msg = "No posts found for user: " + selectedUsername;
                                    s = State.IDLE;
                                } else {
                                    msg = "Select a post by entering the number:\n";
                                    for (int i = 0; i < userPosts.size(); i++) {

                                        msg += "----  OPTION " + (i + 1) + ". ----\n" +
                                                userPosts.get(i).display() + "\n";
                                    }
                                    msg += "\nPlease select the desired post by " +
                                            "typing in the respective option number.\n";
                                    s = State.SELECT_POST_CHOICE;
                                }
                            }
                            break;

                        case SELECT_POST_CHOICE: // asking for which post they want to select
                            try {
                                int choice = Integer.parseInt(line) - 1;
                                if (choice == -1) {
                                    msg = "Invalid input. Please enter a valid number.";
                                    break;
                                }
                                selectedPost = PostDBDatabase.selectPost(selectedUsername, choice, activeUser);
                                if (selectedPost != null) {
                                    msg = "Post selected. " + selectedPost.getId();
                                } else {
                                    msg = "Invalid selection.";
                                }
                            } catch (NumberFormatException e) {
                                msg = "Invalid input. Please enter a number.";
                            }
                            s = State.IDLE;
                            break;

                        case ADD_COMMENT: // adding comment to selected post
                            if (selectedPost != null) {
                                PostDBDatabase.addComment(selectedPost.getId(), activeUser, line);
                                msg = "Comment added.";
                            } else {
                                msg = "Failed to add comment. No post selected.";
                            }
                            s = State.IDLE;
                            break;

                        case ADD_FRIEND: // adding a friend based on target post
                            if (line.equals(activeUser)) {
                                msg = "Could not add friend " + line;
                            } else {
                                boolean res = UserDBDatabase.addFriend(activeUser, line);
                                if (res) {
                                    msg = "Successfully added friend " + line;
                                } else {
                                    msg = "Could not add friend " + line;
                                }
                            }
                            s = State.IDLE;
                            break;

                        case BLOCK: // Blocked friend
                            boolean res2 = UserDBDatabase.addBlocked(activeUser, line);
                            if (res2) {
                                msg = "Successfully blocked " + line;
                            } else {
                                msg = "Could not block " + line;
                            }
                            s = State.IDLE;
                            break;
                        case REMOVE_FRIEND: // removed friend
                            boolean res3 = UserDBDatabase.removeFriend(activeUser, line);
                            if (res3) {
                                msg = "Successfully removed friend " + line;
                            } else {
                                msg = "Could not remove friend " + line;
                            }
                            s = State.IDLE;
                            break;

                        case SELECT_COMMENT:
                            if (comments != null && !comments.isEmpty()) {
                                try {
                                    int commentIndex = Integer.parseInt(line); // Select the comment
                                    if (commentIndex >= 0 && commentIndex < comments.size()) {
                                        selectedComment = comments.get(commentIndex);
                                        msg = "Selected comment: " + selectedComment;
                                        s = State.IDLE; // Transition to comment interaction state
                                    } else {
                                        msg = "Invalid comment index. Please try again.";
                                    }
                                } catch (NumberFormatException e) {
                                    msg = "Invalid input. Please enter a number.";
                                }
                            } else {
                                msg = "No comments available for selection.";
                                s = State.IDLE;
                            }
                            break;

                        default:
                            msg = "Unrecognized state.";
                            s = State.IDLE;
                            break;
                    }
                }
//                if (s.equals(State.IDLE)) { // print out main menu message if idle
//                    msg += "\n" + Constants.MAIN_MENU_MSG;
//                }
                msg = line + "\n" + msg;
                System.out.println("Us:\n" + msg);
                out.println(msg); // Send response to client
                out.println("EOM"); // Send an "END OF MESSAGE" indicator to
                // indicate end of response
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private String saveImageToServer(byte[] imageBytes) {
        // Generate a unique file name for the image
        String imageFileName = "image_" + UUID.randomUUID().toString() + ".png"; // or appropriate extension

        // Define the directory where images will be saved
        String imageDirectory = "images/";

        // Ensure the directory exists
        File dir = new File(imageDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Save the image to the file system
        try (FileOutputStream fos = new FileOutputStream(imageDirectory + imageFileName)) {
            fos.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Return the path or URL to the saved image
        return imageDirectory + imageFileName;
    }


    public static void main(String[] args) {
        try (ServerSocket mainSocket = new ServerSocket(Constants.PORT_NUMBER)) {
            System.out.println("Server is listening on port " + Constants.PORT_NUMBER);
            while (true) {
                Socket socket = mainSocket.accept();
                System.out.println("New client connected");
                Server s = new Server(socket);
                new Thread(s).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
