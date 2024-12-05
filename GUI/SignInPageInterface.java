package GUI;

import javax.swing.text.StyledDocument;

/**
 * Interface to describe the Sign In Page of the GUI
 * Handles displaying the log in page, creating users, validating login details
 */

public interface SignInPageInterface {
    // method that handles when the user is trying to create a user
    void createUser();

    // method that handles when the user is trying to login
    void loginUser();

    // method that handles when the client is trying  gets this specific frame
    StyledDocument getStyledDocument();

    // method that handles when the client is setting the position of this frme
    void setCaretPosition(int position);
}
