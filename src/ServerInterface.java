package src;

/**
 * An Interface that describes field functionality for the Server class
 * Commands: Creating posts, deleting posts,
 * commenting on posts, deleting comments on posts,
 * upvoting, downvoting.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public interface ServerInterface {
    // All possible different states that can be achieved
    enum state {
        IDLE, CREATE_USER, LOGIN_USER, CREATE_POST, CREATE_POST_IMG, SELECT_POST, SELECT_POST_USERNAME, SELECT_POST_CHOICE, LOGIN_USER_PASSWORD, ADD_COMMENT, ADD_FRIEND, BLOCK, REMOVE_FRIEND
    }

    // Accepted User Commands
    String[] commands = {
            "help",
            "Create user",
            "Login user",
            "Create post",
            "Select post",
            "Upvote",
            "Downvote",
            "Comment",
            "exit",
            "add friend",
            "block",
            "remove friend",
            "remove user"
    };

}
