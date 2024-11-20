package src;

/**
 * An interface that defines how the UserDBDatabase class will act.
 *
 *
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
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
