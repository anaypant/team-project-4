package src;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {
    private static int idCounter = 0;
    private int id;
    private User creator; // Holds User
    private String caption; // Caption of post
    private String url; // URL to post Image
    private String dateCreated; // Date the post was created
    private int upVotes; // Number of up votes
    private int downVotes; // Number of down votes
    private ArrayList<String> comments; // An Array List of Comments on the POst

    public Post() {
        this.id = idCounter++;
        this.creator = null;
        this.caption = null;
        this.url = null;
        this.dateCreated = null;
        this.upVotes = 0;
        this.downVotes = 0;
        this.comments = null;
    }

    public Post(User creator, String caption, String url, String dateCreated) {
        this.id = idCounter++;
        this.creator = creator;
        this.caption = caption;
        this.url = url;
        this.dateCreated = dateCreated;
        this.upVotes = 0;
        this.downVotes = 0;
        this.comments = new ArrayList<>();
    }
    public int getID() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Post) {
            Post newo = (Post) o;
            if (this.id == newo.getID()) {
                return true;
            }
        }
        return false;
    }

}
