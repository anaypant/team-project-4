package src;

public class Comment implements  CommentInterface{
    private String comment;
    private int upvotes;
    private int downvotes;
    private boolean isVisible;
    private User postOwner;
    private User commentOwner;

    public Comment(String comment, int upvotes, int downvotes, boolean isVisible, User postOwner, User commentOwner) {
        this.comment = comment;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.isVisible = isVisible;
        this.postOwner = postOwner;
        this.commentOwner = commentOwner;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getUpvotes() {
        return upvotes;
    }
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }
    public int getDownvotes() {
        return downvotes;
    }
    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }
    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    public User getPostOwner() {
        return postOwner;
    }
    public void setPostOwner(User postOwner) {
        this.postOwner = postOwner;
    }
    public User getCommentOwner() {
        return commentOwner;
    }
}
