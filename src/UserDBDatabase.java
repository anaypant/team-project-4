package src;

import org.sqlite.core.DB;

import java.io.*;
import java.sql.*;
import java.util.*;
/**
 * A class that defines how the database deals with User methods.
 * Logging in, adding friends, creating users.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class UserDBDatabase implements UserDBInterface {
    private static final String DB_PATH = "jdbc:sqlite:C:\\Users\\utsie\\Downloads\\sqlite-dump.db";


    // Starts up the writer and reader before every process (they are closed at the end).
    

    // Allows user to grab username and delete
    static {
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             Statement stmt = conn.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT NOT NULL, " +
                    "friends TEXT, " + // Semi-colon-separated list of friends
                    "blocked TEXT)";   // Semi-colon-separated list of blocked users
            stmt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    

    // Allows user to create a username and account
    public static synchronized boolean createUser(User u) {
        String username = u.getUsername();
        String password = u.getPassword();
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            String queryStatement = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(queryStatement);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        String createQuery = "INSERT INTO users (username, password, friends, blocked) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(createQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, ""); // Empty friends list
            pstmt.setString(4, ""); // Empty blocked list
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
       return true;
        
    }

    // Creates a user and writes it to the file
    public static synchronized boolean createUser(String username, String password) {
        String selectQuery = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement statement = conn.prepareStatement(selectQuery);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        User u = new User(username, password);
        return createUser(u);


    }


    // check the file to see if the username and password match with something
    // doesn't have to be locked right?
    public static synchronized User loginUser(String loginUsername, String Loginpassword) {
        String selectQuery = "SELECT * FROM users";
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> blocked = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(loginUsername) && result.getString(3).equals(Loginpassword)) {
                    if (result.getString(4).isEmpty()) {
                        friends = new ArrayList<>();
                    } else {
                        friends = new ArrayList<>(Arrays.asList(result.getString(4).split(":::")));
                    }
                    if (result.getString(5).isEmpty()) {
                        blocked = new ArrayList<>();
                    } else {
                        blocked = new ArrayList<>(Arrays.asList(result.getString(5).split(":::")));
                    }
                    User u = new User(result.getString(2),result.getString(3),result.getString(6),friends,blocked); // need to figure out how the freinds and bliocked will work and turn them into an arrayList
                    return (u);
                }   
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
      
    }

    // If username already exists
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
    public static synchronized boolean addFriend(String username, String targetUsername) {
        String selectQuery = "SELECT friends FROM users WHERE username = ?";
        String updateQuery = "UPDATE users SET friends = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String friendList = "";

            // Step 1: Retrieve the current friends list
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, username); // Filter by the user's username
            ResultSet result = pstmt.executeQuery();

            if (result.next()) { // If the user exists
                friendList = result.getString("friends");

                // Convert the friends list to an ArrayList for easier manipulation
                ArrayList<String> friends = friendList != null && !friendList.isEmpty()
                        ? new ArrayList<>(Arrays.asList(friendList.split(":::")))
                        : new ArrayList<>();

                // Step 2: Check if the friend is already in the list
                if (friends.contains(targetUsername)) {
                    return false; // Friend already exists
                }

                // Step 3: Add the new friend to the list
                friends.add(targetUsername);

                // Step 4: Convert the updated list back to a string
                friendList = String.join(":::", friends);
            } else {
                return false; // User does not exist
            }

            // Step 5: Update the friends list in the database
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, friendList); // Updated friends list
            updateStmt.setString(2, username);   // User's username
            updateStmt.executeUpdate();

            return true; // Friend successfully added

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle SQL exceptions
        }
    }

    // Allows user to search for an account (targetUsername) and remove that user as a friend
    public static synchronized boolean removeFriend(String username, String targetUsername) {
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

                if (friendsList != null && !friendsList.isEmpty()) {
                    // Convert the friends list to an ArrayList
                    friends = new ArrayList<>(Arrays.asList(friendsList.split(":::")));

                    // Remove the target friend
                    friends.remove(targetUsername);

                    // Convert the updated list back to a string
                    friendsList = String.join(":::", friends);
                } else {
                    return false; // No friends to remove
                }
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
    public static synchronized boolean addBlocked(String username, String targetUsername) {
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

                // Step 2: Convert the friends and blocked lists into ArrayLists
                if (friendsList != null && !friendsList.isEmpty()) {
                    friends = new ArrayList<>(Arrays.asList(friendsList.split(":::")));
                }
                if (blockedList != null && !blockedList.isEmpty()) {
                    blocked = new ArrayList<>(Arrays.asList(blockedList.split(":::")));
                }

                // Step 3: Remove the target user from the friends list
                friends.remove(targetUsername);

                // Step 4: Add the target user to the blocked list if not already there
                if (!blocked.contains(targetUsername)) {
                    blocked.add(targetUsername);
                } else {
                    return false; // User is already blocked
                }

                // Step 5: Convert updated lists back to strings
                friendsList = String.join(":::", friends);
                blockedList = String.join(":::", blocked);
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

    // Allows user to grab username and delete
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
                    if (friendsList == null || friendsList.isEmpty()) {
                        friends = new ArrayList<>();
                    } else {
                        friends = new ArrayList<>(Arrays.asList(friendsList.split(":::")));
                    }
                    blockedList = result.getString(5);
                    if (blockedList == null || blockedList.isEmpty()) {
                        blocked = new ArrayList<>();
                    } else {
                        blocked = new ArrayList<>(Arrays.asList(blockedList.split(":::")));
                    }
                    User u = new User (result.getString(2),result.getString(3),result.getString(6),friends,blocked);
                    PreparedStatement second  = conn.prepareStatement(deletQuery);
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




        
    
}
