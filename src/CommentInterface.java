package src;

/**
 * Class to describe the Comments
 * Author, caption, upvotes, downvotes
 *
 * @author cs180 Lab 2 Team 5
 * @version 1
 */

public interface CommentInterface {
    //gets the upvotes for a comment
    int getUpvotes();

    //gets the downvotes for a comment
    int getDownvotes();

    //gets the comment text for a comment
    String getComment();

    //sets the upvotes for a comment
    void setUpvotes(int upvotes);

    //gsets the downvotes for a comment
    void setDownvotes(int downvotes);

    //sets the comment text for a comment
    void setComment(String comment);

    //gets the creator of a comment
    String getCreator();

    //sets the creator of a comment
    void setCreator(String creator);

}
