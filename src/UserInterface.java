package src;

import java.util.ArrayList;

public interface UserInterface {
    String getUsername();
    String getImagePath();
    ArrayList<String> getFriendsList();
    ArrayList<String> getBlockedList();
    void setUsername(String username);
    String getPassword();
}
