import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    private Socket socket;
    private UserDatabase userDb;
    private PostDatabase postDb;
    private static ArrayList<Object> arr = new ArrayList<>();
    private Post selectedPost;
    private state s;
    private User user = null;
    // 0         1        2           3
    // postBfr   postPw   userBfr     userPw

    // Accepted commands:
    // Create User
    // Login User
    // Create Post
    // Select Post
    // Like Selected Post
    // Dislike Selected Post
    // Comment Selected Post
    private static final String[] commands = {
            "help",
            "Create user",
            "Login user",
            "Create post",
            "Select post"
    };

    private static enum state {
        IDLE, CREATE_USER, LOGIN_USER, CREATE_POST, SELECT_POST, LOGIN_USER_PASSWORD,
    }

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        if (userDb == null) {
            userDb = new UserDatabase();
            postDb = new PostDatabase();
            arr.add(new Object());
            arr.add(new Object());
        }
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
                                if (UserDatabase.createUser(tempUsername, password)) {
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
                                this.user = UserDatabase.loginUser(loginUsername, password);
                                msg = this.user != null ? "Login successful." : "Login failed.";
                                s = state.IDLE;
                                break;
                            } else {
                                msg = "Fatal: loginUsername is null (LOGIN_USER_PASSWORD)";
                                break;
                            }


                        case CREATE_POST:
                            String postContent = line;
                            PostDatabase.createPost(postContent);
                            msg = "Post created. Returning to IDLE.";
                            s = state.IDLE;
                            break;

                        case SELECT_POST:
                            int postId = Integer.parseInt(line);
                            selectedPost = PostDatabase.selectPost(postId);
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
