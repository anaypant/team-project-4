import java.util.ArrayList;

public class UserDatabase implements UserInterface {
    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public ArrayList<User> getFriendsList() {
        return null;
    }

    @Override
    public ArrayList<User> getBlockedList() {
        return null;
    }

    @Override
    public String getImagePath() {
        return "";
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public void setImagePath(String imagePath) {

    }

    @Override
    public void addFriend(User friend) {

    }

    @Override
    public void removeFriend(User friend) {

    }

    @Override
    public void blockUser(User user) {

    }
}
