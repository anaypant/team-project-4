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
        ADD_FRIEND, BLOCK, REMOVE_FRIEND, UPVOTE_COMMENT, SELECT_COMMENT, DOWNVOTE_COMMENT,
        ENABLE_COMMENTS, DISABLE_COMMENTS, HIDE_POST, UNHIDE_POST, DELETE_COMMENT, DELETE_POST
    }

    // Accepted User Commands
    String[] COMMANDS = {
            "Create user",
            "Login user",
            "Create post",
            "Select post",
            "Upvote",
            "Downvote",
            "Comment",
            "Upvote comment",
            "Downvote comment",
            "Enable comments",
            "Disable comments",
            "exit",
            "add friend",
            "block",
            "remove friend",
            "remove user",
            "hide post",
            "unhide post",
            "delete post"
    };


}
