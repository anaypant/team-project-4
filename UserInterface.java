import java.util.ArrayList;

public interface UserInterface {
  public String getUsername();
  public ArrayList<User> getFriendsList();
  private ArrayList<User> getBlockedList();
  private String getImagePath();
  private void setUsername(String username);
  private void setImagePath(String imagePath);
  private void addFriend(User friend);
  private void removeFriend(User friend);
  private void blockUser(User user);
}
