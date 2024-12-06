package src;

import GUI.SocialMedia;

import javax.swing.*;
import java.awt.*;

/**
 * Class to describe the GUI Component for a Post
 * Holds a post, an ID, the social media manager,
 * if it's in admin mode, and if its in profile mode
 *
 * @author cs180 Lab 2 Team 5
 * @version 1
 */

public class PostGUI implements PostGUIInterface {
    private Post post;
    private int id;
    private JPanel postPanel;
    private SocialMedia sm;
    private final boolean adminMode;
    private final boolean profileMode;


    public PostGUI(Post post, int id, SocialMedia sm, boolean profileMode) {
        this.post = post;
        this.id = id;
        this.sm = sm;
        this.adminMode = (sm.getActiveUser().equals(post.getCreator()));
        this.profileMode = profileMode;

        this.createPostGUI();
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


    public void createPostGUI() {
        postPanel = new JPanel(new BorderLayout());
        postPanel.setBorder(BorderFactory.createTitledBorder("Post by " + post.getCreator()));

        // Image display
        if (post.getUrl() != null && !post.getUrl().isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(post.getUrl());
            Image image = imageIcon.getImage();
            int newWidth = 200;
            int newHeight = 200;
            Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(resizedImage);
            JLabel imageLabel = new JLabel(imageIcon);
            postPanel.add(imageLabel, BorderLayout.WEST);
        }

        // Post content
        JPanel contentPanel = new JPanel(new BorderLayout());
        JTextArea postDescription = new JTextArea(post.getCaption());
        postDescription.setWrapStyleWord(true);
        postDescription.setLineWrap(true);
        postDescription.setEditable(false);

        postDescription.setBorder(BorderFactory.createCompoundBorder(
                postDescription.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));


        contentPanel.add(postDescription, BorderLayout.CENTER);

        JLabel dateLabel = new JLabel("Posted on: " + post.getDateCreated());
        contentPanel.add(dateLabel, BorderLayout.SOUTH);

        postPanel.add(contentPanel, BorderLayout.CENTER);
        JPanel actionPanel = new JPanel(new BorderLayout());


        if (!profileMode) {
            // Actions and comments

            // Upvote and downvote section
            JPanel votePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel voteLabel = new JLabel("Upvotes: " + post.getUpVotes() +
                    " | Downvotes: " + post.getDownVotes());

            JButton upvoteButton = new JButton("Upvote");
            JButton downvoteButton = new JButton("Downvote");

            upvoteButton.addActionListener(e -> {
                sm.handleVotePost(post.getId(), true);
                voteLabel.setText("Upvotes: " + (post.getUpVotes() + 1) +
                        " | Downvotes: " + post.getDownVotes());
            });

            downvoteButton.addActionListener(e -> {
                sm.handleVotePost(post.getId(), false);
                voteLabel.setText("Upvotes: " + post.getUpVotes() +
                        " | Downvotes: " + (post.getDownVotes() + 1));
            });

            votePanel.add(voteLabel);
            votePanel.add(upvoteButton);
            votePanel.add(downvoteButton);

            actionPanel.add(votePanel, BorderLayout.NORTH);


            // Enable/Disable Comments (Admin Mode)
            if (adminMode) {
                JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton enableCommentsButton = new JButton("Enable Comments");
                JButton disableCommentsButton = new JButton("Disable Comments");

                enableCommentsButton.addActionListener(e -> {
                    sm.handleEnableComments(post.getId());
                    enableCommentsButton.setEnabled(false);
                    disableCommentsButton.setEnabled(true);
                });

                disableCommentsButton.addActionListener(e -> {
                    sm.handleDisableComments(post.getId());
                    enableCommentsButton.setEnabled(true);
                    disableCommentsButton.setEnabled(false);
                });

                JButton hideButton = new JButton("Hide Post");
                JButton unhideButton = new JButton("Unhide Post");

                hideButton.addActionListener(e -> {
                    sm.handleHidePost(post.getId());
                    hideButton.setEnabled(false);
                    unhideButton.setEnabled(true);
                });

                unhideButton.addActionListener(e -> {
                    sm.handleUnhidePost(post.getId());
                    hideButton.setEnabled(true);
                    unhideButton.setEnabled(false);
                });

                JButton deletePost = new JButton("Delete Post");
                deletePost.addActionListener(e -> {
                   sm.handleDeletePost(post.getId());
                });

                adminPanel.add(enableCommentsButton);
                adminPanel.add(disableCommentsButton);
                adminPanel.add(hideButton);
                adminPanel.add(unhideButton);
                adminPanel.add(deletePost);

                actionPanel.add(adminPanel, BorderLayout.SOUTH);
            }
        }

        // Comments Section
        JPanel commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBorder(BorderFactory.createTitledBorder("Comments"));

        if (post.getComments() != null && !post.getComments().isEmpty()) {
            for (Comment comment : post.getComments()) {
                JPanel singleCommentPanel = new JPanel(new BorderLayout());

                JLabel commentLabel = new JLabel(comment.display());
                singleCommentPanel.add(commentLabel, BorderLayout.CENTER);

                if (!profileMode) {
                    JPanel commentActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton commentUpvoteButton = new JButton("Upvote");
                    JButton commentDownvoteButton = new JButton("Downvote");

                    commentUpvoteButton.addActionListener(e -> {
                        sm.handleVoteComment(comment.toString(), true);
                        comment.setUpvotes(comment.getUpvotes() + 1); // Update upvotes in the model
                        commentLabel.setText(comment.display());     // Update display
                    });

                    commentDownvoteButton.addActionListener(e -> {
                        sm.handleVoteComment(comment.toString(), false);
                        comment.setDownvotes(comment.getDownvotes() + 1); // Update downvotes in the model
                        commentLabel.setText(comment.display());          // Update display
                    });

                    commentActionPanel.add(commentUpvoteButton);
                    commentActionPanel.add(commentDownvoteButton);

                    // Add Delete Button if the active user is the creator of the post or the comment
                    if (sm.getActiveUser().equals(post.getCreator()) ||
                            sm.getActiveUser().equals(comment.getCreator())) {
                        JButton deleteButton = new JButton("Delete");
                        deleteButton.addActionListener(e -> {
                            sm.handleDeleteComment(post.getId(), comment);
                            commentsPanel.remove(singleCommentPanel); // Remove from GUI
                            commentsPanel.revalidate();
                            commentsPanel.repaint();
                        });
                        commentActionPanel.add(deleteButton);
                    }

                    singleCommentPanel.add(commentActionPanel, BorderLayout.EAST);

                }

                commentsPanel.add(singleCommentPanel);
            }
        }

        if (!profileMode) {
            // Add new comment input
            JPanel addCommentPanel = new JPanel(new BorderLayout());
            JTextField commentInput = new JTextField();
            JButton addCommentButton = new JButton("Add Comment");

            addCommentButton.addActionListener(e -> {
                String newCommentText = commentInput.getText().trim();
                if (!newCommentText.isEmpty()) {
                    // Create a new comment object
                    Comment newComment = new Comment(newCommentText, sm.getActiveUser());

                    // Send the comment to the server
                    sm.handleAddComment(newComment.encode(), post.getId());

                    // Add the comment locally for immediate display
                    JPanel newCommentPanel = new JPanel(new BorderLayout());
                    JLabel newCommentLabel = new JLabel(newComment.display());

                    JPanel newCommentActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton newCommentUpvoteButton = new JButton("Upvote");
                    JButton newCommentDownvoteButton = new JButton("Downvote");

                    newCommentUpvoteButton.addActionListener(ev -> {
                        sm.handleVoteComment(newComment.toString(), true);
                        newComment.setUpvotes(newComment.getUpvotes() + 1);
                        newCommentLabel.setText(newComment.display());
                    });

                    newCommentDownvoteButton.addActionListener(ev -> {
                        sm.handleVoteComment(newComment.toString(), false);
                        newComment.setDownvotes(newComment.getDownvotes() + 1);
                        newCommentLabel.setText(newComment.display());
                    });

                    // Add Delete Button for new comments
                    if (sm.getActiveUser().equals(post.getCreator()) ||
                            sm.getActiveUser().equals(newComment.getCreator())) {
                        JButton deleteButton = new JButton("Delete");
                        deleteButton.addActionListener(ev -> {
                            sm.handleDeleteComment(post.getId(), newComment);
                            commentsPanel.remove(newCommentPanel); // Remove from GUI
                            commentsPanel.revalidate();
                            commentsPanel.repaint();
                        });
                        newCommentActionPanel.add(deleteButton);
                    }

                    newCommentPanel.add(newCommentLabel, BorderLayout.CENTER);
                    newCommentPanel.add(newCommentActionPanel, BorderLayout.EAST);

                    commentsPanel.add(newCommentPanel);
                    commentsPanel.revalidate();
                    commentsPanel.repaint();

                    commentInput.setText(""); // Clear input field
                }
            });

            addCommentPanel.add(commentInput, BorderLayout.CENTER);
            addCommentPanel.add(addCommentButton, BorderLayout.EAST);
            commentsPanel.add(addCommentPanel);


        }
        actionPanel.add(commentsPanel, BorderLayout.CENTER);
        postPanel.add(actionPanel, BorderLayout.SOUTH);


    }


    public JPanel getPostPanel() {
        this.postPanel.revalidate();
        this.postPanel.repaint();
        return this.postPanel;
    }
}
