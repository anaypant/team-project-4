package test;

import org.junit.Before;
import org.junit.Test;
import src.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test file for User
 *
 * @author CS180 L2 Team 5
 *
 * @version 2.0
 **/

public class UserTest {
    private User user;
    private ArrayList<String> friends;
    private ArrayList<String> blocked;

    //sets up the test by creating a user and setting their fields to test later
    @Before
    public void setUp() {
        friends = new ArrayList<>(Arrays.asList("friend1", "friend2"));
        blocked = new ArrayList<>(List.of("blockedUser1"));
        user = new User("username", "password", "profile.jpg", friends, blocked);
    }

    //tests when creating a new user its fields are successfully initialized
    @Test
    public void testConstructorWithUsernameAndPassword() {
        User userWithPassword = new User("user123", "securePass");
        assertEquals("user123", userWithPassword.getUsername());
        assertEquals("securePass", userWithPassword.getPassword());
        assertNotNull(userWithPassword.getFriendsList());
        assertNotNull(userWithPassword.getBlockedList());
    }

    //creates a new user with all fields and makes sure all the fields are intialized to the correct variables.
    @Test
    public void testConstructorWithAllFields() {
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("profile.jpg", user.getImagePath());
        assertEquals(friends, user.getFriendsList());
        assertEquals(blocked, user.getBlockedList());
    }

    //makes sures all setters and getters are returning or setting the right value
    @Test
    public void testSettersAndGetters() {
        user.setUsername("newUsername");
        user.setPassword("newPassword");
        user.setImagePath("newImage.jpg");

        assertEquals("newUsername", user.getUsername());
        assertEquals("newPassword", user.getPassword());
        assertEquals("newImage.jpg", user.getImagePath());
    }

    //adds a freind to the user and makes sure it's accurately represented in the users ArrayList of friends
    @Test
    public void testAddFriend() {
        user.addFriend("newFriend");
        assertTrue(user.getFriendsList().contains("newFriend"));
    }

    //removes a freind to the user and makes sure it's accurately represented in the users ArrayList of friends
    @Test
    public void testRemoveFriend() {
        user.addFriend("toBeRemoved");
        assertTrue(user.getFriendsList().contains("toBeRemoved"));

        user.removeFriend("toBeRemoved");
        assertFalse(user.getFriendsList().contains("toBeRemoved"));
    }

    //adds a blocked user to the user and makes sure it's accurately represented in the users ArrayList of blocked users
    @Test
    public void testBlockUser() {
        user.addFriend("toBeBlocked");
        assertTrue(user.getFriendsList().contains("toBeBlocked"));

        user.blockUser("toBeBlocked");
        assertTrue(user.getBlockedList().contains("toBeBlocked"));
        assertFalse(user.getFriendsList().contains("toBeBlocked"));
    }

    //Makes sure that when the ToString method is called is actually doing the ToString() method properly
    @Test
    public void testToString() {
        String expected = "username:::" + "password:::" + "profile.jpg:::" +
                "[\"friend1\",\"friend2\"]:::[\"blockedUser1\"]";
        assertEquals(expected, user.toString());
    }

    // Makes sure that when we parse through the User we are able to properly make a User out of teh he parsed data
    @Test
    public void testParseUser() {
        String userString = "username:::" + "password:::" + "profile.jpg:::" +
                "[\"friend1\",\"friend2\"]:::[\"blockedUser1\"]";
        User parsedUser = User.parseUser(userString);

        assertEquals("username", parsedUser.getUsername());
        assertEquals("password", parsedUser.getPassword());
        assertEquals("profile.jpg", parsedUser.getImagePath());
        assertEquals(Arrays.asList("friend1", "friend2"), parsedUser.getFriendsList());
        assertEquals(List.of("blockedUser1"), parsedUser.getBlockedList());
    }

    //Checks that a blokcked freinnd cannot be affedded to the friends lusist.
    @Test
    public void testAddFriendWhenBlocked() {
        user.blockUser("blockedFriend");
        user.addFriend("blockedFriend");
        assertTrue(user.getFriendsList().contains("blockedFriend"));
    }
}
