package GUI;

import javax.swing.text.BadLocationException;

public interface SocialMediaInterface {
    // method that helps the main GUI handle the create user function
    void handleCreateUser(String username, String password);

    // method that helps the main GUI handle the login user function
    void handleLoginUser(String username, String password);

    // method that helps the main GUI handle the add friend function
    void handleAddFriend(String active, String target);

    // method that helps the main GUI handle the remove friend function
    void handleRemoveFriend(String active, String target);

    // method that helps the main GUI handle the block user function
    void handleBlockUser(String active, String target);

    // method that helps the main GUI handle the upvote post function
    void handleUpvote();

    // method that helps the main GUI handle the downvote post function
    void handleDownvote();

    // method that helps the main GUI handle the comment post function
    void handleComment(String comment);

    // method that helps the main GUI handle the select comment on a post function
    void handleSelectComment();

    // method that helps the main GUI handle the upvote comment on a post function
    void handleUpVoteComment();

    // method that helps the main GUI handle the downvote comment on a post function
    void handleDownVoteComment();

    // method that helps the main GUI handle the hide post function
    void handleHidePost();

    // method that helps the main GUI handle the unhide post function
    void handleUnhidePost();

    // method that helps the main GUI handle the enable comment on a post function
    void handleEnableComments();

    // method that helps the main GUI handle the disable comment on a post function
    void handleDisableComments();

    // method that helps the main GUI handle the server connection
    void pollServer();

    // method that helps the main GUI handle the display a post image function
    void displayImg(String imgPath, boolean postPage);

    // method that helps the main GUI handle the server responses
    void processServerResponse(String response) throws BadLocationException;

    // method that helps the main GUI handle messages to the server
    void sendMessage(String message);

    // method that helps the main GUI handle the resseting connection
    void reset(boolean delete);

    // method that helps the main GUI handle the return functions
    void returnMainPage();

    // method that helps the main GUI handle the current user
    String getActiveUser();

    // method that helps the main GUI handle the current post
    String getSelectedPost();


}
