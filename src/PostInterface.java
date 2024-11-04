package src;

import java.util.ArrayList;

/**
 * An interface that defines what the Post class will look like.
 *
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public interface PostInterface {
    // gets id
    public String getId();

    // gets post creator
    public String getCreator();

    // sets post creator
    public void setCreator(String creator);

    // gets caption of post
    public String getCaption();

    // sets post caption
    public void setCaption(String caption);

    // gets post image url
    public String getUrl();

    // sets post image url
    public void setUrl(String url);

    // gets date post was created
    public String getDateCreated();

    // sets the date that the post was created
    public void setDateCreated(String dateCreated);

    // gets number of upvotes
    public int getUpVotes();

    // sets number of upvotes
    public void setUpVotes(int upVotes);

    // gets number of downvotes
    public int getDownVotes();

    // sets number of downvotes
    public void setDownVotes(int downVotes);

    // gets list of comments
    public ArrayList<String> getComments();

    // sets list of comments
    public void setComments(ArrayList<String> comments);

}
