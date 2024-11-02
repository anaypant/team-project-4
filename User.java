import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private String imagePath;
    private ArrayList<User> friends;
    private ArrayList<User> blocked;
    private String password;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
    }

    public String getUsername() {
        return this.username;
    }

    private String getImagePath() {
        return this.imagePath;
    }

    public ArrayList<User> getFriendsList() {
        return this.friends;
    }

    private ArrayList<User> getBlockedList() {
        return this.blocked;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private void addFriend(User friend) {
        if (!blocked.contains(friend)) {
            this.friends.add(friend);
        }
    }

    private void removeFriend(User friend) {
        this.friends.remove(friend);
    }

    private void blockUser(User user) {
        this.blocked.add(user);
        this.friends.remove(user);
    }

    @Override
    public String toString() {
        return this.username + "," + this.password + "," + this.imagePath + "," + this.friends.toString() + "," + this.blocked.toString();
    }

    public static User parseUser(String s) {
        String[] parsed = s.split(",");
        return new User(parsed[0], parsed[1]);
    }
}
