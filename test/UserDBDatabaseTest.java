package test;

import org.junit.Before;
import org.junit.Test;
import org.sqlite.core.DB;
import src.Constants;
import src.User;
import src.UserDBDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Test file for UserDBDatabase
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class UserDBDatabaseTest {
    private static final String DB_PATH = Constants.USER_DB;

    @Before
    // sets up the test by removing all rows from the users table and connecting to the database at the database path
    public void setUp() {
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             Statement stmt = conn.createStatement()) {

            // Delete all rows from the users table
            stmt.execute("DELETE FROM users");


        } catch (Exception e) {
            throw new RuntimeException("Failed to reset the database: " + e.getMessage(), e);
        }
    }


    @Test
    // checks that the createUser() database method is working correctly when passing in a username and password
    public void testCreateUser() {
        assertTrue("User should be successfully created", UserDBDatabase.createUser("testUser123", "password123"));
        assertFalse("Duplicate user creation should fail", UserDBDatabase.createUser("testUser123", "password123"));
    }

    @Test
    // creates user named testUser
    // checks that user can log in with the correct credentials
    public void testLoginUserSuccess() {
        UserDBDatabase.createUser("testUser", "password123");
        User user = UserDBDatabase.loginUser("testUser", "password123");

        assertNotNull("User should be able to log in with correct credentials", user);
        assertEquals("Username should match after login", "testUser", user.getUsername());
    }

    // Ensuring that certain types of logins are invalid
    // Tests that users can't log in with usernames that don't exist
    // Makes sure that the user has to enter the correct password to login and that incorrect passwords prevent login
    @Test
    public void testLoginUserFailure() {
        User user = UserDBDatabase.loginUser("nonExistentUser", "password123");
        assertNull("Login should fail for non-existent user", user);

        UserDBDatabase.createUser("testUser", "password123");
        User userWrongPassword = UserDBDatabase.loginUser("testUser", "wrongPassword");
        assertNull("Login should fail with incorrect password", userWrongPassword);
    }

    // Test the add friend method, make sure we cannot add the same friend twice.
    // Creates two users
    // Makes sure that users can only add friends that exist in the database
    @Test
    public void testAddFriend() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        assertTrue("Should successfully add friend", UserDBDatabase.addFriend("testUser", "friendUser"));

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNotNull("User should exist", user);
        assertTrue("Friends list should contain the added friend", user.getFriendsList().contains("friendUser"));
    }

    // creates a user and a blocked user, and logs the user in and adds the blocked user the test users account
    // goes through the database and makes sure the blocked user was succesfully deleted
    @Test
    public void testAddBlocked() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("blockedUser", "password123");

        assertTrue("Should successfully block user", UserDBDatabase.addBlocked("testUser", "blockedUser"));

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNotNull("User should exist", user);
        assertTrue("Blocked list should contain the blocked user", user.getBlockedList().contains("blockedUser"));
    }

    // Tests the remove friend functionality, basically that friends are properly removed.
    // creates two users and tests the database methods to remove friends
    @Test
    public void testRemoveFriend() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        UserDBDatabase.addFriend("testUser", "friendUser");
        assertTrue("Should successfully remove friend", UserDBDatabase.removeFriend("testUser", "friendUser"));

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNotNull("User should exist", user);
        assertFalse("Friends list should not contain the removed friend", user.getFriendsList().contains("friendUser"));
    }

    // creates a test user and a friend uer then adds the friend to the test users account than tries to
    // add it again and makes sure it cant be added twice.
    // tests using two
    @Test
    public void testDuplicateFriendAddition() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        assertTrue("Should successfully add friend", UserDBDatabase.addFriend("testUser", "friendUser"));
        assertFalse("Adding the same friend again should fail", UserDBDatabase.addFriend("testUser", "friendUser"));
    }

    //same as duplicate friend but for blocked user
    // tests if the user was successfully blocked and that the user is in the blocked list
    @Test
    public void testDuplicateBlockUser() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("blockedUser", "password123");

        assertTrue("Should successfully block user", UserDBDatabase.addBlocked("testUser", "blockedUser"));
        assertFalse("Blocking the same user again should fail", UserDBDatabase.addBlocked("testUser", "blockedUser"));
    }

    // creates a user and then deletes than and tries to login and make sure the deleted user is ntot able to login
    @Test
    // creates a user
    // checks that the getAndDeleteUser method works and returns the deleted user
    // tries logging in as the deleted user and tests that it shouldn't work
    public void testDeleteUser() {
        UserDBDatabase.createUser("testUser", "password123");

        User deletedUser = UserDBDatabase.getAndDeleteUser("testUser");
        assertNotNull("Deleted user should be returned", deletedUser);

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNull("Deleted user should not exist in the database", user);
    }
}
