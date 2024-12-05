package src;

import javax.swing.*;

/**
 * Class to describe the GUI Component for a Post
 * Holds a post, an ID, the social media manager,
 * if it's in admin mode, and if its in profile mode
 *
 * @author cs180 Lab 2 Team 5
 * @version 1
 */
public interface PostGUIInterface {
    // gets the post associated with the postGUI
    Post getPost();

    // sets the associated post
    void setPost(Post post);

    // actually creates the component
    void createPostGUI();

    // gets the component to display to screen
    JPanel getPostPanel();


}
