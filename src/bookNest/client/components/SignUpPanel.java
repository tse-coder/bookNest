package bookNest.client.components;

import bookNest.models.User;
import javax.swing.*;
import java.awt.*;

// Represents the signup panel for new users
public class SignUpPanel extends JPanel {
    public SignUpPanel(JFrame parentFrame) {
        // Initialize UI components and layout
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0; gbc2.gridy = 0;
        JLabel signupTitle = new JLabel("Sign Up");
        signupTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        signupTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc2.gridwidth = 2;
        add(signupTitle, gbc2);
        gbc2.gridwidth = 1;
        gbc2.gridy++;
        add(new JLabel("Name:"), gbc2);
        JTextField signupNameField = new JTextField(18);
        gbc2.gridx = 1;
        add(signupNameField, gbc2);
        gbc2.gridx = 0; gbc2.gridy++;
        add(new JLabel("Email:"), gbc2);
        JTextField signupEmailField = new JTextField(18);
        gbc2.gridx = 1;
        add(signupEmailField, gbc2);
        gbc2.gridx = 0; gbc2.gridy++;
        add(new JLabel("Set Password:"), gbc2);
        JPasswordField signupPasswordField = new JPasswordField(18);
        gbc2.gridx = 1;
        add(signupPasswordField, gbc2);
        gbc2.gridx = 0; gbc2.gridy++;
        gbc2.gridwidth = 2;
        JButton signupActionBtn = new JButton("Sign Up");
        signupActionBtn.setBackground(Color.BLACK);
        signupActionBtn.setForeground(Color.WHITE);
        signupActionBtn.setFocusPainted(false);
        signupActionBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(signupActionBtn, gbc2);
        gbc2.gridwidth = 1;

        // Handle signup actions
        signupActionBtn.addActionListener(e -> {
            String name = signupNameField.getText().trim();
            String email = signupEmailField.getText().trim();
            String password = new String(signupPasswordField.getPassword());
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Insert into DB
            try (java.sql.Connection conn = bookNest.database.DatabaseUtil.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int userId = rs.getInt(1);
                            User newUser = new User(userId, name, email, java.util.Collections.emptyList());
                            parentFrame.dispose();
                            new bookNest.client.App(newUser).setVisible(true);
                            return;
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Signup failed. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
    }
}

// This file is intentionally left blank. See bookNest.client.auth.SignUpPanel for the real implementation.