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
 * @version 2.0
 **/

public class Server implements Runnable, ServerInterface {

    private final Socket socket;

    // Individual Client parameters
    private Post selectedPost;
    private State s;
    private String activeUser = null;
    private String selectedUsername = null;
    private ArrayList<Comment> comments = null;
    private Comment selectedComment = null;

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.selectedPost = null;
        this.s = State.IDLE;
    }

    private void reset() {
        this.selectedPost = null;
        this.s = State.IDLE;
        this.comments = null;
        this.selectedComment = null;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line;

            while ((line = in.readLine()) != null) {
                line = line.trim();
                String msg;
                System.out.println(line);

                switch (s) {
                    case IDLE:
                        msg = handleIdleState(line, in);
                        break;
                    case CREATE_USER:
                        s = State.IDLE;
                        msg = handleCreateUser(line);
                        break;
                    case LOGIN_USER:
                        s = State.IDLE;
                        msg = handleLoginUser(line, in);
                        break;
                    case CREATE_POST_IMG:
                        s = State.IDLE;
                        msg = handleCreatePost(line, in);
                        break;
                    case ADD_COMMENT:
                        s = State.IDLE;
                        msg = handleAddComment(line);
                        break;
                    case SELECT_POST_USERNAME:
                        s = State.IDLE;
                        msg = handleSelectPostUsername(line);
                        break;
                    case SELECT_POST_CHOICE:
                        s = State.IDLE;
                        msg = handleSelectPostChoice(line);
                        break;
                    case SELECT_COMMENT:
                        s = State.IDLE;
                        msg = handleSelectComment(line);
                        break;
                    case HIDE_POST:
                        s = State.IDLE;
                        msg = handleHidePost(line);
                        break;
                    case UNHIDE_POST:
                        s = State.IDLE;
                        msg = handleUnhidePost(line);
                        break;
                    case DELETE_COMMENT:
                        s = State.IDLE;
                        msg = handleDeleteComment(line);
                        break;
                    case ENABLE_COMMENTS:
                        s = State.IDLE;
                        msg = handleEnableComments(line);
                        break;
                    case DISABLE_COMMENTS:
                        s = State.IDLE;
                        msg = handleDisableComments(line);
                        break;
                    case ADD_FRIEND:
                        s = State.IDLE;
                        msg = handleAddFriend(line);
                        break;
                    case REMOVE_FRIEND:
                        s = State.IDLE;
                        msg = handleRemoveFriend(line);
                        break;
                    case BLOCK:
                        s = State.IDLE;
                        msg = handleBlock(line);
                        break;
                    case DELETE_POST:
                        s = State.IDLE;
                        msg = handleDeletePost(line);
                        break;
                    default:
                        msg = "Invalid state.";
                        reset();
                        break;
                }

                out.println(msg);
                out.println("EOM");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleIdleState(String line, BufferedReader in) throws IOException {
        switch (line.toLowerCase()) {
            case "fetch posts":
                return fetchPosts();
            case "upvote post":
            case "downvote post":
                return handleVotePost(line, in.readLine());
            case "upvote comment":
            case "downvote comment":
                return handleVoteComment(line, in.readLine());
            case "create user":
                s = State.CREATE_USER;
                return "Please enter a username: ";
            case "login user":
                s = State.LOGIN_USER;
                return "Please enter your username: ";
            case "create post":
                s = State.CREATE_POST_IMG;
                return "Please enter post content: ";
            case "add comment":
                if (activeUser != null) {
                    String postId = in.readLine(); // Read the post ID
                    String commentData = in.readLine(); // Read the encoded comment data
                    Comment newComment = Comment.parseCommentFromString(commentData);
                    boolean success = PostDBDatabase.addComment(postId, newComment.getCreator(),
                            newComment.getComment());
                    return success ? "Comment added successfully." : "Failed to add comment.";
                } else {
                    return "Please log in to add a comment.";
                }
            case "select post":
                s = State.SELECT_POST_USERNAME;
                return "Enter the username to view posts: ";
            case "hide post":
                s = State.HIDE_POST;
                return "post id:";
            case "unhide post":
                s = State.UNHIDE_POST;
                return "post id:";
            case "delete comment":
                s = State.DELETE_COMMENT;
                return "comment to delete:";
            case "enable comments":
                s = State.ENABLE_COMMENTS;
                return "post to enable";
            case "disable comments":
                s = State.DISABLE_COMMENTS;
                return "post to enable";
            case "add friend":
                s = State.ADD_FRIEND;
                return "friend to add";
            case "remove friend":
                s = State.REMOVE_FRIEND;
                return "friend to remove";
            case "block":
                s = State.BLOCK;
                return "user to block";
            case "remove user":
                return handleDeleteUser(activeUser);
            case "get all users":
                return handleGetAllUsers();
            case "delete post":
                s = State.DELETE_POST;
                return "post to delete";
            default:
                return "Invalid command. Type 'help' for a list of commands.";
        }
    }

    private String handleDeletePost(String line) {
        boolean res = PostDBDatabase.deletePost(line);
        if (res) {
            return "Successfully deleted post.";
        } else {
            return "Failed to delete post.";
        }
    }

    private String handleGetAllUsers() {

        ArrayList<User> users = UserDBDatabase.getUsers();
        if (users.isEmpty()) {
            return "USER_LIST:~~~No~~~users~~~available.";
        }

        StringBuilder feed = new StringBuilder("USER_LIST:");
        for (User u : users) {
            feed.append(u.toString() + "|");
        }
        return feed.toString();
    }

    private String handleDeleteUser(String line) {
        boolean res = UserDBDatabase.deleteUser(line);
        boolean res2 = PostDBDatabase.deletePostsByUsername(line);
        if (res && res2) {
            return "Successfully deleted user.";
        } else {
            return "Failed to delete user.";
        }
    }

    private String handleBlock(String line) {
        boolean resp = UserDBDatabase.addBlocked(activeUser, line);
        if (resp) {
            return "Successfully blocked user.";
        } else {
            return "Failed to block user.";
        }
    }

    private String handleRemoveFriend(String line) {
        boolean resp = UserDBDatabase.removeFriend(activeUser, line);
        if (resp) {
            return "Successfully removed friend.";
        } else {
            return "Failed to remove friend.";
        }
    }

    private String handleAddFriend(String line) {
        boolean resp = UserDBDatabase.addFriend(activeUser, line);
        if (resp) {
            return "Successfully added friend.";
        } else {
            return "Failed to add friend.";
        }
    }

    private String handleEnableComments(String line) {
        boolean res = PostDBDatabase.enableComments(line);
        if (res) {
            return "Comment enabled successfully.";
        } else {
            return "Could not enable comments.";
        }
    }

    private String handleDisableComments(String line) {
        boolean res = PostDBDatabase.disableComments(line);
        if (res) {
            return "Comment disabled successfully.";
        } else {
            return "Could not disable comments.";
        }
    }

    private String handleDeleteComment(String line) {
        String[] resp = line.split(Constants.DELIMITER);
        System.out.println(Arrays.toString(resp));
        boolean res = PostDBDatabase.deleteComment(resp[0], resp[1], resp[2]);
        if (res) {
            return "Successfully deleted comment";
        } else {
            return "Failed to delete comment";
        }
    }

    private String handleHidePost(String postId) {
        boolean res = PostDBDatabase.hidePost(postId);
        if (res) {
            return "Successfully hidden post.";
        } else {
            return "Failed to hide post.";
        }
    }

    private String handleUnhidePost(String postId) {
        boolean res = PostDBDatabase.unhidePost(postId);
        if (res) {
            return "Successfully unhid post.";
        } else {
            return "Failed to unhide post.";
        }
    }

    private String handleVoteComment(String action, String commentId) {
        if (activeUser == null) {
            return "Please log in to vote on comments.";
        }

        // Upvote or downvote the comment
        boolean success = action.equals("upvote comment")
                ? PostDBDatabase.upvoteComment(commentId)
                : PostDBDatabase.downvoteComment(commentId);

        return success ? "Comment vote updated successfully." : "Failed to update comment vote.";
    }


    private String handleCreateUser(String line) {
        // Logic to create user
        String[] resp = line.split(Constants.DELIMITER);
        if (resp[0].isEmpty()) {
            reset();
            return "User creation failed. Try again.";
        }
        // Assume database interaction
        int response = UserDBDatabase.createUser(resp[0], resp[1]);
        if (response == 1) {
            return "User creation failed. Username must be at least 4 characters, " +
                    "Password must be at least 4 characters.";
        } else if (response == 2) {
            return "User creation failed. Username already exists.";
        }
        reset();
        return "User created successfully.";
    }

    private String handleLoginUser(String line, BufferedReader in) throws IOException {
        // Logic to login user
        // Assume validation with database
        String[] resp = line.split(Constants.DELIMITER);
        User u = UserDBDatabase.loginUser(resp[0], resp[1]);
        if (u == null) {
            return "Login failed.";
        }
        if (u.getUsername() != null) {
            activeUser = u.getUsername();
            reset();
            return "Login successful.";
        }
        return "Login failed.";
    }

    private String fetchPosts() {
        if (activeUser == null) {
            return "Please log in to view posts.";
        }
        System.out.println(activeUser);

        ArrayList<Post> posts = PostDBDatabase.getFeedForUser(UserDBDatabase.getUserByUsername(activeUser));
        if (posts.isEmpty()) {
            return "POST_LIST:~~~No~~~posts~~~available.";
        }

        StringBuilder feed = new StringBuilder("POST_LIST:");
        for (Post post : posts) {
            feed.append(post.toString() + "|");
        }
        return feed.toString();
    }


    private String handleVotePost(String action, String postId) {
        if (activeUser == null) {
            return "Please log in to vote on posts.";
        }
        boolean success = action.equals("upvote post") ?
                PostDBDatabase.upvotePost(postId) :
                PostDBDatabase.downvotePost(postId);
        return success ? "Post vote updated successfully." : "Failed to update post vote.";
    }

    private String handleCreatePost(String line, BufferedReader in) throws IOException {
        // Read the encoded image from the client
        String[] resp = line.split(Constants.DELIMITER);


        // Check if the encoded image is valid or if it is marked as "NO_IMAGE"
        if (resp.length == 2) {
            // If no image is provided, create the post without an image
            PostDBDatabase.createPost(activeUser, resp[0], null);
            reset();
            return "Post created successfully without an image.";
        }

        try {
            // Decode the Base64 string into bytes
            byte[] imageBytes = Base64.getDecoder().decode(resp[2]);

            // Save the decoded image to the server
            String imagePath = saveImageToServer(imageBytes);

            // Create the post with the image path
            PostDBDatabase.createPost(activeUser, resp[0], imagePath);

            reset();
            return "Post created successfully with an image.";
        } catch (IllegalArgumentException e) {
            // Handle invalid Base64 strings
            return "Failed to create post: Invalid image encoding.";
        }
    }

    private String handleAddComment(String comment) {
        if (selectedPost == null) {
            reset();
            return "No post selected.";
        }
        PostDBDatabase.addComment(selectedPost.getId(), activeUser, comment);
        reset();
        return "Comment added.";
    }

    private String handleSelectPostUsername(String username) {
        selectedUsername = username;
        s = State.SELECT_POST_CHOICE;
        return "Enter the number of the post to select: ";
    }

    private String handleSelectPostChoice(String choice) {
        // Logic to select post
        reset();
        return "Post selected.";
    }

    private String handleSelectComment(String line) {
        // Logic to handle selecting comments
        reset();
        return "Comment selected.";
    }

    private String saveImageToServer(byte[] imageBytes) {
        String imageFileName = "image_" + UUID.randomUUID().toString() + ".png";
        String imageDirectory = "images/";

        File dir = new File(imageDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(imageDirectory + imageFileName)) {
            fos.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageDirectory + imageFileName;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Constants.PORT_NUMBER)) {
            System.out.println("Server listening on port " + Constants.PORT_NUMBER);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new Server(socket)).start();
            }
        } catch (IOException e) {
            // do nothing
            // notethat a client disonnected
        }
    }
}
