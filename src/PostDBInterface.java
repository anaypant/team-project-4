package src;

import java.util.ArrayList;

public interface PostDBInterface {
    static final String filename = Constants.POST_DATABASE_PATH;

    static void createPost(String content) {

    }

    void createPost(String content, String image);

    void deletePost(Post post);

    ArrayList<Post> getPosts();

    void addComment(Post post, String comment);

    void deleteComment(Post post, String comment);
}
