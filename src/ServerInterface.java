package src;

/**
 * An Interface that describes field functionality for the Server class
 * Commands: Creating posts, deleting posts,
 * commenting on posts, deleting comments on posts,
 * upvoting, downvoting. *
 *
 * @author CS180 L2 Team 5
 * @version 2.0
 **/
public interface ServerInterface {
    // All possible different states that can be achieved
    enum State {
        IDLE, CREATE_USER, LOGIN_USER, CREATE_POST, CREATE_POST_IMG, SELECT_POST,
        SELECT_POST_USERNAME, SELECT_POST_CHOICE, LOGIN_USER_PASSWORD, ADD_COMMENT,
        ADD_FRIEND, BLOCK, REMOVE_FRIEND
    }

    // Accepted User Commands
    String[] COMMANDS = {
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
