package src;

/**
 * Interface containing constant variables used for the database
 * including paths, the server host name, and the main menu.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public interface Constants {
    String USER_DATABASE_PATH = "user.txt"; // constant for the path of the file we want to store all user data
    String POST_DATABASE_PATH = "post.txt"; // constant for the path of the file we want to store all post data
    int PORT_NUMBER = 4343; // specific port number for this server
    String SERVER_HOST_NAME = "localhost"; //the Servers host name which wil stay constant
    String DELIMITER = ":::"; // specific delimiter we set so when we parse we know what to parse by
    String MAIN_MENU_MSG = "Please enter a command ('help' for a list of commands): "; // message for when user needs to
    // enter a command

}
