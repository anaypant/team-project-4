package test;

import org.junit.Before;
import org.junit.Test;
import org.sqlite.core.DB;
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
    private static final String DB_PATH = "jdbc:sqlite:users.sqlite";

    @Before
    public void setUp() {
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             Statement stmt = conn.createStatement()) {

            // Delete all rows from the users table
            stmt.execute("DELETE FROM users");

            System.out.println("All rows deleted. Database is ready for testing.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset the database: " + e.getMessage(), e);
        }
    }



    @Test
    public void testCreateUser() {
        assertTrue("User should be successfully created", UserDBDatabase.createUser("testUser123", "password123"));
        assertFalse("Duplicate user creation should fail", UserDBDatabase.createUser("testUser123", "password123"));
    }

    @Test
    public void testLoginUserSuccess() {
        UserDBDatabase.createUser("testUser", "password123");
        User user = UserDBDatabase.loginUser("testUser", "password123");

        assertNotNull("User should be able to log in with correct credentials", user);
        assertEquals("Username should match after login", "testUser", user.getUsername());
    }

    @Test
    public void testLoginUserFailure() {
        User user = UserDBDatabase.loginUser("nonExistentUser", "password123");
        assertNull("Login should fail for non-existent user", user);

        UserDBDatabase.createUser("testUser", "password123");
        User userWrongPassword = UserDBDatabase.loginUser("testUser", "wrongPassword");
        assertNull("Login should fail with incorrect password", userWrongPassword);
    }

    @Test
    public void testAddFriend() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        assertTrue("Should successfully add friend", UserDBDatabase.addFriend("testUser", "friendUser"));

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNotNull("User should exist", user);
        assertTrue("Friends list should contain the added friend", user.getFriendsList().contains("friendUser"));
    }

    @Test
    public void testAddBlocked() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("blockedUser", "password123");

        assertTrue("Should successfully block user", UserDBDatabase.addBlocked("testUser", "blockedUser"));

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNotNull("User should exist", user);
        assertTrue("Blocked list should contain the blocked user", user.getBlockedList().contains("blockedUser"));
    }

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

    @Test
    public void testDuplicateFriendAddition() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        assertTrue("Should successfully add friend", UserDBDatabase.addFriend("testUser", "friendUser"));
        assertFalse("Adding the same friend again should fail", UserDBDatabase.addFriend("testUser", "friendUser"));
    }

    @Test
    public void testDuplicateBlockUser() {
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("blockedUser", "password123");

        assertTrue("Should successfully block user", UserDBDatabase.addBlocked("testUser", "blockedUser"));
        assertFalse("Blocking the same user again should fail", UserDBDatabase.addBlocked("testUser", "blockedUser"));
    }

    @Test
    public void testDeleteUser() {
        UserDBDatabase.createUser("testUser", "password123");

        User deletedUser = UserDBDatabase.getAndDeleteUser("testUser");
        assertNotNull("Deleted user should be returned", deletedUser);

        User user = UserDBDatabase.loginUser("testUser", "password123");
        assertNull("Deleted user should not exist in the database", user);
    }
}
