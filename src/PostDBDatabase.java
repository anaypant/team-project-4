package src;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * A class that defines how the database deals with Post methods
 * Creating posts, deleting posts, commenting on posts,
 * deleting comments on posts, upvoting, downvoting.
 *
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class PostDBDatabase implements PostDBInterface {
    private static final String DB_PATH = Constants.POST_DB;


    // Gathers user's username, post content and image to create post
    // uses the create method from CRUD, in this case INSERT, to create a post in the database
    // uses query and gets all the properties of the post
    // before executing the query to update the database
    // returns true if successful, false otherwise
    public static synchronized boolean createPost(String username, String content, String image) {
        Post p = new Post(username, content, image, "");
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        // Adds date to post
        p.setDateCreated(date);

        String createQuery = "INSERT INTO posts (id, creator, " +
                "caption, url, datecreated, upvotes, downvotes, " +
                "comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement ps = conn.prepareStatement(createQuery);
            ps.setString(1, p.getId());
            ps.setString(2, username);
            ps.setString(3, content);
            ps.setString(4, image);
            ps.setString(5, p.getDateCreated());
            ps.setInt(6, 0);
            ps.setInt(7, 0);
            ps.setString(8, "");
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    // method creates a post using a preexisting Post object
    // in this case, uses getters for the Post object that was passed
    // in to write the query that is then called to update the database
    // returns true if successful, false otherwise
    public static synchronized boolean createPost(Post p) {
        String createQuery = "INSERT INTO posts (id, creator, caption, url, " +
                "datecreated,upvotes, downvotes, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String username = p.getCreator();
        String content = p.getCaption();
        String image = p.getUrl();
        int upvotes = p.getUpVotes();
        int downvotes = p.getDownVotes();
        String datecreated = p.getDateCreated();
        ArrayList<Comment> comments = p.getComments();
        String commentList = comments.toString();
        commentList = commentList.substring(1, commentList.length() - 1);
        commentList = commentList.replace(", ", ":::");
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement ps = conn.prepareStatement(createQuery);
            ps.setString(1, p.getId());
            ps.setString(2, username);
            ps.setString(3, content);
            ps.setString(4, image);
            ps.setString(5, datecreated);
            ps.setInt(6, upvotes);
            ps.setInt(7, downvotes);
            ps.setString(8, commentList);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Allows a given user to create a post by providing their username and content
    // Allows users to create posts without images
    public static synchronized boolean createPost(String username, String content) {
        return createPost(username, content, null);
    }

    // Allows user to search for a post
    // Creates query to obtain a post given a username and integer index
    // obtains most recent post
    public static synchronized Post selectPost(String username, int index) {
        String selectQuery = "SELECT * FROM posts WHERE creator = ? " +
                "ORDER BY datecreated DESC LIMIT 1 OFFSET ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH);
             PreparedStatement ps = conn.prepareStatement(selectQuery)) {

            // Bind parameters to the query
            ps.setString(1, username);
            ps.setInt(2, index);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // Check if a row is returned
            if (rs.next()) {
                // Create and return a Post object
                return new Post(
                        rs.getString("id"),
                        rs.getString("creator"),
                        rs.getString("caption"),
                        rs.getString("url"),
                        rs.getString("datecreated"),
                        rs.getInt("upvotes"),
                        rs.getInt("downvotes"),
                        Utils.ComarrayFromString(rs.getString("comments"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Allows user to choose a post to delete
    // uses a select and delete query to first get the posts and then
    // delete the specific post by id where id = postId
    private static synchronized Post getAndDeletePost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String deletQuery = "DELETE FROM posts WHERE id = ?";
        ArrayList<Comment> comments;
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                System.out.println("--");
                System.out.println(result.getString(1));
                System.out.println(postId);
                if (result.getString(1).equals(postId)) {
                    comments = Utils.ComarrayFromString(result.getString(8));
                    Post p = new Post(result.getString(1),
                            result.getString(2), result.getString(3),
                            result.getString(4), result.getString(5),
                            result.getInt(6), result.getInt(7), comments);
                    PreparedStatement second = conn.prepareStatement(deletQuery);
                    second.setString(1, postId);
                    second.executeUpdate();
                    return (p);
                }
            }

        } catch (SQLException e) {
            return null;
        }
        return null;

    }

    // attempts to delete a post using getAndDeletePost and returns the result
    public static synchronized boolean deletePost(String postId) {
        return (getAndDeletePost(postId) != null);
    }

    // Allows user to add a comment to a post, displays postId, username, and comments
    // uses select and update queries to search for a specific post of
    // postId and then post a comment from a given username
    public static synchronized boolean addComment(String postId, String username, String comment) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET comments = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    Post p = new Post(result.getString(1),
                            result.getString(2), result.getString(3),
                            result.getString(4), result.getString(5),
                            result.getInt(6), result.getInt(7),
                            Utils.ComarrayFromString(result.getString(8)));
                    ArrayList<Comment> comments = p.getComments();
                    comments.add(new Comment(username, comment));
                    p.setComments(comments);
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setString(1, Utils.ComarrListToString(p.getComments()));
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // Allows user to delete comments from their own post, displays postId and comments
    // find specific post by postId, then finds the comment that matches the passed in comment
    // returns true if the delete was successful, false otherwise
    public static synchronized boolean deleteComment(String postId, String comment) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET comments = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    Post p = new Post(result.getString(1),
                            result.getString(2), result.getString(3),
                            result.getString(4), result.getString(5),
                            result.getInt(6), result.getInt(7),
                            Utils.ComarrayFromString(result.getString(8)));
                    ArrayList<Comment> comments = p.getComments();
                    comments.remove(comment);
                    p.setComments(comments);
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setString(1, Utils.ComarrListToString(p.getComments()));
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static synchronized boolean deletePostsByUsername(String username) {
        String delQuery = "DELETE FROM posts WHERE creator = ?;";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {


            PreparedStatement second = conn.prepareStatement(delQuery);
            second.setString(1, username);
            second.executeUpdate();

            return true;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return the list of posts (empty if no posts were found)
    }

    // Allows user to search through posts by specific username
    // for each post in the database from a specific user,
    // the post is added to an ArrayList of Post objects
    // returns the list of Post objects
    public static synchronized ArrayList<Post> getPostsByUsername(String username) {
        String selectQuery = "SELECT * FROM posts WHERE creator = ? ORDER BY datecreated;";
        ArrayList<Post> posts = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {

            // Bind the username parameter to the query
            pstmt.setString(1, username);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // Iterate over the result set and populate the posts list
            while (rs.next()) {
                Post post = new Post(
                        rs.getString("id"),
                        rs.getString("creator"),
                        rs.getString("caption"),
                        rs.getString("url"),
                        rs.getString("datecreated"),
                        rs.getInt("upvotes"),
                        rs.getInt("downvotes"),
                        Utils.ComarrayFromString(rs.getString("comments"))
                );
                posts.add(post);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts; // Return the list of posts (empty if no posts were found)
    }


    // Allows user to upvote a post
    // gets Post object by calling getAndDeletePost
    // increments the number of upvotes by calling the respective methods
    // recreates Post that was deleted
    public static synchronized boolean upvotePost(String postId) {
        Post p = getAndDeletePost(postId);
        System.out.println(p);
        if (p == null) {
            return false;
        }
        p.setUpVotes(p.getUpVotes() + 1);
        System.out.println(p);
        return createPost(p);

    }

    // Allows user to downvote a post
    // gets Post object by calling getAndDeletePost
    // decrements the number of upvotes by calling the respective methods
    // recreates Post that was deleted
    public static synchronized boolean downvotePost(String postId) {
        Post p = getAndDeletePost(postId);
        if (p == null) {
            return false;
        }
        p.setDownVotes(p.getDownVotes() + 1);
        return createPost(p);
    }


    public static boolean hidePost(String postId, String user) {
        Post post = getAndDeletePost(postId); // Retrieve the post
        if (post == null) {
            return false;
        }

        try {
            post.hide(user);
            String updateQuery = "UPDATE posts SET hidden = true WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_PATH)) {
                PreparedStatement ps = conn.prepareStatement(updateQuery);
                ps.setString(1, postId);
                ps.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Upvote a comment
    public static boolean upvoteComment(String postId, String commentId) {
        Post post = getAndDeletePost(postId); // Retrieve and delete the post from the database
        if (post == null) {
            return false;
        }

        try {
            // Find and upvote the comment
            post.upvoteComment(commentId);

            // Save the updated post back to the database
            saveUpdatedPost(post);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean downvoteComment(String postId, String commentId) {
        String selectQuery = "SELECT comments FROM posts WHERE id = ?";
        String updateQuery = "UPDATE posts SET comments = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            // Step 1: Retrieve the comments for the post
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, postId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Deserialize comments into a list of Comment objects
                String commentsJson = rs.getString("comments");
                ArrayList<Comment> comments = Utils.commentsFromJson(commentsJson);

                // Step 2: Find the comment and update its downvotes
                boolean commentFound = false;
                for (Comment comment : comments) {
                    if (comment.getId().equals(commentId)) {
                        comment.downvote();
                        commentFound = true;
                        break;
                    }
                }

                if (!commentFound) {
                    return false; // Comment not found
                }

                // Step 3: Serialize the updated comments back to JSON
                String updatedCommentsJson = Utils.commentsToJson(comments);

                // Step 4: Update the database with the new comments
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, updatedCommentsJson);
                updateStmt.setString(2, postId);
                updateStmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Failed to update comment
    }

    private static boolean saveUpdatedPost(Post post) {
        String updateQuery = "UPDATE posts SET caption = ?, url = ?, datecreated = ?, upvotes = ?, downvotes = ?, comments = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement ps = conn.prepareStatement(updateQuery);

            // Prepare the updated post data
            ps.setString(1, post.getCaption());
            ps.setString(2, post.getUrl());
            ps.setString(3, post.getDateCreated());
            ps.setInt(4, post.getUpVotes());
            ps.setInt(5, post.getDownVotes());
            ps.setString(6, Utils.commentsToJson(post.getComments())); // Convert comments to JSON
            ps.setString(7, post.getId());

            // Execute the update query
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unhidePost(String postId, String user) {
        String selectQuery = "SELECT * FROM posts WHERE id = ?";
        String updateQuery = "UPDATE posts SET hidden = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            // Step 1: Retrieve the post
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, postId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Step 2: Check if the user is the owner
                String creator = rs.getString("creator");
                if (!creator.equals(user)) {
                    return false; // Only the owner can unhide the post
                }

                // Step 3: Update the post's hidden status
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setBoolean(1, false); // Set hidden = false
                updateStmt.setString(2, postId);
                updateStmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Return false if the operation fails
    }



}
