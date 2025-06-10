package bookNest.client.components;

import bookNest.models.Book;
import bookNest.models.User;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

// Represents the client page for browsing and borrowing books
public class BooksPage extends JPanel {

    private JTable booksTable;
    private JTextField searchField;
    private User currentUser;
    private JButton borrowBookBtn;
    private Book selectedBook;

    // Listener for when a book is borrowed (for UI sync)
    private Runnable borrowListener;

    public BooksPage(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Modern Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));

        JLabel searchIcon = new JLabel("\uD83D\uDD0D"); // Unicode magnifier
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        searchIcon.setForeground(Color.DARK_GRAY);
        searchPanel.add(searchIcon);

        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220,220,220), 2, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        searchField.setBackground(new Color(245,245,245));
        searchField.setForeground(Color.BLACK);
        searchField.setPreferredSize(new Dimension(220, 36));
        searchPanel.add(Box.createRigidArea(new Dimension(8,0)));
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setBackground(Color.BLACK);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 1, true),
            BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> filterBooks());
        searchPanel.add(Box.createRigidArea(new Dimension(8,0)));
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Books Table with modern black/white theme
        booksTable = new JTable();
        booksTable.setRowHeight(28);
        booksTable.setFont(new Font("SansSerif", Font.PLAIN, 15));
        booksTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        booksTable.getTableHeader().setBackground(Color.BLACK);
        booksTable.getTableHeader().setForeground(Color.WHITE);
        booksTable.setBackground(Color.WHITE);
        booksTable.setForeground(Color.BLACK);
        booksTable.setSelectionBackground(new Color(40, 44, 52)); // dark gray selection
        booksTable.setSelectionForeground(Color.WHITE);
        booksTable.setGridColor(new Color(220, 220, 220));
        booksTable.setShowGrid(true);
        updateBooksTable(Book.getAllBooksFromDB());
        JScrollPane booksScrollPane = new JScrollPane(booksTable);
        booksScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(booksScrollPane, BorderLayout.CENTER);

        // --- Modern Borrow Book Button ---
        borrowBookBtn = new JButton("\uD83D\uDCDA  Borrow Book");
        borrowBookBtn.setEnabled(false);
        borrowBookBtn.setBackground(new Color(0, 0, 0));
        borrowBookBtn.setForeground(Color.WHITE);
        borrowBookBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        borrowBookBtn.setFocusPainted(false);
        borrowBookBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 1, true),
            BorderFactory.createEmptyBorder(10, 36, 10, 36)
        ));
        borrowBookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        borrowBookBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                borrowBookBtn.setBackground(new Color(30,30,30));
            }
            public void mouseExited(MouseEvent e) {
                borrowBookBtn.setBackground(Color.BLACK);
            }
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        btnPanel.add(borrowBookBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Table selection logic
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            int row = booksTable.getSelectedRow();
            if (row != -1) {
                int bookId = (int) booksTable.getValueAt(row, 0);
                selectedBook = Book.getAllBooksFromDB().stream()
                        .filter(book -> book.getId() == bookId)
                        .findFirst()
                        .orElse(null);
                // Enable button only if book is available
                borrowBookBtn.setEnabled(selectedBook != null && selectedBook.getCopiesAvailable() > 0);
            } else {
                borrowBookBtn.setEnabled(false);
                selectedBook = null;
            }
        });

        // Borrow button action
        borrowBookBtn.addActionListener(e -> {
            if (selectedBook != null && selectedBook.getCopiesAvailable() > 0) {
                if (Book.borrowBook(currentUser, selectedBook)) {
                    JOptionPane.showMessageDialog(BooksPage.this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (borrowListener != null) borrowListener.run();
                } else {
                    JOptionPane.showMessageDialog(BooksPage.this, "Failed to borrow book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateBooksTable(Book.getAllBooksFromDB());
                borrowBookBtn.setEnabled(false);
            }
        });
    }

    // Initialize UI components and layout
    public void updateBooksTable(List<Book> books) {
        String[] columnNames = {"ID", "Title", "Author", "Genre", "Year", "Available Copies"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Book book : books) {
            model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getYear(), book.getCopiesAvailable()});
        }
        booksTable.setModel(model);
    }

    // Configure table for displaying book details
    private void filterBooks() {
        String query = searchField.getText().toLowerCase();
        List<Book> filteredBooks = Book.getAllBooksFromDB().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query) || book.getAuthor().toLowerCase().contains(query))
                .toList();
        updateBooksTable(filteredBooks);
    }

    // Handle book borrowing actions
    public void addBorrowListener(Runnable r) { this.borrowListener = r; }
}
