package src;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PostDBDatabase implements PostDBInterface {
    private final BufferedReader bfr;
    private final PrintWriter pw;
    private int postIDcounter = 1; 

    public PostDBDatabase() throws IOException {
        this.pw = new PrintWriter(new FileWriter(filename, true));
        this.bfr = new BufferedReader(new FileReader(filename));
        InitializePostCounter();
    }

    public static boolean createPost(String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            Post p = new Post();
//            bw.write(p.getID());
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    public static Post selectPost(int postId) {
        return null;
    }

    public void createPost(String content, String image) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("PostID:" + postIDcounter++ + "|Content:" + content + "|Image:" + image );
            bw.newLine();
        } catch (IOException e) {
            //idk what to do in case of an error
        }
    }

    private void InitializePostCounter() {
        try {
            String line;
            int maxID = 0;
            while ((line = bfr.readLine()) != null) {
                if (line.contains("PostID")) {
                    String[] parts = line.split("|");
                    int id = Integer.parseInt(parts[0].split(":")[1]);
                    maxID = Math.max(maxID, id);
                }
            } 
            postIDcounter = maxID + 1;
        } catch (IOException e) {
            //idk what to do in case of error
        }
    }

    public void deletePost(Post post) {
        //this needs heavy checking
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            ArrayList<Post> posts = getPosts();
            for (Post check : posts) {
                if (check.getID() == post.getID()) {
                    posts.remove(check);
                }
            }
            for (Post post1 : posts) {
                bw.write("PostID:" + post1.getID() + "|Content:" + post1.getCaption() + "|Image:" + post1.getUrl()); // the getCaption def wrong use
            }
            
        } catch (IOException e) {
            //idk what to do for exceptions
        }

    }

    public ArrayList<Post> getPosts() {
        //this whole method is need to be redone will redo
        //actually just dont understand what to do
        return null;
    }

    @Override
    public void addComment(Post post, String comment) {
        ArrayList<Post> posts = new ArrayList<>();
        for (Post check : posts) {
            if (post.equals(check)) { 
                check.getComments().add(comment);
            }
        }
        


    }

    @Override
    public void deleteComment(Post post, String comment) {
        ArrayList<Post> posts = new ArrayList<>();
        for (Post check : posts) {
            if (post.equals(check)) { 
                for (String check2 : check.getComments()) {
                    if (check2.equals(comment)) {
                        check.getComments().remove(comment);
                    }
                }
            }
        }

    }
}
