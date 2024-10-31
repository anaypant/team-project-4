public interface PostInterface {
  private void createPost(String content);
  private void createPost(String content, String image);
  private void deletePost(Post post);
  private void upvotePost(Post post);
  private void downvotePost(Post post);
  private ArrayList<Post> getPosts();
  private int getUpvotes();
  private int getDownvotes();
  private void addComment(String comment);
  private void deleteComment(String comment);
}
