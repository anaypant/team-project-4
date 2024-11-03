package src;

import java.io.*;
import java.util.ArrayList;

public class PostDBDatabase implements PostDBInterface {
    private final BufferedReader bfr;
    private final PrintWriter pw;

    public PostDBDatabase() throws IOException {
        this.pw = new PrintWriter(new FileWriter(filename));
        this.bfr = new BufferedReader(new FileReader(filename));
    }

    public static Post selectPost(int postId) {
        return null;
    }

    public static void createPost(String content, String image) {

    }

    public void deletePost(Post post) {

    }

    public ArrayList<Post> getPosts() {
        return null;
    }

    @Override
    public void addComment(Post post, String comment) {

    }

    @Override
    public void deleteComment(Post post, String comment) {

    }
}
