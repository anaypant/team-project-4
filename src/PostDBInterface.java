package src;

/**
 * An interface that defines how the PostDBDatabase class will act.
 *
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public interface PostDBInterface {
    // creates post with specified username, content, and image
    static boolean createPost(String username, String content, String image) {
        return false;
    }

    // deletes a target post based on it's user id
    static boolean deletePost(String post) {
        return false;
    }

    // adds comment to a specific post
    static boolean addComment(String post, String comment) {
        return false;
    }

    // creates post with Post object parameter
    static boolean createPost(Post p) {
        return false;
    }

    // creates post with username and content as inputs
    static boolean createPost(String username, String content) {
        return false;
    }

    // returns a post object from database based on post id
    static Post selectPost(String postId) {
        return null;
    }

    //gets a post based on post id and deletes from database
    static Post getAndDeletePosts(String postId) {
        return null;
    }

}
