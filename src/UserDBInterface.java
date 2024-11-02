package src;

public interface UserDBInterface {
    String filename = Constants.USER_DATABASE_PATH;
    Object lock = new Object();

    static boolean createUser(String username, String password) {
        return false;
    }

    static User loginUser(String username, String password) {
        return null;
    }

}
