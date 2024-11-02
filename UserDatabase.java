import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;

public class UserDatabase implements UserInterface {
    private static PrintWriter out;
    private static BufferedReader in;


    private static void initialize() {
        try {
            out = new PrintWriter(new FileWriter(filename, true));
            in = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

    public static boolean createUser(String username, String password) {
        //@TODO: Make sure the username does not already exist
        // For now it's fine
        initialize();

        // check if username exists already
        try {
            String line = in.readLine();
            while (line != null) {
                if(line.split(",")[0].equals(username)){
                    return false;
                }
                line = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        User u = new User(username, password);
        System.out.println(u);
        out.println(u);
        out.flush();
        close();
        return true;
    }

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
            }
            close();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
