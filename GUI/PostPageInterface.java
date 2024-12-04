package GUI;

import javax.swing.*;
import javax.swing.text.StyledDocument;

public interface PostPageInterface {
    // method that handles the setup of the JFrames
    void init();

    // method that handles when the user is trying ypvote a post
    void upVote();

    // method that handles when the user is trying downvote a post
    void downVote();

    // method that handles when the user is trying comment on a post
    void comment();

    // method that handles when the user is trying select a comment on a post
    void selectComment();

    // method that handles when the user is trying to uvote a comment on a post
    void upVoteComment();

    // method that handles when the user is trying downvote a comment on a post
    void downVoteComment();

    // method that handles when the user is trying to hife a post from other users
    void hidePost();

    // method that handles when the user is trying to unhide a hidden post
    void unhidePost();

    // method that handles when the user is trying to enable comment on one of their own post (default true)
    void enableComments();

    // method that handles when the user is trying to disable comment when comments are enabled
    void disableComments();

    // method that handles when the user is trying return to the previous screen
    void ret();

    // method that handles when the client is trying  to send a message to a server
    void sendMessage();

    // method that handles when the client is tryng to get a specific frame
    StyledDocument getStyledDocument();

    // method that handles when the client is setting the position of a JFrame
    void setCaretPosition(int pos);

    // method that handles when the client is setting a certain frame
    void insertComponent(JLabel imageLabel);
}
