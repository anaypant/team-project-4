package src;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable, UserInterface {
    private String username;
    private String imagePath;
    private ArrayList<String> friends;
    private ArrayList<String> blocked;
    private String password;
    private ArrayList<Integer> posts;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public String getUsername() {
        return this.username;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public ArrayList<String> getFriendsList() {
        return this.friends;
    }

    public ArrayList<String> getBlockedList() {
        return this.blocked;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPassword() {return this.password;}

    public void addFriend(String friend) {
        if (!blocked.contains(friend)) {
            this.friends.add(friend);
        }
    }

    public void removeFriend(User friend) {
        this.friends.remove(friend);
    }

    public void blockUser(String user) {
        this.blocked.add(user);
        this.friends.remove(user);
    }

    @Override
    public String toString() {
        return this.username + Constants.DELIMITER + this.password + Constants.DELIMITER + this.imagePath + Constants.DELIMITER + this.friends.toString() + Constants.DELIMITER + this.blocked.toString() + Constants.DELIMITER + this.posts.toString();
    }

    private static ArrayList<String> arrFromStr(String str) {
        // format of String: "["1","2","3","4","5","6","7"]"
        if(str.equals("[]")) { return new ArrayList<>();}
        System.out.println(str);
        String st = str.substring(2, str.length()-2);
        System.out.println(st);
        return null;
    }

    public static User parseUser(String s) {
        String[] parsed = s.split(Constants.DELIMITER);
        arrFromStr(parsed[3]);
        return new User(parsed[0], parsed[1]);
    }
}
