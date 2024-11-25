package src;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;          // Unique ID for the comment
    private String text;        // Content of the comment
    private String creator;     // Creator of the comment
    private int upvotes;        // Number of upvotes
    private int downvotes;      // Number of downvotes
    private boolean hidden;     // Flag for whether the comment is hidden

    // Constructor
    public Comment(String creator, String text) {
        this.id = java.util.UUID.randomUUID().toString();
        this.text = text;
        this.creator = creator;
        this.upvotes = 0;
        this.downvotes = 0;
        this.hidden = false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getText() {
        if (hidden) {
            return "[Hidden]";
        } else {
            return text;
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreator() {
        return creator;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public boolean isHidden() {
        return hidden;
    }

    // Functional methods
    public void upvote() {
        upvotes++;
    }

    public void downvote() {
        downvotes++;
    }

    public void hide(String user) {
        if (user.equals(creator)) {
            hidden = true;
        } else {
            throw new IllegalArgumentException("Only the comment owner can hide this comment.");
        }
    }

    public void unhide(String user) {
        if (user.equals(creator)) {
            hidden = false;
        } else {
            throw new IllegalArgumentException("Only the comment owner can unhide this comment.");
        }
    }


    @Override
    public String toString() {
        if (hidden) {
            return "[Hidden]";
        } else {
            return text + " (Upvotes: " + upvotes + ", Downvotes: " + downvotes + ")";
        }    }
}
