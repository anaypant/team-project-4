package src;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A class that represents each social media post, either an image or text post.
 * Each post has a creator, id, caption, and url
 *
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class Post implements Serializable, PostInterface {
    private final String id;
    private String creator; // Holds User
    private String caption; // Caption of post
    private String url; // URL to post Image
    private String dateCreated; // Date the post was created
    private int upVotes; // Number of up votes
    private int downVotes; // Number of down votes
    private ArrayList<Comment> comments; // An Array List of Comments on the POst
    private boolean canComment;

    //Constructor for post that takes an id, creator, caption,
    // url (for image), date created all in forms of Strings
    //the upvotes downvotes are ints and the comments are an ArrayList
    // and then intializes them to theiur given feilds
    //use this version when we parse
    public Post(String id, String creator, String caption, String url,
                String dateCreated, int upVotes, int downVotes,
                ArrayList<Comment> comments) {
        this.id = id;
        this.creator = creator;
        this.caption = caption;
        this.url = url;
        this.dateCreated = dateCreated;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.comments = comments;
    }

    // this post is for when creating a new post and
    // takes the fields of creators caption url (Imagelink) and dateCreated
    // and initliazes the idd to a random generated id,
    // and all other feilds to normal, and the feilds not
    //taken as paramters are initialized to 0 or empty.
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

    //takes no inputs and just returns the Posts id in the form of a String
    public String getId() {
        return id;
    }

    //takes no inputs and just returns the creator id in the form of a String
    public String getCreator() {
        return creator;
    }

    //takes an input of a creator in the form of a String
    // and sets the creator of the post to the given
    //creator specified and returns nothing.
    public void setCreator(String creator) {
        this.creator = creator;
    }

    //takes no inputs and just returns the caption in the form of a String
    public String getCaption() {
        return caption;
    }

    //takes an input of a caption in the form of a String
    // and sets the caption of the post to the given
    //caption specified and returns nothing.
    public void setCaption(String caption) {
        this.caption = caption;
    }

    //takes no inputs and just returns the url in the form of a String
    public String getUrl() {
        return url;
    }

    //takes an input of a caption in the form of a String and
    // sets the caption of the post to the given
    //caption specified and returns nothing.
    public void setUrl(String url) {
        this.url = url;
    }

    //takes no inputs and just returns the date in the form of a String
    public String getDateCreated() {
        return dateCreated;
    }

    //takes an input of a date in the form of a String and sets the date of the post to the given
    //date specified and returns nothing.
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    //takes no inputs and just returns the number of upvotes in the form of a integer
    public int getUpVotes() {
        return upVotes;
    }

    //takes an input of a number of upvotes in the form of an
    // int and sets the number of the post to the given
    //number specified and returns nothing.
    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    //takes no inputs and just returns the number of  down votes in the form of a integer
    public int getDownVotes() {
        return downVotes;
    }

    //takes an input of a number of down votes in the form
    // of an int and sets the number of the post to the given
    //number specified and returns nothing.
    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    //
    public ArrayList<Comment> getComments() {
        return comments;
    }

    //takes an input of a comment in the form of an ArrayList
    // of Strings and sets the comments to the given
    //comments specified and returns nothing.
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    //overriding the equals() method and checking if the ids opf
    // two posts are equal so that we can check if
    //two posts are equal.
    @Override
    public boolean equals(Object o) {
        if (o instanceof Post newo) {
            return (this.id.equals(newo.getId()));
        }
        return false;
    }

    //overriding the toString() method to make it our own using
    // our own delimter of ":::" so that we can parse
    //through it later and returns the String in the form of id ,
    // creator, dateCreated,upvotes,downVotes,comments
    @Override
    public String toString() {
        return this.id + Constants.DELIMITER + this.creator + Constants.DELIMITER +
                this.caption + Constants.DELIMITER + this.url + Constants.DELIMITER +
                this.dateCreated + Constants.DELIMITER + this.upVotes +
                Constants.DELIMITER + this.downVotes + Constants.DELIMITER +
                Utils.ComarrListToString(this.comments);
    }

    //just another toString() but in our own format and returns our speicial format
    public String display() {
        String msg = "\n --- Post ID: " + this.id + " ---\n";
        msg += "Created by: " + this.creator + "\n";
        msg += "Description: " + this.caption + "\n";
        msg += "URL to image: " + this.url + "\n";
        msg += "Date Created: " + this.dateCreated + "\n";
        msg += "Number of Up Votes: " + this.upVotes + "\n";
        msg += "Number of Down Votes: " + this.downVotes + "\n";
        msg += "Comments: \n";
        for (Comment comment : this.comments) {
            msg += comment.getCommentText() + String.valueOf(comment.getUpvotes()) + String.valueOf(comment.getDownvotes()) + "\n";
        }
        msg += "----------";
        return msg;

    }

    //parses the post in the given format of the toString() and sets
    // each variable to its respective parts and
    // and returns the Post based off the variables
    /*public static Post parseString(String s) {
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
    }*/

}
