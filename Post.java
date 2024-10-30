import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {
    private User creator; // Holds User
    private String caption; // Caption of post
    private String url; // URL to post Image
    private String dateCreated; // Date the post was created
    private int upVotes; // Number of up votes
    private int downVotes; // Number of down votes
    private ArrayList<String> comments; // An Array List of Comments on the POst

    public Post() {
        this.creator = null;
        this.caption = null;
        this.url = null;
        this.dateCreated = null;
        this.upVotes = 0;
        this.downVotes = 0;
        this.comments = null;
    }

    public Post(User creator, String caption, String url, String dateCreated) {
        this.creator = creator;
        this.caption = caption;
        this.url = url;
        this.dateCreated = dateCreated;
        this.upVotes = 0;
        this.downVotes = 0;
        this.comments = new ArrayList<>();
    }
}
