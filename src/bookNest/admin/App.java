package bookNest.admin;

import java.awt.*;
import javax.swing.*;

import bookNest.admin.components.BooksPage;
import bookNest.admin.components.UsersPage;


public class App extends JFrame {
    JPanel navigation, page, usersPage, booksPage, topBar;
    JButton usersBtn, booksBtn;
    JLabel bookEmojiLabel,titleLabel;
    
    // Represents the main admin application window
    private App() {
        super("BOOKNEST");
        // Set up the application window properties
        setSize(1000, 600);
        setMinimumSize(new Dimension(1000,600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Configure the navigation panel on the left
        navigation = new JPanel();
        navigation.setBackground(Color.black);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        navigation.setPreferredSize(new Dimension(180, getHeight()));
        
        // Add logo and title to the navigation panel
        bookEmojiLabel = new JLabel("📖");
        bookEmojiLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        bookEmojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookEmojiLabel.setBackground(Color.BLACK);
        bookEmojiLabel.setForeground(Color.WHITE);

        titleLabel = new JLabel("Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBackground(Color.BLACK);
        titleLabel.setForeground(Color.WHITE);

        
        // Create and style navigation buttons
        usersBtn = createSidebarButton("👤    Users");
        usersBtn.setBackground(Color.white);
        usersBtn.setForeground(Color.black);
        usersBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usersBtn.setPreferredSize(new Dimension(180, 40));
        usersBtn.setMinimumSize(new Dimension(180, 40));
        booksBtn = createSidebarButton("📘    Books");
        booksBtn.setBackground(Color.black);
        booksBtn.setForeground(Color.white);
        booksBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        booksBtn.setPreferredSize(new Dimension(180, 40));
        booksBtn.setMinimumSize(new Dimension(180, 40));
        
        navigation.add(bookEmojiLabel);
        navigation.add(titleLabel);
        navigation.add(Box.createRigidArea(new Dimension(0, 20)));
        navigation.add(usersBtn);
        navigation.add(Box.createRigidArea(new Dimension(0, 15)));
        navigation.add(booksBtn);
        
        // Set up the main content area with card layout
        page = new JPanel();
        CardLayout cardLayout = new CardLayout();
        page.setLayout(cardLayout);

        // Add user and book pages to the card layout
        booksPage = new BooksPage();
        usersPage = new UsersPage();
        
        page.add(usersPage, "users");
        page.add(booksPage, "books");

        // Handle navigation button clicks to switch pages
        usersBtn.addActionListener(e -> {
            cardLayout.show(page, "users");
            usersBtn.setBackground(Color.white);
            usersBtn.setForeground(Color.black);

            booksBtn.setBackground(Color.black);
            booksBtn.setForeground(Color.white);
        });

        booksBtn.addActionListener(e -> {
            cardLayout.show(page, "books");
            booksBtn.setBackground(Color.white);
            booksBtn.setForeground(Color.black);

            usersBtn.setBackground(Color.black);
            usersBtn.setForeground(Color.white);
        });

        add(navigation, BorderLayout.WEST);
        add(page, BorderLayout.CENTER);
    }

    // Create reusable method for styled buttons
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.white);
        btn.setBackground(Color.black);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 40));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return btn;
    }

    // Entry point to launch the admin application
    public static void main(String[] args) {
        new App().setVisible(true);
    }
}
