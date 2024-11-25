package src;

import java.util.ArrayList;

public class Comment implements  CommentInterface{
    private String commentText;
    private int upvotes;
    private int downvotes;
    private boolean isVisible;
    private String postOwner;
    private String commentOwner;

    public Comment(String commentText, int upvotes, int downvotes, boolean isVisible, String postOwner, String commentOwner) {
        this.commentText = commentText;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.isVisible = isVisible;
        this.postOwner = postOwner;
        this.commentOwner = commentOwner;
    }

    public String getCommentText() {
        return commentText;
    }
    public void setCommentText(String commentText) {
        this.commentText = commentText;
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
    public String getPostOwner() {
        return postOwner;
    }
    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }
    public String getCommentOwner() {
        return commentOwner;
    }
    public void setCommentOwner(String commentOwner) {this.commentOwner = commentOwner;}

    public String toString(){
        return commentText + Constants.COMMENT_DELIMITER + Integer.toString(upvotes) + Constants.COMMENT_DELIMITER + Integer.toString(downvotes) + Constants.COMMENT_DELIMITER + Boolean.toString(isVisible) + Constants.COMMENT_DELIMITER + postOwner + Constants.COMMENT_DELIMITER + commentOwner;
    }

    public static Comment parseComment(String s) {
        String[] parsed = s.split(Constants.COMMENT_DELIMITER);
        String commentText = parsed[0];
        int upvote = Integer.parseInt(parsed[1]);
        int downvote = Integer.parseInt(parsed[2]);
        boolean isVisible = Boolean.parseBoolean(parsed[3]);
        String postOwner  = parsed[4];
        String commentOwner = parsed[5];
        return new Comment(commentText, upvote, downvote, isVisible, postOwner, commentOwner);
    }
}