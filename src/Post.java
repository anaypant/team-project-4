package src;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Post implements Serializable {
    private String id;
    private String creator; // Holds User
    private String caption; // Caption of post
    private String url; // URL to post Image
    private String dateCreated; // Date the post was created
    private int upVotes; // Number of up votes
    private int downVotes; // Number of down votes
    private ArrayList<String> comments; // An Array List of Comments on the POst

    public Post() {
        this.id = "";
        this.creator = "";
        this.caption = "";
        this.url = "";
        this.dateCreated = "";
        this.upVotes = 0;
        this.downVotes = 0;
        this.comments = new ArrayList<>();
    }

    public Post(String id, String creator, String caption, String url, String dateCreated, int upVotes, int downVotes, ArrayList<String> comments) {
        this.id = id;
        this.creator = creator;
        this.caption = caption;
        this.url = url;
        this.dateCreated = dateCreated;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.comments = comments;
    }

    public Post(String creator, String caption, String url, String dateCreated) {
        this.id = UUID.randomUUID().toString();
        this.creator = creator;
        this.caption = caption;
        this.url = url;
        this.dateCreated = dateCreated;
        this.upVotes = 0;
        this.downVotes = 0;
        this.comments = new ArrayList<>();
    }

    public String getID() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
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
            return this.id.equals(newo.getID());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.id + Constants.DELIMITER + this.creator + Constants.DELIMITER +
                this.caption + Constants.DELIMITER + this.url + Constants.DELIMITER +
                this.dateCreated + Constants.DELIMITER + this.upVotes +
                Constants.DELIMITER + this.downVotes + Constants.DELIMITER +
                this.comments.toString();
    }

    public String display() {
        String msg = "\n --- Post ID: " + this.id + " ---\n";
        msg += "Created by: " + this.creator + "\n";
        msg += "Description: " + this.caption + "\n";
        msg += "URL to image: " + this.url + "\n";
        msg += "Date Created: " + this.dateCreated + "\n";
        msg += "Number of Up Votes: " + this.upVotes + "\n";
        msg += "Number of Down Votes: " + this.downVotes + "\n";
        msg += "Comments: \n";
        for (String comment : this.comments) {
            msg += comment + "\n";
        }
        msg += "----------";
        return msg;

    }

    public static Post parseString(String s) {
        String[] parsed = s.split(Constants.DELIMITER);
        String uid = parsed[0];
        String creator = parsed[1];
        String caption = parsed[2];
        String url = parsed[3];
        String date = parsed[4];
        int uv = Integer.parseInt(parsed[5]);
        int dv = Integer.parseInt(parsed[6]);
        ArrayList<String> c = Utils.arrayFromString(parsed[7]);
        return new Post(uid, creator, caption, url, date, uv, dv, c);
    }
}
