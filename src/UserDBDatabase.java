package src;

import java.io.*;

public class UserDBDatabase implements UserDBInterface {
    private static PrintWriter out;
    private static BufferedReader in;

    // Starts up the writer and reader before every process (they are closed at the end).
    private static void initialize() {
        try {
            out = new PrintWriter(new FileWriter(filename, true));
            in = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static User getUser(String username) {
        initialize();
        synchronized (lock) {
            try {
                String line = in.readLine();
                while (line != null) {

                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    // Closes the reader and writer -- used after every process
    private static void close() {
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

    // Creates a user and writes it to the file
    public static boolean createUser(String username, String password) {
        // Starts up printer and writer
        initialize();

        // check if username exists already
        synchronized (lock) { // I think we have to synchronize this stuff to be thread safe
            try {
                String line = in.readLine();
                while (line != null) { // Parse the line and check if the first string (username) is the target username
                    if (line.split(",")[0].equals(username)) {
                        return false; // username already exists
                    }
                    line = in.readLine();
                }
                // if we didn't find it, we can add a username and password
                User u = new User(username, password);
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


    // check the file to see if the username and password match with something
    // doesn't have to be locked right?
    public static User loginUser(String loginUsername, String password) {
        try {
            initialize();
            String line = in.readLine();
            System.out.println("line:");
            System.out.println(line);
            while (line != null) {
                String[] parsed = line.split(",");
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

    private static boolean usernameExists(String username) {
        initialize();
        try {
            String line = in.readLine();
            while (line != null) {
                String[] parsed = line.split(",");
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

    public static boolean addFriend(User host, String targetUsername) {
        // remove specific user from the file
        // make sure target exists
        if (!usernameExists(targetUsername)) {
            return false;
        }
        String oldHost = host.toString();
        host.addFriend(targetUsername);
        String newHost = host.toString();
        try {
            synchronized (lock) {
                Utils.replaceLineInFile(oldHost, newHost, filename);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
