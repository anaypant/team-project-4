package test;

import org.junit.Before;
import org.junit.Test;
import src.User;
import src.UserDBDatabase;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test file for UserDBDatabase
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class UserDBDatabaseTest {
    //set up by creating making sure it is writing to a file datbase and is writing to the file
    @Before
    public void setUp() {
        String filename = "test_user_db.txt";
        try {
            new FileWriter(filename, false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UserDBDatabase.setFilename(filename);
    }

    //tests that when a User is created and they tryu to login you are able to find them in the databse succefully
    @Test
    public void testLoginUserSuccess() {
        // Create a user and verify login
        UserDBDatabase.createUser("testUser", "password123");
        User user = UserDBDatabase.loginUser("testUser", "password123");

        assertNotNull("User should be able to log in with correct credentials", user);
        assertEquals("Username should match after login", "testUser", user.getUsername());
    }

    //makes sure that a User that was never created cannot login to the server as they are nonexistant in the database
    @Test
    public void testLoginUserFailure() {
        // Attempt login without creating a user first
        User user = UserDBDatabase.loginUser("nonExistentUser", "password123");
        assertNull("Login should fail for non-existent user", user);

        // Create user and try incorrect password
        UserDBDatabase.createUser("testUser", "password123");
        User userWrongPass = UserDBDatabase.loginUser("testUser", "wrongPassword");
        assertNull("Login should fail with incorrect password", userWrongPass);
    }

    //makes sure that when we add a freind to a user it is succesfully represented in teh Database
    @Test
    public void testAddFriend() {
        // Create users and add one as a friend to another
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        assertTrue("Should successfully add friend", UserDBDatabase.addFriend("testUser", "friendUser"));

        // Verify friend addition fails if user doesn’t exist
        assertFalse("Adding non-existent user as friend should fail", UserDBDatabase.addFriend("testUser", "nonExistentUser"));
    }

    //makes sure that when we add a blocked user to a user it is succesfully represented in teh Database
    @Test
    public void testBlockUser() {
        // Create users and block one from another
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("blockedUser", "password123");

        assertTrue("Should successfully block user", UserDBDatabase.addBlocked("testUser", "blockedUser"));

        // Verify block fails if user doesn’t exist
        assertFalse("Blocking non-existent user should fail", UserDBDatabase.addBlocked("testUser", "nonExistentUser"));
    }

    // makes sure if a duplicate friend tries to get added they won't get added to the database
    @Test
    public void testDuplicateFriendAddition() {
        // Create users and add a friend, then attempt duplicate addition
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("friendUser", "password123");

        UserDBDatabase.addFriend("testUser", "friendUser");
        assertFalse("Adding the same friend again should fail", UserDBDatabase.addFriend("testUser", "friendUser"));
    }

    // makes sure if a duplicate blocked user tries to get added they won't get added to the database
    @Test
    public void testDuplicateBlockUser() {
        // Create users, block a user, and attempt duplicate block
        UserDBDatabase.createUser("testUser", "password123");
        UserDBDatabase.createUser("blockedUser", "password123");

        UserDBDatabase.addBlocked("testUser", "blockedUser");
        assertFalse("Blocking the same user again should fail", UserDBDatabase.addBlocked("testUser", "blockedUser"));
    }

}
