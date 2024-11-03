package src;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PostDBDatabase implements PostDBInterface {
    private static BufferedReader bfr;
    private static PrintWriter pw;

    private static void initialize() {
        if (bfr == null) {
            try {
                bfr = new BufferedReader(new FileReader(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (pw == null) {
            try {
                pw = new PrintWriter(new FileWriter(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void close() {
        if (bfr != null) {
            try {
                bfr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bfr = null;
        }
        if (pw != null) {
            pw.close();
            pw = null;
        }
    }

    public static synchronized boolean createPost(String username, String content, String image) {
        try {
            initialize();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            Post p = new Post(username, content, image, date);
            pw.println(p);
            close();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized boolean createPost(String username, String content) {
        return createPost(username, content, null);
    }

    public static synchronized Post selectPost(String postId) {
        // find a post by it's id and return a post form of it
        initialize();
        try {
            String line = bfr.readLine();
            while (line != null) {
                if (line.split(Constants.DELIMITER)[0].equals(postId)) {
                    return Post.parseString(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public void deletePost(Post post) {
        // this needs heavy checking
        // find a post and remove the line

//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
//            ArrayList<Post> posts = getPosts();
//            for (Post check : posts) {
//                if (check.getID() == post.getID()) {
//                    posts.remove(check);
//                }
//            }
//            for (Post post1 : posts) {
//                bw.write("PostID:" + post1.getID() + "|Content:" + post1.getCaption() + "|Image:" + post1.getUrl()); // the getCaption def wrong use
//            }
//
//        } catch (IOException e) {
//            //idk what to do for exceptions
//        }

    }

    private static Post getAndRemovePost() {
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
