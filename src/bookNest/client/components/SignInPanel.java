package bookNest.client.components;

import bookNest.models.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

// Represents the signin panel for existing users
public class SignInPanel extends JPanel {
    public SignInPanel(JFrame parentFrame) {
        // Initialize UI components and layout
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel loginTitle = new JLabel("Sign In");
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        add(loginTitle, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(18);
        gbc.gridx = 1;
        add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JButton loginActionBtn = new JButton("Login");
        loginActionBtn.setBackground(Color.BLACK);
        loginActionBtn.setForeground(Color.WHITE);
        loginActionBtn.setFocusPainted(false);
        loginActionBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(loginActionBtn, gbc);
        gbc.gridwidth = 1;

        // Handle signin actions
        loginActionBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passwordField.getPassword());
            List<User> users = bookNest.models.User.getAllUsersFromDB();
            User found = users.stream().filter(u -> u.getName().equals(name)).findFirst().orElse(null);
            if (found == null) {
                JOptionPane.showMessageDialog(this, "User not found. Try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Query DB for password
            try (java.sql.Connection conn = bookNest.database.DatabaseUtil.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE id = ?")) {
                ps.setInt(1, found.getId());
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getString(1).equals(password)) {
                        parentFrame.dispose();
                        new bookNest.client.App(found).setVisible(true);
                        return;
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Incorrect password. Try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        });
    }
}

// This file is intentionally left blank. See bookNest.client.auth.SignInPanel for the real implementation.
