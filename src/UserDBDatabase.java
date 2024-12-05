package src;

import org.sqlite.core.DB;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A class that defines how the database deals with User methods.
 * Logging in, adding friends, creating users.
 *
 * @author CS180 L2 Team 5
 * @version 2.0
 **/

public class UserDBDatabase implements UserDBInterface {
    private static final String DB_PATH = Constants.USER_DB;


    // Starts up the writer and reader before every process (they are closed at the end).


    // Allows user to grab username and delete
    // Initializes table if it doesn't already exist
    // Checks that the schema is appropriate and handles SQL exceptions
    static {
        try (Connection conn = DriverManager.getConnection(DB_PATH); Statement stmt
                = conn.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY," +
                    " " + "password TEXT NOT NULL, " + "friends TEXT, " + // Semi-colon-separated list of friends
                    "blocked TEXT)";   // Semi-colon-separated list of blocked users
            stmt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Allows user to create a username and account
    // Inserts user into the database
    // returns true if user is created successfully, false otherwise
    // returns false if user already exists
    public static synchronized int createUser(User u) {
        String username = u.getUsername();
        String password = u.getPassword();
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            String queryStatement = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(queryStatement);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    return 2;
                }
            }
        } catch (SQLException e) {
            return 2;
        }
        String createQuery = "INSERT INTO users (username, password, friends, blocked)" +
                " VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(createQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, Utils.arrListToString(u.getFriendsList()));
            // Empty friends list
            pstmt.setString(4, Utils.arrListToString(u.getBlockedList()));
            // Empty blocked list
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
        return 0;

    }

    // Creates a user and writes it to the file
    // creates user given username and password parameters
    // again, returns false if user already exists and calls createUser() method with User object
    public static synchronized int createUser(String username, String password) {
        String selectQuery = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    return 2;
                }
            }
        } catch (Exception e) {
            return 1;
        }
        if (username.length() < 4 || password.length() < 4) {
            return 1;
        }
        User u = new User(username, password);
        return createUser(u);


    }


    // check the file to see if the username and password match with something
    // doesn't have to be locked right?
    // verifies username and password
    // returns User object on successful login
    public static synchronized User loginUser(String loginUsername, String loginPassword) {
        String selectQuery = "SELECT * FROM users";
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> blocked = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(loginUsername) &&
                        result.getString(3).equals(loginPassword)) {
                    friends = Utils.arrayFromString(result.getString(4));
                    blocked = Utils.arrayFromString(result.getString(5));
                    User u = new User(result.getString(2),
                            result.getString(3), result.getString(6),
                            friends, blocked);
                    // need to figure out how the freinds and bliocked
                    // will work and turn them into an arrayList
                    return (u);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    // Checks if username already exists
    // iterates through list of users obtained from database by querying
    // if the passed in username string matches a user in the DB, the method returns true
    private static synchronized boolean usernameExists(String username) {
        String selectQuery = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    // Allows user to search for another account (targetUsername) and add as friend
    // Checks if the friend's username exists in the database through SQL queries
    // returns false if friend's username doesn't exist
    public static synchronized boolean addFriend(String username, String targetUsername) {
        if (!usernameExists(targetUsername)) {
            return false; // Target user does not exist
        }

        String selectQuery = "SELECT friends FROM users WHERE username = ?";
        String updateQuery = "UPDATE users SET friends = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String friendsList = "";

            // Fetch current friends list
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, username);
            ResultSet result = selectStmt.executeQuery();

            if (result.next()) {
                friendsList = result.getString("friends");

                // Convert friends list to ArrayList
                ArrayList<String> friends = Utils.arrayFromString(friendsList);

                // Check for duplicates
                if (friends.contains(targetUsername)) {
                    return false; // Friend already exists
                }

                // Add new friend and update list
                friends.add(targetUsername);
                friendsList = Utils.arrListToString(friends);


                // Update database
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, friendsList);
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();
            }
            return true; // Friend successfully added

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle SQL exceptions
        }
    }


    // Allows user to search for an account (targetUsername) and remove that user as a friend
    // if the user to be removed doesn't exist, returns false
    // removed user from friends list using PreparedStatements
    public static synchronized boolean removeFriend(String username, String targetUsername) {
        if (!usernameExists(targetUsername)) {
            return false;
        }
        String selectQuery = "SELECT friends FROM users WHERE username = ?";
        String updateQuery = "UPDATE users SET friends = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String friendsList = "";
            ArrayList<String> friends = new ArrayList<>();

            // Step 1: Retrieve the current friends list
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, username); // Filter by the user's username
            ResultSet result = pstmt.executeQuery();

            if (result.next()) { // If the user exists
                friendsList = result.getString("friends");
                friends = Utils.arrayFromString(friendsList);
                friends.remove(targetUsername);
                friendsList = Utils.arrListToString(friends);
            } else {
                return false; // User does not exist
            }

            // Step 2: Update the friends list in the database
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, friendsList); // Updated friends list
            updateStmt.setString(2, username);   // User's username
            updateStmt.executeUpdate();

            return true; // Friend successfully removed

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    // Allows user to search for an account (targetUsername) and block that user
    // removes blocked user from friends list
    // adds blocked user to blocked list
    // returns false if the user to be blocked doesn't exist
    public static synchronized boolean addBlocked(String username, String targetUsername) {
        if (!usernameExists(targetUsername)) {
            return false;
        }

        String selectQuery = "SELECT friends, blocked FROM users WHERE username = ?";
        String updateQuery = "UPDATE users SET friends = ?, blocked = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String friendsList = "";
            String blockedList = "";
            ArrayList<String> friends = new ArrayList<>();
            ArrayList<String> blocked = new ArrayList<>();

            // Step 1: Retrieve the current friends and blocked lists
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, username);
            ResultSet result = pstmt.executeQuery();

            if (result.next()) { // User exists
                friendsList = result.getString("friends");
                blockedList = result.getString("blocked");

                friends = Utils.arrayFromString(friendsList);
                blocked = Utils.arrayFromString(blockedList);

                // Step 3: Remove the target user from the friends list
                friends.remove(targetUsername);
                // Step 4: Add the target user to the blocked list if not already there
                if (!blocked.contains(targetUsername)) {
                    blocked.add(targetUsername);
                } else {
                    return false; // User is already blocked
                }

                // Step 5: Convert updated lists back to strings
                friendsList = Utils.arrListToString(friends);
                blockedList = Utils.arrListToString(blocked);
            } else {
                return false; // User does not exist
            }

            // Step 6: Update the friends and blocked lists in the database
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, friendsList); // Updated friends list
            updateStmt.setString(2, blockedList); // Updated blocked list
            updateStmt.setString(3, username);   // Target username
            updateStmt.executeUpdate();

            return true; // Successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }

    // Allows user to delete their own account
    // removes User from the database and deletes their posts
    public static synchronized boolean deleteUser(String username) {
        // make sure target exists
        if (!usernameExists(username)) {
            return false;
        }
        // delete all their posts
        if (!PostDBDatabase.deletePostsByUsername(username)) {
            return false;
        }
        return getAndDeleteUser(username) != null;
    }

    // checks if target username is a friend of the base user
    // returns false if user is null
    public static synchronized boolean isFriend(String base, String target) {
        User user = getAndDeleteUser(base);
        if (user == null) {
            return false;
        }
        if (createUser(user) != 0) { // attempts to recreate the user after deleting them,
            // returns false if unsuccessful
            return false;
        }

        return user.getFriendsList().contains(target);
    }

    // Allows user to grab username and delete
    // Looks through database for specific username
    // returns User object after deleting the user
    public static synchronized User getAndDeleteUser(String username) {
        String selectQuery = "SELECT * FROM users";
        String deletQuery = "DELETE FROM users WHERE username = ?";
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> blocked = new ArrayList<>();
        String friendsList = "";
        String blockedList = "";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    friendsList = result.getString(4);
                    friends = Utils.arrayFromString(friendsList);

                    blockedList = result.getString(5);
                    blocked = Utils.arrayFromString(blockedList);

                    User u = new User(result.getString(2),
                            result.getString(3), result.getString(6),
                            friends, blocked);
                    PreparedStatement second = conn.prepareStatement(deletQuery);
                    second.setString(1, username);
                    second.executeUpdate();
                    return (u);
                }
            }

        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public static synchronized User getUserByUsername(String uName) {
        String selectQuery = "SELECT * FROM users";
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> blocked = new ArrayList<>();
        String friendsList = "";
        String blockedList = "";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(uName)) {
                    friendsList = result.getString(4);
                    friends = Utils.arrayFromString(friendsList);

                    blockedList = result.getString(5);
                    blocked = Utils.arrayFromString(blockedList);

                    User u = new User(result.getString(2),
                            result.getString(3), result.getString(6),
                            friends, blocked);
                    return (u);
                }
            }

        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public static synchronized ArrayList<User> getUsers() {
        String selectQuery = "SELECT * FROM users";
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> blocked = new ArrayList<>();
        String friendsList = "";
        String blockedList = "";
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                friendsList = result.getString(4);
                friends = Utils.arrayFromString(friendsList);

                blockedList = result.getString(5);
                blocked = Utils.arrayFromString(blockedList);

                User u = new User(result.getString(2),
                        result.getString(3), result.getString(6),
                        friends, blocked);
                users.add(u);
            }

        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return users;

    }
}
