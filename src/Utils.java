package src;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A class containing helper methods used in various classes.
 * arrListToString(), replaceLineInFile()
 *
 * @author CS180 L2 Team 5
 * @version 2.0
 **/

public class Utils {

    //taking a string and converting the elements inside into an
    // arrayList which it returns that we can use later to go
    //through things like number list for posts and such.
    public static ArrayList<String> arrayFromString(String s) {

        if (s.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<String> result = new ArrayList<>();
        String st = s.substring(1, s.length() - 1);

        String currentElement = "";
        boolean inQuotes = false;

        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentElement.trim());
                currentElement = "";
            } else {
                currentElement += c;
            }
        }
        if (!currentElement.isEmpty()) {
            result.add(currentElement.trim());
        }

        return result;
    }

    public static ArrayList<Comment> arrayCommentFromString(String s) {

        if (s.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Comment> result = new ArrayList<>();
        String st = s.substring(1, s.length() - 1);

        String currentElement = "";
        boolean inQuotes = false;

        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(Comment.parseCommentFromString(currentElement.trim()));
                currentElement = "";
            } else {
                currentElement += c;
            }
        }
        if (!currentElement.isEmpty()) {
            result.add(Comment.parseCommentFromString(currentElement.trim()));
        }

        return result;
    }

    //takes an arrayList and goes through each element and
    // writes it to a string so that we can convert an ArrayList
    //to a string and pit it into a file later.

    public static String arrListCommentToString(ArrayList<Comment> arr) {
        String result = "[";

        for (int i = 0; i < arr.size(); i++) {
            result += "\"" + arr.get(i).encode() + "\"";

            // Add a comma and space if it's not the last element
            if (i < arr.size() - 1) {
                result += ",";
            }
        }

        result += "]";
        return result;
    }

    public static String arrListToString(ArrayList<String> arr) {
        String result = "[";

        for (int i = 0; i < arr.size(); i++) {
            result += "\"" + arr.get(i) + "\"";

            // Add a comma and space if it's not the last element
            if (i < arr.size() - 1) {
                result += ",";
            }
        }

        result += "]";
        return result;
    }

    // Takes the collection of posts in arraylist and sorts them by
    // year ascending using the provided collection formatting
    public static ArrayList<Post> sortPostsByDateDesc(ArrayList<Post> posts) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        posts.sort((post1, post2) -> {
            try {
                // Parse the date strings into Date objects
                Date date1 = dateFormat.parse(post1.getDateCreated());
                Date date2 = dateFormat.parse(post2.getDateCreated());

                // Compare the dates in descending order
                return date2.compareTo(date1);
            } catch (ParseException e) {
                // If parsing fails, consider the posts equal (fallback)
                return 0;
            }
        });

        return posts;
    }
}
