package bookNest.client;

import javax.swing.*;
import java.awt.*;

import bookNest.models.User;
import bookNest.client.components.BooksPage;
import bookNest.client.components.BorrowedBooksPage;

public class App extends JFrame {

    // Represents the main client application window

    private JPanel page;
    private JButton booksBtn, borrowedBooksBtn;
    private User currentUser;
    private JPanel navigation;

    public App(User user) {
        super("Client Library App");
        this.currentUser = user;

        // Set up the application window properties
        setSize(1000, 600);
        setMinimumSize(new Dimension(1000, 600));
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
        JLabel bookEmojiLabel = new JLabel("📖");
        bookEmojiLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        bookEmojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookEmojiLabel.setBackground(Color.BLACK);
        bookEmojiLabel.setForeground(Color.WHITE);
        

        //split the name and take the first word
        JLabel titleLabel = new JLabel(user.getName().split(" ")[0], SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBackground(Color.BLACK);
        titleLabel.setForeground(Color.WHITE);

        navigation.add(bookEmojiLabel);
        navigation.add(titleLabel);

        // Create and style navigation buttons
        booksBtn = createSidebarButton("All books");
        booksBtn.setBackground(Color.white);
        booksBtn.setForeground(Color.black);
        borrowedBooksBtn = createSidebarButton("Borrowed");

        // Add navigation buttons to the navigation panel
        navigation.add(Box.createRigidArea(new Dimension(0, 20)));
        navigation.add(booksBtn);
        navigation.add(Box.createRigidArea(new Dimension(0, 15)));
        navigation.add(borrowedBooksBtn);

        // Set up the main content area with card layout
        page = new JPanel();
        CardLayout cardLayout = new CardLayout();
        page.setLayout(cardLayout);

        // Add book and borrowed book pages to the card layout
        BooksPage booksPage = new BooksPage(currentUser);
        BorrowedBooksPage borrowedBooksPage = new BorrowedBooksPage(currentUser);

        // --- Keep both pages in sync after borrow/return ---
        booksPage.addBorrowListener(() -> borrowedBooksPage.refresh());
        borrowedBooksPage.addReturnListener(() -> booksPage.updateBooksTable(bookNest.models.Book.getAllBooksFromDB()));

        page.add(booksPage, "books");
        page.add(borrowedBooksPage, "borrowedBooks");

        // Handle navigation button clicks to switch pages
        booksBtn.addActionListener(e -> {
            cardLayout.show(page, "books");
            booksBtn.setBackground(Color.white);
            booksBtn.setForeground(Color.black);
            borrowedBooksBtn.setBackground(Color.black);
            borrowedBooksBtn.setForeground(Color.white);
        });

        borrowedBooksBtn.addActionListener(e -> {
            borrowedBooksPage.refresh(); // Always refresh when switching to this page
            cardLayout.show(page, "borrowedBooks");
            borrowedBooksBtn.setBackground(Color.white);
            borrowedBooksBtn.setForeground(Color.black);
            booksBtn.setBackground(Color.black);
            booksBtn.setForeground(Color.white);
        });

        add(navigation, BorderLayout.WEST);
        add(page, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.white);
        btn.setBackground(Color.black);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 40));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Fill width
        btn.setPreferredSize(new Dimension(180, 40)); // Match sidebar width
        btn.setMinimumSize(new Dimension(180, 40));
        return btn;
    }

    // Entry point to launch the client application
    public static void main(String[] args) {
        JFrame authFrame = new JFrame("Welcome to BookNest");
        authFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        authFrame.setSize(400, 350);
        authFrame.setResizable(false);
        authFrame.setLocationRelativeTo(null);
        authFrame.setContentPane(new bookNest.client.auth.Auth(authFrame));
        authFrame.setVisible(true);
    }
}