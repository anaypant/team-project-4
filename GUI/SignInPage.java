package GUI;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Class to describe the Sign In Page of the GUI
 * Handles displaying the log in page, creating users, validating login details
 * Can View Posts from Feed
 */

public class SignInPage extends JFrame implements SignInPageInterface {
    // Login panel components
    private JPanel loginPanel;
    private JButton createUserButton;
    private JButton loginUserButton;
    private JTextPane loginDisplayArea; // Changed to JTextPane

    // Dialog fields
    private JTextField usernameField;
    private JTextField passwordField;

    private SocialMedia sm;


    public SignInPage(SocialMedia sm) {
        loginPanel = new JPanel(new BorderLayout());

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create User button
        createUserButton = new JButton("Create User");
        createUserButton.setPreferredSize(new Dimension(150, 30));
        createUserButton.addActionListener(e -> createUser());

        // Login User button
        loginUserButton = new JButton("Login User");
        loginUserButton.setPreferredSize(new Dimension(150, 30));
        loginUserButton.addActionListener(e -> loginUser());

        // Add buttons to the button panel
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(createUserButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(loginUserButton, gbc);

        // Add button panel to the top of login panel
        loginPanel.add(buttonPanel, BorderLayout.NORTH);

        // Login display area to show server messages
        loginDisplayArea = new JTextPane(); // Changed to JTextPane
        loginDisplayArea.setEditable(false);
        JScrollPane loginScrollPane = new JScrollPane(loginDisplayArea);
        loginPanel.add(loginScrollPane, BorderLayout.CENTER);

        this.add(loginPanel);
        this.setTitle("Sign In");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(600, 500);

        this.sm = sm;
    }

    public void createUser() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send create user commands to server
            sm.handleCreateUser(usernameField.getText(), passwordField.getText());
        }
    }

    public void loginUser() {
        // Dialog to input username and password
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Send login user commands to server
            sm.handleLoginUser(usernameField.getText(), passwordField.getText());

        }
    }

    public StyledDocument getStyledDocument() {
        return loginDisplayArea.getStyledDocument();
    }

    public void setCaretPosition(int position) {
        loginDisplayArea.setCaretPosition(position);
    }


}
