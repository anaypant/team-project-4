import java.util.ArrayList;
public class User {
  public String username;
  private String imagePath;
  public ArrayList<User> friends;
  private ArrayList<User> blocked;
  public User(String username) {
    this.username = username;
  }
  public User(String username, String imagePath) {
    this.username = username;
    this.imagePath = imagePath;
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
}
