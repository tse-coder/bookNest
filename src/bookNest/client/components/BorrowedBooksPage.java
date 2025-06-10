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

// Represents the client page for viewing and returning borrowed books
public class BorrowedBooksPage extends JPanel {

    private JTable borrowedBooksTable;
    private User currentUser;
    private JButton returnBookBtn;
    private Book selectedBook;

    // Listener for when a book is returned (for UI sync)
    private Runnable returnListener;

    public BorrowedBooksPage(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        
        // Borrowed Books Table
        borrowedBooksTable = new JTable();
        borrowedBooksTable.setRowHeight(28);
        borrowedBooksTable.setFont(new Font("SansSerif", Font.PLAIN, 15));
        borrowedBooksTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        borrowedBooksTable.getTableHeader().setBackground(Color.BLACK);
        borrowedBooksTable.getTableHeader().setForeground(Color.WHITE);
        borrowedBooksTable.setBackground(Color.WHITE);
        borrowedBooksTable.setForeground(Color.BLACK);
        borrowedBooksTable.setSelectionBackground(new Color(40, 44, 52)); // dark gray selection
        borrowedBooksTable.setSelectionForeground(Color.WHITE);
        borrowedBooksTable.setGridColor(new Color(220, 220, 220));
        borrowedBooksTable.setShowGrid(true);
        updateBorrowedBooksTable(fetchBorrowedBooksFromDB());
        JScrollPane borrowedBooksScrollPane = new JScrollPane(borrowedBooksTable);
        borrowedBooksScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(borrowedBooksScrollPane, BorderLayout.CENTER);

        // --- Modern Return Book Button ---
        returnBookBtn = new JButton("\u21A9\uFE0F  Return Book");
        returnBookBtn.setEnabled(false);
        returnBookBtn.setBackground(Color.BLACK);
        returnBookBtn.setForeground(Color.WHITE);
        returnBookBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        returnBookBtn.setFocusPainted(false);
        returnBookBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 1, true),
            BorderFactory.createEmptyBorder(10, 36, 10, 36)
        ));
        returnBookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        returnBookBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                returnBookBtn.setBackground(new Color(30,30,30));
            }
            public void mouseExited(MouseEvent e) {
                returnBookBtn.setBackground(Color.BLACK);
            }
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        btnPanel.add(returnBookBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Table selection logic
        borrowedBooksTable.getSelectionModel().addListSelectionListener(e -> {
            int row = borrowedBooksTable.getSelectedRow();
            if (row != -1) {
                int bookId = (int) borrowedBooksTable.getValueAt(row, 0);
                selectedBook = fetchBorrowedBooksFromDB().stream()
                        .filter(book -> book.getId() == bookId)
                        .findFirst()
                        .orElse(null);
                returnBookBtn.setEnabled(selectedBook != null);
            } else {
                returnBookBtn.setEnabled(false);
                selectedBook = null;
            }
        });

        // Return button action
        returnBookBtn.addActionListener(e -> {
            if (selectedBook != null) {
                if (Book.returnBook(currentUser, selectedBook)) {
                    JOptionPane.showMessageDialog(BorrowedBooksPage.this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (returnListener != null) returnListener.run();
                } else {
                    JOptionPane.showMessageDialog(BorrowedBooksPage.this, "Failed to return book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateBorrowedBooksTable(fetchBorrowedBooksFromDB());
                returnBookBtn.setEnabled(false);
            }
        });
    }

    public void refresh() {
        updateBorrowedBooksTable(fetchBorrowedBooksFromDB());
    }

    public void addReturnListener(Runnable r) { this.returnListener = r; }

    private List<Book> fetchBorrowedBooksFromDB() {
        // Always fetch the latest borrowed books for the user from the DB
        return bookNest.models.User.getAllUsersFromDB().stream()
                .filter(u -> u.getId() == currentUser.getId())
                .findFirst()
                .map(User::getBorrowedBooks)
                .orElse(List.of());
    }

    private void updateBorrowedBooksTable(List<Book> borrowedBooks) {
        String[] columnNames = {"ID", "Title", "Author", "Genre", "Year"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Book book : borrowedBooks) {
            model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getYear()});
        }
        borrowedBooksTable.setModel(model);
    }
}
