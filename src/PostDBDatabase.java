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
 * @author CS180 L2 Team 5
 * @version 2.0
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
                "comments, commentsEnabled, hidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(9, "true");
            ps.setString(10, "false");
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    // method creates a post using a preexisting Post object
    // in this case, uses getters for the Post object that was passed
    // in to write the query that is then called to update the database
    // returns true if successful, false otherwise
    public static synchronized boolean createPost(Post p) {
        String createQuery = "INSERT INTO posts (id, creator, caption, url, " +
                "datecreated,upvotes, downvotes, comments, commentsEnabled, hidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String username = p.getCreator();
        String content = p.getCaption();
        String image = p.getUrl();
        int upvotes = p.getUpVotes();
        int downvotes = p.getDownVotes();
        String datecreated = p.getDateCreated();
        ArrayList<Comment> comments = p.getComments();
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement ps = conn.prepareStatement(createQuery);
            ps.setString(1, p.getId());
            ps.setString(2, username);
            ps.setString(3, content);
            ps.setString(4, image);
            ps.setString(5, datecreated);
            ps.setInt(6, upvotes);
            ps.setInt(7, downvotes);
            ps.setString(8, Utils.arrListCommentToString(comments));
            ps.setString(9, "true");
            ps.setString(10, "false");
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
    public static synchronized Post selectPost(String username, int index, String activeUser) {
        ArrayList<Post> posts = getPostsByUsername(username, activeUser);
        if (posts.isEmpty()) {
            return null;
        }
        return posts.get(index);
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
                if (result.getString(1).equals(postId)) {
                    comments = Utils.arrayCommentFromString(result.getString(8));
                    Post p = new Post(result.getString(1),
                            result.getString(2), result.getString(3),
                            result.getString(4), result.getString(5),
                            result.getInt(6), result.getInt(7), comments,
                            Boolean.parseBoolean(result.getString(9)),
                            Boolean.parseBoolean(result.getString(10))
                    );
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
        Post p = getAndDeletePost(postId);
        return (p != null);
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
                            Utils.arrayCommentFromString(result.getString(8)),
                            Boolean.parseBoolean(result.getString(9)),
                            Boolean.parseBoolean(result.getString(10))
                    );
                    ArrayList<Comment> comments = p.getComments();
                    comments.add(new Comment(comment, username));
                    p.setComments(comments);
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setString(1, Utils.arrListCommentToString(p.getComments()));
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
    public static synchronized boolean deleteComment(String postId, String commenter, String comment) {
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
                            Utils.arrayCommentFromString(result.getString(8)),
                            Boolean.parseBoolean(result.getString(9)),
                            Boolean.parseBoolean(result.getString(10))
                    );
                    ArrayList<Comment> comments = p.getComments();
                    for (int c = 0; c < comments.size(); c++) {
                        if (comments.get(c).getComment().equals(comment) &&
                                comments.get(c).getCreator().equals(commenter)) {
                            comments.remove(c);
                            c--;
                        }
                    }
                    p.setComments(comments);
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setString(1, Utils.arrListCommentToString(p.getComments()));
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            System.out.println("excption delete comments");
            e.printStackTrace();
            return false;
        }
        System.out.println("missed delete comments");
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
    public static synchronized ArrayList<Post> getPostsByUsername(String username, String activeUser) {
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
                        Utils.arrayCommentFromString(rs.getString("comments")),
                        Boolean.parseBoolean(rs.getString("commentsEnabled")),
                        Boolean.parseBoolean(rs.getString("hidden"))
                );
                if (!post.isHidden() || post.getCreator().equals(activeUser)) {
                    posts.add(post);
                }
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
        System.out.println(p.getComments());
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

    public static synchronized boolean getCommentsEnabled(String postId) {
        String selectQuery = "SELECT * FROM posts";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    return (result.getString(9).equals("true"));
                }
            }

        } catch (SQLException e) {
            return false;
        }
        return false;

    }

    public static synchronized boolean enableComments(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET commentsEnabled = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, postId);
                    second.setString(1, "true");
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static synchronized boolean disableComments(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET commentsEnabled = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, postId);
                    second.setString(1, "false");
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static synchronized ArrayList<Comment> getCommentsFromPost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    return Utils.arrayCommentFromString(result.getString("comments"));
                }
            }


        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static synchronized boolean upvoteComment(String postId, Comment targetComment) {
        return modifyCommentVotes(postId, targetComment, true);
    }

    public static synchronized boolean downvoteComment(String postId, Comment targetComment) {
        return modifyCommentVotes(postId, targetComment, false);
    }

    private static synchronized boolean modifyCommentVotes(String postId, Comment targetComment, boolean upvote) {
        String selectQuery = "SELECT * FROM posts WHERE id = ?";
        String updateQuery = "UPDATE posts SET comments = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, postId);
            ResultSet result = selectStmt.executeQuery();

            if (result.next()) {
                ArrayList<Comment> comments = Utils.arrayCommentFromString(result.getString("comments"));
                for (Comment comment : comments) {
                    if (comment.equals(targetComment)) {
                        if (upvote) {
                            comment.setUpvotes(comment.getUpvotes() + 1);
                        } else {
                            comment.setDownvotes(comment.getDownvotes() + 1);
                        }
                    }
                }

                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, Utils.arrListCommentToString(comments));
                updateStmt.setString(2, postId);
                updateStmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized String getCreatorOfPost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    return result.getString(2);
                }
            }


        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static synchronized boolean hidePost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET hidden = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, postId);
                    second.setString(1, "true");
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static synchronized boolean unhidePost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET hidden = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    PreparedStatement second = conn.prepareStatement(updateQuery);
                    second.setString(2, postId);
                    second.setString(1, "false");
                    second.executeUpdate();
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
