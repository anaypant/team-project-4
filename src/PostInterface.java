package src;

import java.util.ArrayList;

/**
 * An interface that defines what the Post class will look like.
 *
 *
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public interface PostInterface {
    // gets id
    String getId();

    // gets post creator
    String getCreator();

    // sets post creator
    void setCreator(String creator);

    // gets caption of post
    String getCaption();

    // sets post caption
    void setCaption(String caption);

    // gets post image url
    String getUrl();

    // sets post image url
    void setUrl(String url);

    // gets date post was created
    String getDateCreated();

    // sets the date that the post was created
    void setDateCreated(String dateCreated);

    // gets number of upvotes
    int getUpVotes();

    // sets number of upvotes
    void setUpVotes(int upVotes);

    // gets number of downvotes
    int getDownVotes();

    // sets number of downvotes
    void setDownVotes(int downVotes);

    // gets list of comments
    ArrayList<String> getComments();

    // sets list of comments
    void setComments(ArrayList<String> comments);

}
