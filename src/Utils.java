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
 * @author Purdue University -- CS18000 -- Fall 2024</p>
 *
 * @version November 3rd, 2024
 **/

public class Utils {

    //taking a string and converting the elements inside into an arrayList which it returns that we can use later to go
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
    /*public static ArrayList<Comment> ComarrayFromString(String s) {

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
    }*/

    //takes in a file and id then
    // reads the post file and as long as each post doesn't have the specified id it will add it to an arrayList
    // then once its added all but the specified one it will rewrite the arrayList out essentialy deleting
    //a specified Post and returns nothing
    public static void deletePost(String postId, String fileName) {
        ArrayList<String> lines = new ArrayList<>();

        // Step 1: Read all lines except the line to delete
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.split(Constants.DELIMITER)[0].equals(postId)) {
                    lines.add(currentLine);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            return;
        }

        // Step 2: Write back all lines except the deleted one
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Line deleted successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    //takes in a file and username then
    // reads the user file and as long as each user doesn't have the specified username it will add it to
    // an arrayList
    // then once its added all but the specified one it will rewrite the arrayList out essentialy deleting
    //a specified User and returns nothing
    public static void deleteUser(String username, String fileName) {
        ArrayList<String> lines = new ArrayList<>();

        // Step 1: Read all lines except the line to delete
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.split(Constants.DELIMITER)[0].equals(username)) {
                    lines.add(currentLine);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            return;
        }

        // Step 2: Write back all lines except the deleted one
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Line deleted successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    //takes an arrayList and goes through each element and writes it to a string so that we can convert an ArrayList
    //to a string and pit it into a file later.
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

    public static String ComarrListToString(ArrayList<Comment> arr) {
        ArrayList<String> commentText = new ArrayList<>();
        for (Comment comment : arr) {
            // Ensure each comment is serialized correctly
            commentText.add(comment.getCommentText() + Constants.COMMENT_DELIMITER +
                    comment.getUpvotes() + Constants.COMMENT_DELIMITER +
                    comment.getDownvotes() + Constants.COMMENT_DELIMITER +
                    comment.isVisible() + Constants.COMMENT_DELIMITER +
                    comment.getPostOwner() + Constants.COMMENT_DELIMITER +
                    comment.getCommentOwner());
        }
        return String.join(Constants.DELIMITER, commentText);
    }


    // Takes the collection of posts in arraylist and sorts them by year ascending using the provided collection formatting
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
