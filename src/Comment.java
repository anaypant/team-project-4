package src;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class Comment implements CommentInterface{
    private int upvotes;
    private int downvotes;
    private String comment;
    private String creator;

    public Comment(int upvotes, int downvotes, String comment, String creator) {
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comment = comment;
        this.creator = creator;
    }

    public Comment(String comment, String creator) {
        this.comment = comment;
        this.upvotes = 0;
        this.downvotes = 0;
        this.creator = creator;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return this.creator + ": " + this.comment;
    }

    public static Comment parseCommentFromString(String c) {
        String[] parts = c.split(Constants.DELIMITER);
        return new Comment(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[1], parts[0]);
    }

    public String encode() {
        return this.creator + Constants.DELIMITER + this.comment + Constants.DELIMITER + this.upvotes + Constants.DELIMITER + this.downvotes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Comment c) {
            return c.comment.equals(this.comment) && c.upvotes == this.upvotes && c.downvotes == this.downvotes;
        }
        return false;
    }

    public String display() {
        return this.creator + ": " + this.comment + " --- Upvotes: " +
                this.upvotes + " --- Downvotes: " + this.downvotes;
    }


}
