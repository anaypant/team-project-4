public interface UserInterface {
  default String getUsername();
  default Users[] getFriendsList();
  private Users[] getBlockedList();
  private String getImagePath();
  private void setUsername(String username);
  private void setImagePath(String imagePath);
  private void addFriend(User friend);
  private void removeFriend(User friend);
  private void blockUser(User user);
}
