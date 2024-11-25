package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class that establishes a network connection.
 * Creates the interface for the user to call commands.
 * Creates a Thread for each active user
 *
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class Server implements Runnable, ServerInterface {

    private final Socket socket;

    // Individual Client parameters
    private Post selectedPost;
    private State s;
    private String activeUser = null;
    private String selectedUsername = null; // Username for viewing posts
    private String createPostDesc = null;

    // Creates a new server that connects to a client
    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.selectedPost = null;
        this.s = State.IDLE;
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
                String msg = ""; // output message

                // if the user wants help
                if (line.equals("help") && s == State.IDLE) {
                    msg = "Commands: ";
                    for (String item : commands) {
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
                                        selectedPost = null;
                                    } else {
                                        msg = "No post selected to upvote.";
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
                                        s = State.ADD_COMMENT;
                                        msg = "Enter your comment: ";
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


                                    // If login is successful, show feed of all friends
                                    // Get all Friends and get posts by friends
                                    ArrayList<Post> feed = new ArrayList<>();
                                    for (String friend : u.getFriendsList()) {
                                        ArrayList<Post> posts =
                                                PostDBDatabase.getPostsByUsername(friend);
                                        feed.addAll(posts);
                                    }
                                    feed = Utils.sortPostsByDateDesc(feed);

                                    if (feed.size() == 0) {
                                        msg += "\n No posts to show on feed.";
                                    } else {
                                        msg += "\n Feed: ";
                                        for (int i = 0; i < feed.size(); i++) {
                                            msg += feed.get(i).display();
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

                        case CREATE_POST: // creating a new post, asking for content
                            if (activeUser != null) {
                                if (line.equals("")) {
                                    line = "null";
                                }
                                PostDBDatabase.createPost(activeUser, createPostDesc, line);
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
                                List<Post> userPosts = PostDBDatabase.getPostsByUsername(selectedUsername);
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
                                selectedPost = PostDBDatabase.selectPost(selectedUsername, choice);
                                if (selectedPost != null) {
                                    msg = "Post selected. You can now like, dislike, or comment.";
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
                            boolean res = UserDBDatabase.addFriend(activeUser, line);
                            if (res) {
                                msg = "Successfully added friend " + line;
                            } else {
                                msg = "Could not add friend " + line;
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



                        default:
                            msg = "Unrecognized state.";
                            s = State.IDLE;
                            break;
                    }
                }
                if (s.equals(State.IDLE)) { // print out main menu message if idle
                    msg += "\n" + Constants.MAIN_MENU_MSG;
                }
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
