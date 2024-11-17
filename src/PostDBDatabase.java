package src;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private static final String DB_PATH = "jdbc:sqlite:C:\\Users\\utsie\\Downloads\\sqlite-dump.db";




    // Gathers user's username, post content and image to create post
    public static synchronized boolean createPost(String username, String content, String image) {
        Post p = new Post(username,content,image,"");
        String createQuery = "INSERT INTO posts (id, creator, caption, url, datecreated, upvotes, downvotes, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement ps = conn.prepareStatement(createQuery);
            ps.setString(1, p.getId());
            ps.setString(2, username);
            ps.setString(3, content);
            ps.setString(4, image);
            ps.setString(5, p.getDateCreated().toString());
            ps.setInt(6, 0);
            ps.setInt(7, 0);
            ps.setString(8, "");
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static synchronized boolean createPost(Post p) {
        String createQuery = "INSERT INTO posts (id, creator, caption, url, datecreated,upvotes, downvotes, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String username = p.getCreator();
        String content = p.getCaption();
        String image = p.getUrl();
        int upvotes = p.getUpVotes();
        int downvotes = p.getDownVotes();
        String datecreated = p.getDateCreated();
        ArrayList<String> comments = p.getComments();
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

    // Allows user to create a post
    public static synchronized boolean createPost(String username, String content) {
        return createPost(username, content, null);
    }

    // Allows user to search for a post
    public static synchronized Post selectPost(String username, int index) {
        String selectQuery = "SELECT * FROM posts WHERE username = ? ORDER BY createdAt LIMIT 1 OFFSET ?";

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
                        String.valueOf(rs.getInt("id")),
                        rs.getString("username"),
                        rs.getString("content"),
                        rs.getString("url"),
                        rs.getString("datecreated"),
                        rs.getInt("upvotes"),
                        rs.getInt("downvotes"),
                        Utils.arrayFromString(rs.getString("comments"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Allows user to choose a post to delete
    private static synchronized Post getAndDeletePost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String deletQuery = "DELETE FROM posts WHERE  = ?";
        ArrayList<String> coments = new ArrayList<>();
        String commentList = "";


        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    commentList = result.getString(8);
                    if (commentList == null || commentList.isEmpty()) {
                        coments = new ArrayList<>();
                    } else {
                        coments = new ArrayList<>(Arrays.asList(commentList.split(":::")));
                    }

                    Post p = new Post(result.getString(1),result.getString(2),result.getString(3),result.getString(4),result.getString(5),result.getInt(6),result.getInt(7),coments);
                    PreparedStatement second  = conn.prepareStatement(deletQuery);
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

    public static synchronized boolean deletePost(String postId) {
        return (getAndDeletePost(postId) != null);
    }

    // Allows user to add a comment to a post, displays postId, username, and comments
    public static synchronized boolean addComment(String postId, String username, String comment) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET comments = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    Post p  = new Post(result.getString(1),result.getString(2),result.getString(3),result.getString(4),result.getString(5),result.getInt(6),result.getInt(7),Utils.arrayFromString(result.getString(8)));
                    ArrayList<String> comments =  p.getComments();
                    comments.add(username + ": " + comment);
                    p.setComments(comments);
                    PreparedStatement second  = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setString(1, Utils.arrListToString(p.getComments()));
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
    public static synchronized boolean deleteComment(String postId, String comment) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET comments = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    Post p  = new Post(result.getString(1),result.getString(2),result.getString(3),result.getString(4),result.getString(5),result.getInt(6),result.getInt(7),Utils.arrayFromString(result.getString(8)));
                    ArrayList<String> comments =  p.getComments();
                    comments.remove(comment);
                    p.setComments(comments);
                    PreparedStatement second  = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setString(1, Utils.arrListToString(p.getComments()));
                    second.executeUpdate();
                    return true;
                }
            }




        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // Allows user to search through posts by specific username
    public static synchronized ArrayList<Post> getPostsByUsername(String username) {
        public static synchronized ArrayList<Post> getPostsByUsername(String username) {
            String selectQuery = "SELECT * FROM posts WHERE username = ? ORDER BY createdAt;";
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
                            rs.getString("postId"),
                            rs.getString("username"),
                            rs.getString("content"),
                            rs.getString("image"),
                            rs.getString("datecreated"),
                            rs.getInt("upvotes"),
                            rs.getInt("downvotes"),
                            Utils.arrayFromString(rs.getString("comments"))
                    );
                    posts.add(post);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return posts; // Return the list of posts (empty if no posts were found)
        }


    }

    // Allows user to upvote a post
    public static synchronized boolean upvotePost(String postId) {
        String selectQuery = "SELECT * FROM posts";
        String updateQuery = "UPDATE posts SET upvotes = ? WHERE id = ?";
        int upvotes = 0;
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                if (result.getString(1).equals(postId)) {
                    Post p  = new Post(result.getString(1),result.getString(2),result.getString(3),result.getString(4),result.getString(5),result.getInt(6),result.getInt(7),Utils.arrayFromString(result.getString(8)));
                    upvotes = p.getUpVotes();
                    upvotes = upvotes + 1;
                    PreparedStatement second  = conn.prepareStatement(updateQuery);
                    second.setString(2, p.getId());
                    second.setInt(1, p.getUpVotes());
                    second.executeUpdate();
                    return true;
                }
            }




        } catch (Exception e) {
            return false;
        }
        return false;

    }

    // Allows user to downvote a post
    public static synchronized boolean downvotePost(String postId) {

    }


}
