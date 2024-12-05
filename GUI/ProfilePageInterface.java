package GUI;

/**
 * Interface to describe the Profile Page of the GUI
 * Handles Adding/Removing/Blocking Friends
 * Can View Posts from User
 *
 * @author CS180 Team 5
 * @version 1
 */
public interface ProfilePageInterface {
    // initialize the profile page
    void init();

    // method to add a friend from the profile page
    void addFriend();

    // method to remove a friend from the profile page
    void removeFriend();

    // method to block a user from the profile page
    void block();

    // method to remove your account from the app
    void removeSelf();

    void ret();

}
