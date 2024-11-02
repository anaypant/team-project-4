import java.util.ArrayList;

public interface UserInterface {
    static final String filename = Constants.USER_DATABASE_PATH;

    static void addFriend(User user, User friend) {
        // add a friend to a user
    }

    static void removeFriend(User user, User friend) {
        // remove a friend (if they exist) from a user
    }

    static void blockUser(User user, User user2) {
        // block a user2 from user (user blocks user2)
    }

    static void createUser(String username, String password) {
        // create a new user
    }
}
