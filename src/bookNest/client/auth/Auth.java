package bookNest.client.auth;

import javax.swing.*;
import java.awt.*;

import bookNest.client.components.SignInPanel;
import bookNest.client.components.SignUpPanel;

public class Auth extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JButton loginBtn = new JButton("Login");
    private final JButton signupBtn = new JButton("Sign Up");
    private final JFrame parentFrame;

    // Represents the authentication panel for login and signup
    public Auth(JFrame parentFrame) {
        // Initialize the parent frame and set up the layout
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(Color.WHITE);

        // Configure the login button appearance
        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(Color.BLACK);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Configure the signup button appearance
        signupBtn.setFocusPainted(false);
        signupBtn.setBackground(Color.WHITE);
        signupBtn.setForeground(Color.BLACK);
        signupBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Add login and signup buttons to the top panel
        topPanel.add(loginBtn);
        topPanel.add(signupBtn);
        add(topPanel, BorderLayout.NORTH);

        // Add login and signup panels to the card layout
        cards.removeAll();
        cards.add(new SignInPanel(parentFrame), "login");
        cards.add(new SignUpPanel(parentFrame), "signup");
        add(cards, BorderLayout.CENTER);

        // Button actions
        // Handle login button click to switch to login panel
        loginBtn.addActionListener(e -> {
            cardLayout.show(cards, "login");
            loginBtn.setBackground(Color.BLACK);
            loginBtn.setForeground(Color.WHITE);
            signupBtn.setBackground(Color.WHITE);
            signupBtn.setForeground(Color.BLACK);
        });
        
        // Handle signup button click to switch to signup panel
        signupBtn.addActionListener(e -> {
            cardLayout.show(cards, "signup");
            signupBtn.setBackground(Color.BLACK);
            signupBtn.setForeground(Color.WHITE);
            loginBtn.setBackground(Color.WHITE);
            loginBtn.setForeground(Color.BLACK);
        });
    }
}
