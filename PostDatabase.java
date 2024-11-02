import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PostDatabase implements PostInterface {
    private final BufferedReader bfr;
    private final PrintWriter pw;

    public PostDatabase() throws IOException {
        this.pw = new PrintWriter(new FileWriter(filename));
        this.bfr = new BufferedReader(new FileReader(filename));
    }

    public static void createPost(String content) {

    }

    public static Post selectPost(int postId) {
        return null;
    }

    public void createPost(String content, String image) {

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
