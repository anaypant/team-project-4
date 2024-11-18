package test;

import org.junit.Before;
import org.junit.Test;
import src.Post;
import src.PostDBDatabase;
import src.UserDBDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test file for PostDBDatabase
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 */

public class PostDBDatabaseTest {
    private static final String DB_PATH = "jdbc:sqlite:posts.sqlite";

    @Before
    // create table of posts and users
    // if they already exist, deletes them and recreates
    // creates two users, user1 and user2
    public void setUp() {
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             Statement stmt = conn.createStatement()) {

            // Drop and recreate the posts table
            stmt.execute("DROP TABLE IF EXISTS posts");
            stmt.execute("CREATE TABLE posts (" +
                    "id TEXT PRIMARY KEY, " +
                    "creator TEXT NOT NULL, " +
                    "caption TEXT, " +
                    "url TEXT, " +
                    "datecreated TEXT, " +
                    "upvotes INTEGER DEFAULT 0, " +
                    "downvotes INTEGER DEFAULT 0, " +
                    "comments TEXT)");

            // Drop and recreate the users table
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("CREATE TABLE users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT NOT NULL, " +
                    "friends TEXT, " +
                    "blocked TEXT)");

        } catch (Exception e) {
            throw new RuntimeException("Failed to reset the database: " + e.getMessage(), e);
        }

        // Create test users
        UserDBDatabase.createUser("user1", "password1");
        UserDBDatabase.createUser("user2", "password2");
    }

    @Test
    // tests the creation of a post with a file path
    public void testCreatePost() {
        assertTrue("Post should be created successfully",
                PostDBDatabase.createPost("user1", "This is a test post", "image.jpg"));

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("There should be one post for user1", 1, posts.size());
        assertEquals("This is a test post", posts.get(0).getCaption());
    }

    @Test
    // creates one post each for user1 and user2
    // checks if the indexing for posts work (ex: index 5 should not work for a user who only has one post)
    public void testSelectPost() {
        PostDBDatabase.createPost("user1", "First post", "image1.jpg");
        PostDBDatabase.createPost("user1", "Second post", "image2.jpg");

        Post selectedPost = PostDBDatabase.selectPost("user1", 1);
        assertNotNull("Post should be selected successfully", selectedPost);
        assertEquals("Second post", selectedPost.getCaption());

        Post invalidSelection = PostDBDatabase.selectPost("user1", 5);
        assertNull("Invalid index selection should return null", invalidSelection);
    }

    @Test
    // tests deletion of posts
    // checks that the postId is attributed to the appropriate post and that the post is actually deleted
    public void testDeletePost() {
        PostDBDatabase.createPost("user1", "This post will be deleted", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();
        boolean deleted = PostDBDatabase.deletePost(postId);

        assertTrue("Post should be deleted successfully", deleted);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertTrue("No posts should remain after deletion", posts.isEmpty());
    }

    @Test
    // creates post from user1
    // comments on post from user2
    // checks that the comment is actually posted and that the content of the comment is intact
    public void testAddComment() {
        PostDBDatabase.createPost("user1", "Post with comments", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        boolean commentAdded = PostDBDatabase.addComment(postId, "user2", "Nice post!");
        assertTrue("Comment should be added successfully", commentAdded);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("There should be one comment on the post", 1, posts.get(0).getComments().size());
        assertEquals("user2: Nice post!", posts.get(0).getComments().get(0));
    }

    @Test
    // creates a post by user1
    // adds a comment
    // deletes the comment by calling the appropriate database method
    // checks that no comments are under the post
    public void testDeleteComment() {
        PostDBDatabase.createPost("user1", "Post to delete comment", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        PostDBDatabase.addComment(postId, "user2", "Comment to delete");
        boolean commentDeleted = PostDBDatabase.deleteComment(postId, "user2: Comment to delete");

        assertTrue("Comment should be deleted successfully", commentDeleted);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertTrue("No comments should remain after deletion", posts.get(0).getComments().isEmpty());
    }

    @Test
    // creates post from user1
    // checks postId and calls appropriate database methods to upvote the post
    // checks that the upvote count actually changed
    public void testUpvotePost() {
        PostDBDatabase.createPost("user1", "Post to be upvoted", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        boolean upvoted = PostDBDatabase.upvotePost(postId);
        assertTrue("Post should be upvoted successfully", upvoted);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("Upvote count should be 1", 1, posts.get(0).getUpVotes());
    }

    @Test
    // creates post from user1
    // checks postId and calls appropriate database methods to downvote the post
    // checks that the downvote count actually changed
    public void testDownvotePost() {
        PostDBDatabase.createPost("user1", "Post to be downvoted", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        boolean downvoted = PostDBDatabase.downvotePost(postId);
        assertTrue("Post should be downvoted successfully", downvoted);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("Downvote count should be 1", 1, posts.get(0).getDownVotes());
    }

    @Test
    // creates two posts from user1 and one from user2
    // creates two ArrayLists, one for user1's posts and one for user2's
    // checks that the ArrayLists are the appropriate size, that being the number of posts for each respective user
    public void testGetPostsByUsername() {
        PostDBDatabase.createPost("user1", "User1's first post", "image1.jpg");
        PostDBDatabase.createPost("user1", "User1's second post", "image2.jpg");
        PostDBDatabase.createPost("user2", "User2's post", "image3.jpg");

        ArrayList<Post> user1Posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("There should be two posts for user1", 2, user1Posts.size());

        ArrayList<Post> user2Posts = PostDBDatabase.getPostsByUsername("user2");
        assertEquals("There should be one post for user2", 1, user2Posts.size());
    }
}
