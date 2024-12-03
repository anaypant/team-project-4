package GUI;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class PostPage extends JFrame {
    private JPanel appPanel;
    private JTextPane displayArea; // Changed to JTextPane
    private JTextField inputField;
    private JButton sendButton;

    private JButton upvoteButton;
    private JButton downvoteButton;
    private JButton commentButton;
    private JButton upvoteCommentButton;
    private JButton downvoteCommentButton;

    private JButton hidePostButton;
    private JButton unhidePostButton;

    private JButton enableCommentButton;
    private JButton disableCommentButton;

    private JButton returnButton;

    private JButton selectCommentButton;


    private SocialMedia sm;


    public PostPage(SocialMedia sm) {
        this.sm = sm;
        init();
    }

    private void init() {
        appPanel = new JPanel(new BorderLayout());

        // Display area for server responses
        displayArea = new JTextPane(); // Changed to JTextPane
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        appPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add action listener for the send button
        sendButton.addActionListener(e -> sendMessage());

        // Add input panel to the app panel
        appPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel for command buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 6, 5, 5));

        upvoteButton = new JButton("Upvote Post");
        downvoteButton = new JButton("Downvote Post");
        commentButton = new JButton("Add Comment");

        upvoteCommentButton = new JButton("Upvote Comment");
        downvoteCommentButton = new JButton("Downvote Comment");

        hidePostButton = new JButton("Hide Post");
        unhidePostButton = new JButton("Unhide Post");

        enableCommentButton = new JButton("Enable Comments");
        disableCommentButton = new JButton("Disable Comments");

        returnButton = new JButton("Return");
        selectCommentButton = new JButton("Select Comment");



        // Add action listeners for command buttons
        upvoteButton.addActionListener(e -> upVote());
        downvoteButton.addActionListener(e -> downVote());
        commentButton.addActionListener(e -> comment());

        upvoteCommentButton.addActionListener(e -> upVoteComment());
        downvoteCommentButton.addActionListener(e -> downVoteComment());
        hidePostButton.addActionListener(e -> hidePost());
        unhidePostButton.addActionListener(e -> unhidePost());

        enableCommentButton.addActionListener(e -> enableComments());
        disableCommentButton.addActionListener(e -> disableComments());

        returnButton.addActionListener(e -> ret());
        selectCommentButton.addActionListener(e -> selectComment());



        // Add buttons to the panel
        buttonPanel.add(upvoteButton);
        buttonPanel.add(downvoteButton);
        buttonPanel.add(commentButton);

        buttonPanel.add(upvoteCommentButton);
        buttonPanel.add(downvoteCommentButton);
        buttonPanel.add(hidePostButton);
        buttonPanel.add(unhidePostButton);

        buttonPanel.add(enableCommentButton);
        buttonPanel.add(disableCommentButton);

        buttonPanel.add(returnButton);
        buttonPanel.add(selectCommentButton);



        // Add button panel to the app panel
        appPanel.add(buttonPanel, BorderLayout.NORTH);

        this.setContentPane(appPanel);
        if (sm.getSelectedPost() == null) {
            this.setTitle("null");
        } else {
            this.setTitle(sm.getSelectedPost());
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 900);
    }

    private void upVote() {
        sm.handleUpvote();
    }

    private void downVote() {
        sm.handleDownvote();
    }

    private void comment() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JLabel("Enter Comment: "));
        JTextField field = new JTextField();
        panel.add(field);

        int result = JOptionPane.showConfirmDialog(this, panel, "Comment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create user commands to server
            sm.handleComment(field.getText());
        }

    }

    private void selectComment() {
        sm.handleSelectComment();
    }


    private void upVoteComment() {
        sm.handleUpVoteComment();
    }

    private void downVoteComment() {
        sm.handleDownVoteComment();

    }

    private void hidePost() {
        sm.handleHidePost();

    }

    private void unhidePost() {
        sm.handleUnhidePost();

    }

    private void enableComments() {
        sm.handleEnableComments();

    }

    private void disableComments() {
        sm.handleDisableComments();
    }

    private void ret() {
        sm.returnMainPage();
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            sm.sendMessage(msg);
            inputField.setText("");
        }
    }

    public StyledDocument getStyledDocument() {
        return displayArea.getStyledDocument();
    }

    public void setCaretPosition(int position) {
        displayArea.setCaretPosition(position);
    }

    public void insertComponent(JLabel imageLabel) {
        this.displayArea.insertComponent(imageLabel);
    }


}
