package test;

import org.junit.Before;
import org.junit.Test;
import src.Constants;
import src.Post;
import src.Comment;
import src.PostDBDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test file for PostDBDatabase
 *
 * @version 2.0
 */
public class PostDBDatabaseTest {
    private static final String DB_PATH = Constants.POST_DB;

    @Before
    public void setUp() {
        try (Connection conn = DriverManager.getConnection(DB_PATH);
             Statement stmt = conn.createStatement()) {

            // Delete all rows from the posts table
            stmt.execute("DELETE FROM posts");

            // Ensure the table schema exists
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS posts (
                    id TEXT PRIMARY KEY,
                    creator TEXT NOT NULL,
                    caption TEXT,
                    url TEXT,
                    datecreated TEXT,
                    upvotes INTEGER,
                    downvotes INTEGER,
                    comments TEXT,
                    commentsEnabled TEXT
                );
            """;
            stmt.execute(createTableQuery);

        } catch (Exception e) {
            throw new RuntimeException("Failed to reset the database: " + e.getMessage(), e);
        }
    }

    @Test
    public void testCreatePostWithFields() {
        assertTrue("Post should be created successfully",
                PostDBDatabase.createPost("TestUser", "Test Caption", "test.jpg"));

        Post retrievedPost = PostDBDatabase.selectPost("TestUser", 0, "TestUser");
        assertNotNull("Post should be retrievable from the database", retrievedPost);
        assertEquals("TestUser", retrievedPost.getCreator());
        assertEquals("Test Caption", retrievedPost.getCaption());
        assertEquals("test.jpg", retrievedPost.getUrl());
    }

    @Test
    public void testCreatePostWithoutImage() {
        assertTrue("Post without an image should be created successfully",
                PostDBDatabase.createPost("TestUser", "Test Caption"));

        Post retrievedPost = PostDBDatabase.selectPost("TestUser", 0, "TestUser");
        assertNotNull("Post should be retrievable from the database", retrievedPost);
        assertEquals("TestUser", retrievedPost.getCreator());
        assertEquals("Test Caption", retrievedPost.getCaption());
        assertNull("Post URL should be null", retrievedPost.getUrl());
    }

    @Test
    public void testDeletePost() {
        Post post = new Post("TestUser", "Post to Delete", null, "11-25-2024");
        PostDBDatabase.createPost(post);

        assertTrue("Post should be deleted successfully", PostDBDatabase.deletePost(post.getId()));

        Post retrievedPost = PostDBDatabase.selectPost("TestUser", 0, "TestUser");
        assertNull("Deleted post should not exist in the database", retrievedPost);
    }

    @Test
    public void testAddComment() {
        Post post = new Post("TestUser", "Post with Comments", null, "11-25-2024");
        PostDBDatabase.createPost(post);

        assertTrue("Comment should be added successfully",
                PostDBDatabase.addComment(post.getId(), "Commenter", "This is a comment"));

        ArrayList<Comment> comments = PostDBDatabase.getCommentsFromPost(post.getId());
        assertNotNull("Comments should be retrievable", comments);
        assertEquals(1, comments.size());
        assertEquals("This is a comment", comments.get(0).getComment());
        assertEquals("Commenter", comments.get(0).getCreator());
    }

    @Test
    public void testDeleteComment() {
        Post post = new Post("TestUser", "Post with Comments", null, "11-25-2024");
        PostDBDatabase.createPost(post);

        PostDBDatabase.addComment(post.getId(), "Commenter", "This is a comment");

        assertTrue("Comment should be deleted successfully",
                PostDBDatabase.deleteComment(post.getId(), "Commenter", "This is a comment"));

        ArrayList<Comment> comments = PostDBDatabase.getCommentsFromPost(post.getId());
        assertNotNull("Comments should be retrievable", comments);
        assertEquals(0, comments.size());
    }

    @Test
    public void testUpvotePost() {
        Post post = new Post("TestUser", "Upvote Test", null, "11-25-2024");
        PostDBDatabase.createPost(post);

        assertTrue("Post should be upvoted successfully", PostDBDatabase.upvotePost(post.getId()));

        Post retrievedPost = PostDBDatabase.selectPost("TestUser", 0, "TestUser");
        assertEquals(1, retrievedPost.getUpVotes());
    }

    @Test
    public void testDownvotePost() {
        Post post = new Post("TestUser", "Downvote Test", null, "11-25-2024");
        PostDBDatabase.createPost(post);

        assertTrue("Post should be downvoted successfully", PostDBDatabase.downvotePost(post.getId()));

        Post retrievedPost = PostDBDatabase.selectPost("TestUser", 0, "TestUser");
        assertEquals(1, retrievedPost.getDownVotes());
    }

    @Test
    public void testEnableAndDisableComments() {
        Post post = new Post("TestUser", "Toggle Comments", null, "11-25-2024");
        PostDBDatabase.createPost(post);

        assertTrue("Comments should be disabled successfully", PostDBDatabase.disableComments(post.getId()));
        assertFalse("Comments should be disabled", PostDBDatabase.getCommentsEnabled(post.getId()));

        assertTrue("Comments should be enabled successfully", PostDBDatabase.enableComments(post.getId()));
        assertTrue("Comments should be enabled", PostDBDatabase.getCommentsEnabled(post.getId()));
    }

    @Test
    public void testGetPostsByUsername() {
        PostDBDatabase.createPost("TestUser", "First Post", null);
        PostDBDatabase.createPost("TestUser", "Second Post", null);

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("TestUser", "TestUer1");
        assertNotNull("Posts should be retrievable", posts);
        assertEquals(2, posts.size());
    }

    @Test
    public void testDeletePostsByUsername() {
        PostDBDatabase.createPost("TestUser", "First Post", null);
        PostDBDatabase.createPost("TestUser", "Second Post", null);

        assertTrue("Posts should be deleted successfully by username",
                PostDBDatabase.deletePostsByUsername("TestUser"));

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("TestUser", "Test");
        assertEquals("No posts should remain for the user", 0, posts.size());
    }
}
