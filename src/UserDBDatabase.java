package src;

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
                        friends = null;
                    } else {
                        friends = new ArrayList<>(Arrays.asList(result.getString(4).split(":::")));
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
        String selectQuery = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String freindList = "";
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
    
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(5).contains(targetUsername)) {
                    return false;
                }
                freindList = result.getString(4);
                freindList += ":::" + targetUsername; // tried using the word delimiter didnt work
            }
            String addFriend = "INSERT INTO users (username, friends) VALUES (?, ?)";
            PreparedStatement second = conn.prepareStatement(addFriend);
            second.setString(1, username);
            second.setString(2, freindList);

        } catch (SQLException e) {
            return false;
        }
       return true;
    }

    // Allows user to search for an account (targetUsername) and remove that user as a friend
    public static synchronized boolean removeFriend(String username, String targetUsername) {
        String selectQuery = "SELECT * FROM users";
        String friendsList = "";
        ArrayList<String> friends = new ArrayList<>();
        String addFriend = "INSERT INTO users (username, friends) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    friendsList = result.getString(4);
                    if (!(friendsList == null) || !(friendsList.isEmpty())) { // not sure why there is some wierdo error here
                        friends = new ArrayList<>(Arrays.asList(friendsList.split(":::")));
                        for (int i = 0; i < friends.size(); i++) {
                            if (friends.get(i).equals(targetUsername)) {
                                friends.remove(i);
                            }
                        }
                        for (int i = 0; i < friends.size();i++) {
                            if (i +1 > friends.size()) {
                                friendsList = friends.get(i);
                            }else  {
                                friendsList += friends.get(i) + ":::";
                            }

                        }
                    }
                    PreparedStatement second = conn.prepareStatement(addFriend);
                    second.setString(1, username);
                    second.setString(2, friendsList);
                    return true;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            return false;
        }
       
    }

    // Allows user to search for an account (targetUsername) and block that user
    public static synchronized boolean addBlocked(String username, String targetUsername) {
        String selectQuery = "SELECT * FROM users";
        String blockedList = "";
        ArrayList<String> blocked = new ArrayList<>();
        String blockFriend = "INSERT INTO users (username, blocked) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(2).equals(username)) {
                    blockedList = result.getString(5);
                    if (!(blockedList == null) || !(blockedList.isEmpty())) {
                        blocked = new ArrayList<>(Arrays.asList(blockedList.split(":::")));
                        for (int i = 0; i < blocked.size(); i++) {
                            if (blocked.get(i).equals(targetUsername)) {
                                blocked.remove(i);
                            }
                        }
                        for (int i = 0; i < blocked.size();i++) {
                            if (i +1 > blocked.size()) {
                                blockedList = blocked.get(i);
                            }else  {
                                blockedList += blocked.get(i) + ":::";
                            }

                        }
                    }
                    PreparedStatement second = conn.prepareStatement(blockFriend);
                    second.setString(1, username);
                    second.setString(2, blockedList);
                    return true;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            return false;
        }
        
    }

    public static void main(String[] args) {
        User Utsav = new User("Utsav","Password");
        createUser(Utsav);
    }
        
    
}
