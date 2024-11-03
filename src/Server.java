package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private final Socket socket;

    // Individual Client parameters
    private Post selectedPost;
    private state s;
    private String activeUser = null;


    // Accepted Commands
    public static final String[] commands = {
            "help",
            "Create user",
            "Login user",
            "Create post",
            "Select post"
    };

    private enum state {
        IDLE, CREATE_USER, LOGIN_USER, CREATE_POST, SELECT_POST, LOGIN_USER_PASSWORD,
    }

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.selectedPost = null;
        this.s = state.IDLE;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            String tempUsername = null; // Temporary storage for username during creation
            String loginUsername = null;

            while ((line = in.readLine()) != null) {
                String msg = "NULL";
                if (line.equals("help")) {
                    msg = "Commands: ";
                    for (String item : commands) {
                        msg += item + ", ";
                    }
                } else {
                    switch (s) {
                        case IDLE:
                            switch (line) {
                                case "Create user":
                                    s = state.CREATE_USER;
                                    msg = "Please enter a username: ";
                                    break;
                                case "Login user":
                                    s = state.LOGIN_USER;
                                    msg = "Please enter your username: ";
                                    break;
                                case "Create post":
                                    s = state.CREATE_POST;
                                    msg = "Please enter post content: ";
                                    break;
                                case "Select post":
                                    s = state.SELECT_POST;
                                    msg = "Please enter post ID to select: ";
                                    break;
                                default:
                                    msg = "Invalid command. Please try again. [help] for command list";
                                    break;
                            }
                            break;

                        case CREATE_USER:
                            if (tempUsername == null) {
                                // Expecting username
                                tempUsername = line;
                                msg = "Please enter a password: ";
                            } else {
                                // Expecting password
                                String password = line;
                                if (UserDBDatabase.createUser(tempUsername, password)) {
                                    msg = "User created successfully. Returning to IDLE.";

                                } else {
                                    // Static method to create user with username and password
                                    msg = "User creation failed. Returning to IDLE.";
                                }
                                tempUsername = null; // Reset for next user creation
                                s = state.IDLE;
                            }
                            break;

                        case LOGIN_USER:
                            loginUsername = line;
                            msg = "Please enter your password: ";
                            s = state.LOGIN_USER_PASSWORD; // Move to a new sub-state
                            break;

                        case LOGIN_USER_PASSWORD:
                            String password = line;
                            if (loginUsername != null) {
                                User u = UserDBDatabase.loginUser(loginUsername, password);
                                if (u != null) {
                                    this.activeUser = u.getUsername();
                                }
                                msg = this.activeUser != null ? "Login successful." : "Login failed.";
                                s = state.IDLE;
                                break;
                            } else {
                                msg = "Fatal: loginUsername is null (LOGIN_USER_PASSWORD)";
                                break;
                            }


                        case CREATE_POST:
                            PostDBDatabase.createPost(line, null);
                            msg = "Post created. Returning to IDLE.";
                            s = state.IDLE;
                            break;

                        case SELECT_POST:
                            String postId = line;
                            selectedPost = PostDBDatabase.selectPost(postId);
                            if (selectedPost != null) {
                                msg = "Post selected. You can now like, dislike, or comment.";
                                s = state.IDLE;
                            } else {
                                msg = "Post not found. Returning to IDLE.";
                                s = state.IDLE;
                            }
                            break;

                        default:
                            msg = "Unrecognized state.";
                            s = state.IDLE;
                            break;
                    }
                }

                out.println(msg); // Send response to client
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
            System.out.println("network.Server is listening on port " + Constants.PORT_NUMBER);
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
