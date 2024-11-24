package src;

import java.util.ArrayList;

/**
 * An interface that defines what the User class will look like.
 *
 *
 * @author CS180 L2 Team 5
 *
 *
 * @version 2.0
 *
 **/

public interface UserInterface {
    // gets username
    String getUsername();
    // gets image path for profile pic
    String getImagePath();
    // gets list of friends
    ArrayList<String> getFriendsList();
    // gets list of blocked users
    ArrayList<String> getBlockedList();
    // sets username
    void setUsername(String username);
    // gets password
    String getPassword();
    // sets profile pic path
    void setImagePath(String imagePath);
    // adds friend
    boolean addFriend(String friend);
    // removes friend
    boolean removeFriend(String friend);
    // blocks user
    boolean blockUser(String user);
}
