package src;

import java.io.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class that defines how the database deals with Post methods
 * Creating posts, deleting posts, commenting on posts, deleting comments on posts, upvoting, downvoting.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class PostDBDatabase implements PostDBInterface {
    private static BufferedReader bfr;
    private static PrintWriter pw;
    private static String filename = Constants.POST_DATABASE_PATH;

    private synchronized static void initialize() {
        if (bfr == null) {
            try {
                bfr = new BufferedReader(new FileReader(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (pw == null) {
            try {
                pw = new PrintWriter(new FileWriter(filename, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized static void close() {
        if (bfr != null) {
            try {
                bfr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bfr = null;
        }
        if (pw != null) {
            pw.close();
            pw = null;
        }
    }

    // Gathers user's username, post content and image to create post
    public static synchronized boolean createPost(String username, String content, String image) {
        try {
            initialize();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date()); // Adds date to post

            Post p = new Post(username, content, image, date); // Formatting for post
            System.out.println(p);
            pw.println(p);
            close();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized boolean createPost(Post p) {
        try {
            initialize();
            pw.println(p);
            close();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Allows user to create a post
    public static synchronized boolean createPost(String username, String content) {
        return createPost(username, content, null);
    }

    // Allows user to search for a post
    public static synchronized Post selectPost(String username, int index) {
        ArrayList<Post> userPosts = getPostsByUsername(username);
        if (index >= 0 && index < userPosts.size()) {
            return userPosts.get(index);
        } else {
            System.out.println("Invalid post selection.");
            return null;
        }

    }

    // Allows user to choose a post to delete
    private static synchronized Post getAndDeletePost(String postId) {
        // this needs heavy checking
        // find a post and remove the line
        initialize();
        try {
            String line = bfr.readLine();
            while (line != null) {
                if (line.split(Constants.DELIMITER)[0].equals(postId)) {
                    Utils.deletePost(line.split(Constants.DELIMITER)[0], filename);
                    close();
                    return Post.parseString(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized boolean deletePost(String postId) {
        return (getAndDeletePost(postId) != null);
    }

    // Allows user to add a comment to a post, displays postId, username, and comments
    public static synchronized boolean addComment(String postId, String username, String comment) {
        Post post = getAndDeletePost(postId);
        if (post == null) {
            return false;
        }
        ArrayList<String> comments = post.getComments();
        comments.add(username + ": " + comment); // Format the comment with username and text
        post.setComments(comments);
        return createPost(post); // Save the updated post
    }

    // Allows user to delete comments from their own post, displays postId and comments
    public static synchronized boolean deleteComment(String postId, String comment) {
        Post p = getAndDeletePost(postId);
        if (p == null) {
            return false;
        }
        ArrayList<String> c = p.getComments();
        c.remove(comment);
        p.setComments(c);
        return createPost(p);
    }

    // Allows user to search through posts by specific username
    public static synchronized ArrayList<Post> getPostsByUsername(String username) {
        initialize();
        ArrayList<Post> userPosts = new ArrayList<>();
        try {
            String line;
            while ((line = bfr.readLine()) != null) {
                Post post = Post.parseString(line);
                if (post.getCreator().equals(username)) {
                    userPosts.add(post); // Post will be shown when username is matched
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return userPosts;
    }

    // Allows user to upvote a post
    public static synchronized boolean upvotePost(String postId) {
        Post post = getAndDeletePost(postId); // Fetch and delete the post from the database
        if (post != null) {
            post.setUpVotes(post.getUpVotes() + 1); // Increment the upvote count
            return createPost(post); // Save the updated post back to the database
        }
        return false;
    }

    // Allows user to downvote a post
    public static synchronized boolean downvotePost(String postId) {
        Post post = getAndDeletePost(postId); // Fetch and delete the post from the database
        if (post != null) {
            post.setDownVotes(post.getDownVotes() + 1); // Increment the downvote count
            return createPost(post); // Save the updated post back to the database
        }
        return false;
    }

    public static void setFilename(String fn) {
        filename = fn;
    }
}
