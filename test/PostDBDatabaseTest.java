package test;

import org.junit.Before;
import org.junit.Test;
import src.Post;
import src.PostDBDatabase;
import src.UserDBDatabase;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test file for PostDBDatabase
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 * @author CS180 Team 5 Lab 1
 **/

public class PostDBDatabaseTest {

    @Before
    public void setUp() {
        String filename = "test_post_db.txt";
        try {
            new FileWriter(filename, false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PostDBDatabase.setFilename(filename);
        // Create users for testing
        UserDBDatabase.createUser("user1", "password1");
        UserDBDatabase.createUser("user2", "password2");
    }

    // Test post creation confirmation
    @Test
    public void testCreatePost() {
        boolean created = PostDBDatabase.createPost("user1", "This is a test post", "image.jpg");
        assertTrue("Post should be created successfully", created);

        // gets test post
        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("There should be one post for user1", 1, posts.size());
        assertEquals("This is a test post", posts.get(0).getCaption());
    }

    // Tests post selection
    @Test
    public void testSelectPost() {
        // displays post and option to select
        PostDBDatabase.createPost("user1", "First post", "image1.jpg");
        PostDBDatabase.createPost("user1", "Second post", "image2.jpg");

        // confirmation message
        Post selectedPost = PostDBDatabase.selectPost("user1", 1);
        assertNotNull("Post should be selected successfully", selectedPost);
        assertEquals("Second post", selectedPost.getCaption());

        Post invalidSelection = PostDBDatabase.selectPost("user1", 5);
        assertNull("Invalid index selection should return null", invalidSelection);
    }

    // Tests post deletion
    @Test
    public void testDeletePost() {
        // displays post and option to delete
        PostDBDatabase.createPost("user1", "This post will be deleted", "image.jpg");

        // checks if post is deleted
        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();
        boolean deleted = PostDBDatabase.deletePost(postId);

        // confirmation message
        assertTrue("Post should be deleted successfully", deleted);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertTrue("No posts should remain after deletion", posts.isEmpty());
    }

    // Tests adding comments to posts
    @Test
    public void testAddComment() {
        // displays post and option to add comment
        PostDBDatabase.createPost("user1", "Post with comments", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        // confirmation message
        boolean commentAdded = PostDBDatabase.addComment(postId, "user2", "Nice post!");
        assertTrue("Comment should be added successfully", commentAdded);

        // test comment
        posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("There should be one comment on the post", 1, posts.get(0).getComments().size());
        assertEquals("user2: Nice post!", posts.get(0).getComments().get(0));
    }

    // Tests deleting comments from a post
    @Test
    public void testDeleteComment() {
        // displays post and option to delete comment
        PostDBDatabase.createPost("user1", "Post to delete comment", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        PostDBDatabase.addComment(postId, "user2", "Comment to delete");
        boolean commentDeleted = PostDBDatabase.deleteComment(postId, "user2: Comment to delete");

        // confirmation message
        assertTrue("Comment should be deleted successfully", commentDeleted);

        posts = PostDBDatabase.getPostsByUsername("user1");
        assertTrue("No comments should remain after deletion", posts.get(0).getComments().isEmpty());
    }

    // Tests ability to upvote a post
    @Test
    public void testUpvotePost() {
        // displays post and option to upvote
        PostDBDatabase.createPost("user1", "Post to be upvoted", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.get(0).getId();

        boolean upvoted = PostDBDatabase.upvotePost(postId);
        assertTrue("Post should be upvoted successfully", upvoted);

        // adds upvote to post
        posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("Upvote count should be 1", 1, posts.get(0).getUpVotes());
    }

    // Tests ability to downvote the post
    @Test
    public void testDownvotePost() {
        // displays post and option to downvote
        PostDBDatabase.createPost("user1", "Post to be downvoted", "image.jpg");

        ArrayList<Post> posts = PostDBDatabase.getPostsByUsername("user1");
        String postId = posts.getFirst().getId();

        boolean downvoted = PostDBDatabase.downvotePost(postId);
        assertTrue("Post should be downvoted successfully", downvoted);

        // adds downvote to post
        posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("Downvote count should be 1", 1, posts.get(0).getDownVotes());
    }

    // Tests ability to find a post by searching for the username for the post
    @Test
    public void testGetPostsByUsername() {
        // displays user and their posts
        PostDBDatabase.createPost("user1", "User1's first post", "image1.jpg");
        PostDBDatabase.createPost("user1", "User1's second post", "image2.jpg");
        PostDBDatabase.createPost("user2", "User2's post", "image3.jpg");

        ArrayList<Post> user1Posts = PostDBDatabase.getPostsByUsername("user1");
        assertEquals("There should be two posts for user1", 2, user1Posts.size());

        ArrayList<Post> user2Posts = PostDBDatabase.getPostsByUsername("user2");
        assertEquals("There should be one post for user2", 1, user2Posts.size());
    }
}
