package src;

/**
 * An interface that defines how the UserDBDatabase class will act.
 *
 *
 * @author CS180 L2 Team 5
 *
 * @version 2.0
 **/

public interface UserDBInterface {

    // outline of createUser(String username, String password) method for UserDBDatabase
    static boolean createUser(String username, String password) {
        return false;
    }

    // outline of loginUser(String username, String password) method for UserDBDatabase
    static User loginUser(String username, String password) {
        return null;
    }

}
