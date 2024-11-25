package src;

/**
 * Interface containing constant variables used for the database
 * including paths, the server host name, and the main menu.
 *
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public interface Constants {
    int PORT_NUMBER = 4343; // specific port number for this server
    String SERVER_HOST_NAME = "localhost"; //the Servers host name which wil stay constant
    String DELIMITER = ":::"; // specific delimiter we set so when we parse we know what to parse by
    String COMMENT_DELIMITER = "***";
    String MAIN_MENU_MSG = "Please enter a command ('help' for a list of commands): "; // message for when user needs to
    String USER_DB = "jdbc:sqlite:users.sqlite";
    String POST_DB = "jdbc:sqlite:posts.sqlite";

}
