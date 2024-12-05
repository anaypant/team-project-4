package test;

import org.junit.Before;
import org.junit.Test;
import src.Comment;
import src.Post;
import src.Constants;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test file for Post class
 *
 * @version 2.0
 */
public class PostTest {
    private Post post;
    private ArrayList<Comment> comments;

    @Before
    public void setUp() {
        comments = new ArrayList<>();
        comments.add(new Comment(5, 1, "Great post!", "UserA"));
        comments.add(new Comment(3, 0, "Nice picture", "UserB"));

        post = new Post("12345", "TestUser", "Test Caption", "test.jpg",
                "11-25-2024", 10, 2, comments, true, false);
    }

    @Test
    public void testConstructorWithId() {
        assertEquals("12345", post.getId());
        assertEquals("TestUser", post.getCreator());
        assertEquals("Test Caption", post.getCaption());
        assertEquals("test.jpg", post.getUrl());
        assertEquals("11-25-2024", post.getDateCreated());
        assertEquals(10, post.getUpVotes());
        assertEquals(2, post.getDownVotes());
        assertEquals(comments, post.getComments());
        assertTrue(post.isCommentsEnabled());
    }

    @Test
    public void testConstructorWithoutId() {
        Post newPost = new Post("AnotherUser", "Another Caption", "another.jpg", "11-26-2024");

        assertNotNull(newPost.getId());
        assertEquals("AnotherUser", newPost.getCreator());
        assertEquals("Another Caption", newPost.getCaption());
        assertEquals("another.jpg", newPost.getUrl());
        assertEquals("11-26-2024", newPost.getDateCreated());
        assertEquals(0, newPost.getUpVotes());
        assertEquals(0, newPost.getDownVotes());
        assertTrue(newPost.getComments().isEmpty());
        assertTrue(newPost.isCommentsEnabled());
    }

    @Test
    public void testSettersAndGetters() {
        post.setCreator("UpdatedUser");
        post.setCaption("Updated Caption");
        post.setUrl("updated.jpg");
        post.setDateCreated("11-27-2024");
        post.setUpVotes(20);
        post.setDownVotes(5);

        assertEquals("UpdatedUser", post.getCreator());
        assertEquals("Updated Caption", post.getCaption());
        assertEquals("updated.jpg", post.getUrl());
        assertEquals("11-27-2024", post.getDateCreated());
        assertEquals(20, post.getUpVotes());
        assertEquals(5, post.getDownVotes());
    }

    @Test
    public void testCommentsManagement() {
        ArrayList<Comment> newComments = new ArrayList<>();
        newComments.add(new Comment(2, 0, "New comment!", "UserC"));
        post.setComments(newComments);

        assertEquals(1, post.getComments().size());
        assertEquals("New comment!", post.getComments().get(0).getComment());
    }

    @Test
    public void testToString() {
        String expected = "12345" + Constants.DELIMITER + "TestUser" + Constants.DELIMITER +
                "Test Caption" + Constants.DELIMITER + "test.jpg" + Constants.DELIMITER +
                "11-25-2024" + Constants.DELIMITER + "10" + Constants.DELIMITER +
                "2" + Constants.DELIMITER + "[\"UserA~~~Great post!~~~5~~~1\",\"UserB~~~Nice picture~~~3~~~0\"]" +
                Constants.DELIMITER + "true" + Constants.DELIMITER + "false";

        assertEquals(expected, post.toString());
    }

    @Test
    public void testDisplay() {
        System.out.println(post.display());
        String expected = "\n --- Post ID: 12345 ---\n" +
                "Created by: TestUser\n" +
                "Description: Test Caption\n" +
                "IMAGE_URL: test.jpg\n" +
                "Date Created: 11-25-2024\n" +
                "Number of Up Votes: 10\n" +
                "Number of Down Votes: 2\n" +
                "Comments: \n" +
                "UserA: Great post! --- Upvotes: 5 --- Downvotes: 1\n" +
                "UserB: Nice picture --- Upvotes: 3 --- Downvotes: 0\n" +
                "----------";

        assertEquals(expected, post.display());
    }



    @Test
    public void testEquals() {
        Post samePost = new Post("12345", "AnotherUser", "Another Caption", null,
                "11-26-2024", 5, 3, new ArrayList<>(), false, false);

        Post differentPost = new Post("67890", "DifferentUser", "Different Caption", null,
                "11-26-2024", 1, 1, new ArrayList<>(), true, false);

        assertEquals(post, samePost);
        assertNotEquals(post, differentPost);
    }

    @Test
    public void testCommentsEnabled() {
        post.setCommentsEnabled(false);
        assertFalse(post.isCommentsEnabled());

        post.setCommentsEnabled(true);
        assertTrue(post.isCommentsEnabled());
    }
}
