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

    public static void createPost(String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("PostID:" + 0 + "|Content:" + content + "|Image:null" );
            bw.newLine();
        } catch (IOException e) {
            //idk what to do in case of an error
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
                bw.write("PostID:" + check.getID() + "|Content:" + check.getCaption() + "|Image:" + check.getUrl());
            }
            
        } catch (IOException e) {
            //idk what to do for exceptions
        }

    }

    public ArrayList<Post> getPosts() {
        //this whole method is need to be redone will redo
        ArrayList<Post> posts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line=br.readLine()) != null) {
                String[] parts = line.split("|");
                int id = Integer.parseInt(parts[0].split(":")[1]);
                String content = parts[1].split(":")[1];
                String image = parts[2].split(":")[1];
                posts.add(new Post(new User("change"),content,image,"change"));
            }

        } catch (IOException e) {
            //idk what to do for exceptions 
        }
        return posts;
    }

    @Override
    public void addComment(Post post, String comment) {
        ArrayList<Post> posts = new ArrayList<>();
        for (Post check : posts) {
            if (post.equals(check)) { // need to create an overlide .equals() method this needs to be updated wil redo
                check.getComments().add(comment);
            }
        }
        


    }

    @Override
    public void deleteComment(Post post, String comment) {
        ArrayList<Post> posts = new ArrayList<>();
        for (Post check : posts) {
            if (post.equals(check)) { // need to create an overlide .equals() method
                for (String check2 : check.getComments()) {
                    if (check2.equals(comment)) {
                        check.getComments().remove(comment);
                    }
                }
            }
        }

    }
}
