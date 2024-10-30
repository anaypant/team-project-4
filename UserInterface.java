import java.util.ArrayList;

public interface UserInterface {
  String getUsername();
  ArrayList<User> getFriendsList();
  ArrayList<User> getBlockedList();
  String getImagePath();
  void setUsername(String username);
  void setImagePath(String imagePath);
  void addFriend(User friend);
  void removeFriend(User friend);
  void blockUser(User user);
}
