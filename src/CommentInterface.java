package src;

public interface CommentInterface {
    int getUpvotes();

    int getDownvotes();

    String getComment();

    void setUpvotes(int upvotes);

    void setDownvotes(int downvotes);

    void setComment(String comment);

    String getCreator();

    void setCreator(String creator);

}
