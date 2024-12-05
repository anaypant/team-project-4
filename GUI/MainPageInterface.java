package GUI;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.io.File;


/**
 * Interface to describe the Main Menu Page of the GUI
 * Handles Post Selection, Adding/Removing/Blocking Friends
 * Can View Posts from Feed
 *
 * @author CS180 Team 5
 * @version 1
 */

public interface MainPageInterface {
    //sets up the gui and creates all the buttons
    void init();

    // method that handles when the user is trying to create a post
    void createPost();

    // method that checks if the user inputs an actual image
    boolean isImageFile(File file);

    // method that handles when the client needs to put a specific page on the GUI
    StyledDocument getStyledDocument();

    // method that handles when the client puts the JPanel
    void setCaretPosition(int position);

    // method that handles when the user is trying to select a post
    void selectPost();

    // method that handles when the user is trying to logout
    void logout();

    // method that handles when the client needs to insert  JLabel
    void insertComponent(JLabel imageLabel);

}
