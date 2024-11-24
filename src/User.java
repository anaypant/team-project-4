package src;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class that represents each social media user.
 * Each user has a username, password, and profile picture path.
 *
 * @author CS180 L2 Team 5
 *
 * @version 2.0
 **/

public class User implements UserInterface {
    private String username;
    private String imagePath;
    private final ArrayList<String> friends;
    private final ArrayList<String> blocked;
    private String password;

    // constructer intilalzes all fields the User given an input of a username and password
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
    }

    // constructor with all the fields, mainly used for parsing from a string to a user
    public User(String username, String password,
                String imagePath, ArrayList<String> friends,
                ArrayList<String> blocked) {
        this.username = username;
        this.imagePath = imagePath;
        this.friends = friends;
        this.blocked = blocked;
        this.password = password;
    }

    //takes no inputs and returns a Users username in the form of a string
    public String getUsername() {
        return this.username;
    }

    //takes no inputs and returns a Users imeage path in the form of a string
    public String getImagePath() {
        return this.imagePath;
    }

    //takes no inputs and returns a Users freinds list in the form of an ArrayList
    public ArrayList<String> getFriendsList() {
        return this.friends;
    }

    //takes no inputs and returns a Users blocked list in the form of an ArrayList
    public ArrayList<String> getBlockedList() {
        return this.blocked;
    }

    //takes the input of a username in the form of a String and sets the username
    // to the given one and returns nothing.
    public void setUsername(String username) {
        this.username = username;
    }

    //takes the input of an image path in the form of a String and sets the image
    // path to the given one and returns nothing.
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    ////takes no inputs and returns a Users password in the form of a string
    public String getPassword() {
        return this.password;
    }

    //method to add a friend that takes the input of a friend in the form of a string.
    // It then makes sure the friend
    // is not blocked and if they aren't it will add the friend to the ArrayList
    // of friends and doesnt return anything.
    public boolean addFriend(String friend) {
        if (this.friends.contains(friend)) {
            return false;
        }
        this.blocked.remove(friend);
        this.friends.add(friend);
        return true;
    }

    //The remove Friend method will take an input of a String and will remove this string from
    //the ArrayList of friends and returns nothing
    public boolean removeFriend(String friend) {
        if (!this.friends.contains(friend)) {
            return false;
        }
        this.friends.remove(friend);
        return true;
    }

    // the blockUser method will take an input of a string and add this user
    // to the blocked ArrayList as well as
    //remove them from the friends list and returns nothing.
    public boolean blockUser(String user) {
        if (this.blocked.contains(user)) {
            return false;
        }
        this.blocked.add(user);
        this.friends.remove(user);
        return true;
    }

    //overriding the toString() method so that it returns a users username
    // then our delimiter which is ":::" then
    //password then deloimiter then image path then delimiter than freinds
    // list then delimiter then blockedd list
    //then delimiter then posts lists which is an integer list.
    @Override
    public String toString() {

        return this.username + Constants.DELIMITER + this.password +
                Constants.DELIMITER + this.imagePath + Constants.DELIMITER +
                Utils.arrListToString(this.friends) + Constants.DELIMITER +
                Utils.arrListToString(this.blocked);
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    //This method will take an input of a string which is formatted in the toString() way
    //it will then parse through using the delimiter we set and get the Users variables so that
    //we can find this user and it will return that User.
    public static User parseUser(String s) {
        String[] parsed = s.split(Constants.DELIMITER);
        ArrayList<String> friends = Utils.arrayFromString(parsed[3]);
        ArrayList<String> blocked = Utils.arrayFromString(parsed[4]);
        return new User(parsed[0], parsed[1], parsed[2], friends, blocked);
    }


}
