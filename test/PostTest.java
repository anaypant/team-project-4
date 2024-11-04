package test;

import org.junit.Before;
import org.junit.Test;
import src.Post;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test file for Post
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class PostTest {
    private Post post;
    private ArrayList<String> comments;

    // Formats post with username, caption, content, date, and comments with usernames
    public void setUp() {
        comments = new ArrayList<>(Arrays.asList("user: comment", "user2: comment2"));
        post = new Post("user", "caption", "null", "11-03-2024");
        post.setUpVotes(1);
        post.setDownVotes(1);
        post.setComments(comments);
    }

    // Tests a post with postId users, date, comments, upvotes/downvotes
    @Test
    public void testConstructorWithId() {
        setUp();
        Post postWithId = new Post("doesn't matter", "user2", "caption2", "null", "11-03-2024", 1, 1, comments);
        assertEquals("user2", postWithId.getCreator());
        assertEquals("caption2", postWithId.getCaption());
        assertEquals("null", postWithId.getUrl());
        assertEquals("11-03-2024", postWithId.getDateCreated());
        assertEquals(1, postWithId.getUpVotes());
        assertEquals(1, postWithId.getDownVotes());
        assertEquals(comments, postWithId.getComments());
    }

    // Tests a post without postId, with users, date, comments, upvotes/downvotes
    @Test
    public void testConstructorWithoutId() {
        setUp();
        assertNotNull(post.getId());
        assertEquals("user", post.getCreator());
        assertEquals("caption", post.getCaption());
        assertEquals("null", post.getUrl());
        assertEquals("11-03-2024", post.getDateCreated());
        assertEquals(1, post.getUpVotes());
        assertEquals(1, post.getDownVotes());
        assertEquals(comments, post.getComments());
    }

    // Setters and Getters for post contents, grabs username, caption, content, date posted, and upvotes/downvotes
    @Test
    public void testSettersAndGetters() {
        setUp();
        post.setCreator("newUser");
        post.setCaption("Updated Caption");
        post.setUrl("testImg.png");
        post.setDateCreated("11-04-2024");
        post.setUpVotes(20);
        post.setDownVotes(4);

        assertEquals("newUser", post.getCreator());
        assertEquals("Updated Caption", post.getCaption());
        assertEquals("testImg.png", post.getUrl());
        assertEquals("11-04-2024", post.getDateCreated());
        assertEquals(20, post.getUpVotes());
        assertEquals(4, post.getDownVotes());
    }

    // Tests that values are equal to the other post
    @Test
    public void testEquals() {
        setUp();
        Post anotherPost = new Post(post.getId(), "user", "caption", "null", "11-03-2024", 1, 1, comments);
        assertTrue(post.equals(anotherPost));
    }

    // Tests that postId is matched
    @Test
    public void testToString() {
        setUp();
        String expected = post.getId() + ":::user:::caption:::null:::11-03-2024:::1:::1:::[\"user: comment\",\"user2: comment2\"]";
        assertEquals(expected, post.toString());
    }

    // Displays test post
    @Test
    public void testDisplay() {
        setUp();
        String expected = "\n --- Post ID: " + post.getId() + " ---\n" +
                "Created by: user\n" +
                "Description: caption\n" +
                "URL to image: null\n" +
                "Date Created: 11-03-2024\n" +
                "Number of Up Votes: 1\n" +
                "Number of Down Votes: 1\n" +
                "Comments: \n" +
                "user: comment\n" +
                "user2: comment2\n" +
                "----------";
        assertEquals(expected, post.display());
    }

    @Test
    public void testParseString() {
        setUp();
        String postString = post.getId() + ":::user:::caption:::null:::11-03-2024:::1:::1:::[\"user: comment\",\"user2: comment2\"]";
        Post parsedPost = Post.parseString(postString);

        assertEquals(post.getId(), parsedPost.getId());
        assertEquals(post.getCreator(), parsedPost.getCreator());
        assertEquals(post.getCaption(), parsedPost.getCaption());
        assertEquals(post.getUrl(), parsedPost.getUrl());
        assertEquals(post.getDateCreated(), parsedPost.getDateCreated());
        assertEquals(post.getUpVotes(), parsedPost.getUpVotes());
        assertEquals(post.getDownVotes(), parsedPost.getDownVotes());
        assertEquals(post.getComments(), parsedPost.getComments());
    }

    // Formats comments on test post
    @Test
    public void testCommentsFormat() {
        setUp();
        post.getComments().add("user3: New comment!");
        String lastComment = post.getComments().get(post.getComments().size() - 1);
        assertEquals("user3: New comment!", lastComment);
    }
}
