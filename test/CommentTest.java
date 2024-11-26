package test;

import org.junit.Test;
import src.Comment;
import src.Constants;

import static org.junit.Assert.*;

/**
 * Test file for Comment
 *
 * @version 1.0
 **/

public class CommentTest {

    private Comment comment;

    // Sets up a default comment for testing
    public void setUp() {
        comment = new Comment(5, 2, "This is a test comment.", "TestUser");
    }

    // Tests the constructor with upvotes and downvotes
    @Test
    public void testConstructorWithVotes() {
        setUp();
        assertEquals(5, comment.getUpvotes());
        assertEquals(2, comment.getDownvotes());
        assertEquals("This is a test comment.", comment.getComment());
        assertEquals("TestUser", comment.getCreator());
    }

    // Tests the constructor without upvotes and downvotes
    @Test
    public void testConstructorWithoutVotes() {
        Comment newComment = new Comment("No votes comment", "AnotherUser");
        assertEquals(0, newComment.getUpvotes());
        assertEquals(0, newComment.getDownvotes());
        assertEquals("No votes comment", newComment.getComment());
        assertEquals("AnotherUser", newComment.getCreator());
    }

    // Tests the setters and getters
    @Test
    public void testSettersAndGetters() {
        setUp();
        comment.setUpvotes(10);
        comment.setDownvotes(3);
        comment.setComment("Updated comment");
        comment.setCreator("UpdatedUser");

        assertEquals(10, comment.getUpvotes());
        assertEquals(3, comment.getDownvotes());
        assertEquals("Updated comment", comment.getComment());
        assertEquals("UpdatedUser", comment.getCreator());
    }

    // Tests the toString method
    @Test
    public void testToString() {
        setUp();
        String expected = "TestUser: This is a test comment.";
        assertEquals(expected, comment.toString());
    }

    // Tests encoding a comment to a string
    @Test
    public void testEncode() {
        setUp();
        String encoded = comment.encode();
        String expected = "TestUser:::This is a test comment.:::5:::2";
        assertEquals(expected, encoded);
    }

    // Tests parsing a comment from a string
    @Test
    public void testParseCommentFromString() {
        setUp();
        String encodedComment = "AnotherUser:::Parsed comment:::8:::1";
        Comment parsedComment = Comment.parseCommentFromString(encodedComment);

        assertEquals("AnotherUser", parsedComment.getCreator());
        assertEquals("Parsed comment", parsedComment.getComment());
        assertEquals(8, parsedComment.getUpvotes());
        assertEquals(1, parsedComment.getDownvotes());
    }

    // Tests the equals method
    @Test
    public void testEquals() {
        setUp();
        Comment identicalComment = new Comment(5, 2, "This is a test comment.", "TestUser");
        Comment differentComment = new Comment(3, 1, "Different comment", "AnotherUser");

        assertEquals(comment, identicalComment);
        assertFalse(comment.equals(differentComment));
    }
}
