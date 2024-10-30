public interface PostInterface {
  private void makePost(String content);
  public void upvote(Post post);
  public void downvote(Post post);
  public Post[] getPosts();
  public int getUpvotes();
  public int getDownvotes();
}
