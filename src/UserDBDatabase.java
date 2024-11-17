package src;

import java.io.*;

/**
 * A class that defines how the database deals with User methods.
 * Logging in, adding friends, creating users.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class UserDBDatabase implements UserDBInterface {
    private static PrintWriter out;
    private static BufferedReader in;
    private static String filename = Constants.USER_DATABASE_PATH;


    // Starts up the writer and reader before every process (they are closed at the end).
    private synchronized static void initialize() {
        try {
            out = new PrintWriter(new FileWriter(filename, true));
            in = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Allows user to grab username and delete
    public static synchronized User getAndDeleteUser(String username) {
        initialize();
        synchronized (lock) {
            try {
                String line = in.readLine();
                while (line != null) {
                    if (line.split(Constants.DELIMITER)[0].equals(username)) {
                        Utils.deleteUser(line.split(Constants.DELIMITER)[0], filename);
                        close();
                        return User.parseUser(line);
                    }
                    line = in.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    // Closes the reader and writer -- used after every process
    private synchronized static void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Allows user to create a username and account
    public static synchronized boolean createUser(User u) {
        // Starts up printer and writer
        initialize();

        // Checks if username already exists
        synchronized (lock) { // I think we need to synchronize this stuff to be thread safe
            try {
                String line = in.readLine();
                while (line != null) { // Parse the line and check if the first string (username) is the target username
                    if (line.split(Constants.DELIMITER)[0].equals(u.getUsername())) {
                        return false; // username already exists
                    }
                    line = in.readLine();
                }
                // if we didn't find it, we can add a username and password
//                User u = new User(username, password);
                System.out.println(u);
                out.println(u);
                out.flush(); // flush to ensure the file is written
                close();

            } catch (IOException e) {
                return false;
            }


            return true;
        }
    }

    // Creates a user and writes it to the file
    public static synchronized boolean createUser(String username, String password) {
        User u = new User(username, password);
        return createUser(u);

    }


    // check the file to see if the username and password match with something
    // doesn't have to be locked right?
    public static synchronized User loginUser(String loginUsername, String password) {
        try {
            initialize();
            String line = in.readLine();
            System.out.println("line:");
            System.out.println(line);
            while (line != null) {
                String[] parsed = line.split(Constants.DELIMITER);
                if (parsed[0].equals(loginUsername) && parsed[1].equals(password)) {
                    close();
                    return User.parseUser(line);
                }
                line = in.readLine();
            }
            close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // If username already exists
    private static synchronized boolean usernameExists(String username) {
        initialize();
        try {
            String line = in.readLine();
            while (line != null) {
                String[] parsed = line.split(Constants.DELIMITER);
                if (parsed[0].equals(username)) {
                    close();
                    return true;
                }
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Allows user to search for another account (targetUsername) and add as friend
    public static synchronized boolean addFriend(String username, String targetUsername) {
        // make sure target exists
        if (!usernameExists(targetUsername)) {
            return false;
        }
        User u = getAndDeleteUser(username);
        if (u == null) {
            return false;
        }
        if (!u.addFriend(targetUsername)) {
            createUser(u);
            return false;
        }
        return createUser(u);
    }

    // Allows user to search for an account (targetUsername) and remove that user as a friend
    public static synchronized boolean removeFriend(String username, String targetUsername) {
        // make sure target exists
        if (!usernameExists(targetUsername)) {
            return false;
        }
        User u = getAndDeleteUser(username);
        if (u == null) {
            return false;
        }
        if (!u.removeFriend(targetUsername)) {
            createUser(u);
            return false;
        }
        return createUser(u);
    }

    // Allows user to search for an account (targetUsername) and block that user
    public static synchronized boolean addBlocked(String username, String targetUsername) {
        // make sure target exists
        if (!usernameExists(targetUsername)) {
            return false;
        }
        User u = getAndDeleteUser(username);
        if (u == null) {
            return false;
        }
        if (!u.blockUser(targetUsername)) {
            createUser(u);
            return false;
        }
        return createUser(u);
    }

    // Allows user to delete their own account
    public static synchronized boolean deleteUser(String username) {
        // make sure target exists
        if (!usernameExists(username)) {
            return false;
        }
        return getAndDeleteUser(username) != null;
    }

    // checks if target username is a friend of the base user
    public static synchronized boolean isFriend(String base, String target) {
        User user = getAndDeleteUser(base);
        if (user == null) {
            return false;
        }
        if (!createUser(user)) {
            return false;
        }
        
        return user.getFriendsList().contains(target);
    }

    public static void setFilename(String fn) {
        filename = fn;
    }
}
