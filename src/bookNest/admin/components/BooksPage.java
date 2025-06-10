package bookNest.admin.components;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import bookNest.models.Book;

// Represents the admin page for managing books
public class BooksPage extends JPanel {

    /* ---------------- data ---------------- */
    private List<Book> books = Book.getAllBooksFromDB(); 

    /* ---------------- ui parts ------------- */
    private final DefaultTableModel model;
    private final JTable table;

    // info labels
    private final JLabel idLbl      = makeInfoLabel();
    private final JLabel titleLbl   = makeInfoLabel();
    private final JLabel authorLbl  = makeInfoLabel();
    private final JLabel genreLbl   = makeInfoLabel();
    private final JLabel yearLbl    = makeInfoLabel();
    private final JLabel copiesLbl  = makeInfoLabel();

    public BooksPage() {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);

        // Table setup with modern black/white theme
        String[] cols = {"ID", "Title", "Author", "Genre", "Year", "Copies"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        updateTableRows(books);

        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(Color.BLACK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(40, 44, 52)); // dark gray selection
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search and Add Book Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(220, 220, 220)); // Light gray background
        JTextField searchField = new JTextField(20);
        JButton addBookBtn = new JButton("Add Book");
        addBookBtn.setBackground(Color.BLACK); 
        addBookBtn.setForeground(Color.WHITE);
        addBookBtn.setFocusPainted(false);
        addBookBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBookBtn.setPreferredSize(new Dimension(100, 30)); // Set button height
        searchField.setPreferredSize(new Dimension(200, 30)); // Match height with button
        topPanel.add(searchField);
        topPanel.add(addBookBtn);

        // Remove Book button
        JButton removeBookBtn = new JButton("Remove Book");
        removeBookBtn.setBackground(Color.RED);
        removeBookBtn.setForeground(Color.WHITE);
        removeBookBtn.setFocusPainted(false);
        removeBookBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        removeBookBtn.setPreferredSize(new Dimension(120, 30));
        topPanel.add(removeBookBtn);

        // Update Book button
        JButton updateBookBtn = new JButton("Update Book");
        updateBookBtn.setBackground(Color.ORANGE);
        updateBookBtn.setForeground(Color.WHITE);
        updateBookBtn.setFocusPainted(false);
        updateBookBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        updateBookBtn.setPreferredSize(new Dimension(120, 30));
        topPanel.add(updateBookBtn);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterBooks(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterBooks(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterBooks(searchField.getText()); }
        });

        addBookBtn.addActionListener(e -> showAddBookDialog());
        removeBookBtn.addActionListener(e -> removeSelectedBook());
        updateBookBtn.addActionListener(e -> updateSelectedBook());

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setPreferredSize(new Dimension(350, 0)); // Set fixed width
        infoPanel.add(new JLabel("ID:")); infoPanel.add(idLbl);
        infoPanel.add(new JLabel("Title:")); infoPanel.add(titleLbl);
        infoPanel.add(new JLabel("Author:")); infoPanel.add(authorLbl);
        infoPanel.add(new JLabel("Genre:")); infoPanel.add(genreLbl);
        infoPanel.add(new JLabel("Year:")); infoPanel.add(yearLbl);
        infoPanel.add(new JLabel("Copies:")); infoPanel.add(copiesLbl);

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);

        // Table selection logic to update info panel
        table.getSelectionModel().addListSelectionListener(this::rowSelected);
    }

    /* -------- helper ui builder -------- */

    private JLabel makeInfoLabel() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(Color.BLACK);
        return l;
    }

    /* -------- handle row click -------- */
    private void rowSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Get book from the model, not the original books list
                int bookId = (int) model.getValueAt(row, 0);
                Book b = books.stream().filter(book -> book.getId() == bookId).findFirst().orElse(null);
                if (b != null) {
                    idLbl.setText(String.valueOf(b.getId()));
                    titleLbl.setText(b.getTitle());
                    authorLbl.setText(b.getAuthor());
                    genreLbl.setText(b.getGenre());
                    yearLbl.setText(String.valueOf(b.getYear()));
                    copiesLbl.setText(String.valueOf(b.getCopiesAvailable()));
                }
            }
        }
    }

    private void updateTableRows(List<Book> list) {
        books = list;
        model.setRowCount(0);
        books.forEach(b -> model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(), b.getYear(), b.getCopiesAvailable()}));
    }

    private void filterBooks(String query) {
        String trimmedQuery = query.trim().toLowerCase();
        if (trimmedQuery.isEmpty()) {
            updateTableRows(Book.getAllBooksFromDB());
        } else {
            // Filter books whose title contains the search query (case-insensitive)
            List<Book> filtered = Book.getAllBooksFromDB().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(trimmedQuery))
                .collect(Collectors.toList());
            updateTableRows(filtered);
        }
    }

    private void showAddBookDialog() {
        JTextField titleF = new JTextField(15);
        JTextField authorF = new JTextField(15);
        JTextField genreF = new JTextField(15);
        JTextField yearF = new JTextField(5);
        JTextField copiesF = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Title:")); panel.add(titleF);
        panel.add(new JLabel("Author:")); panel.add(authorF);
        panel.add(new JLabel("Genre:")); panel.add(genreF);
        panel.add(new JLabel("Year:")); panel.add(yearF);
        panel.add(new JLabel("Copies:")); panel.add(copiesF);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleF.getText().trim();
                String author = authorF.getText().trim();
                String genre = genreF.getText().trim();
                int year = Integer.parseInt(yearF.getText().trim());
                int copies = Integer.parseInt(copiesF.getText().trim());
                if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) throw new Exception();
                // Add to database
                boolean success = bookNest.models.Book.addBookToDB(title, author, genre, year, copies);
                if (success) {
                    updateTableRows(bookNest.models.Book.getAllBooksFromDB());
                    filterBooks("");
                    JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add book to database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Add a remove book functionality
    private void removeSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) model.getValueAt(selectedRow, 0);
            boolean success = Book.removeBookFromDB(bookId);
            if (success) {
                updateTableRows(Book.getAllBooksFromDB());
                JOptionPane.showMessageDialog(this, "Book removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No book selected.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Add a method to update the information of a selected book
    private void updateSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) model.getValueAt(selectedRow, 0);
            String currentTitle = (String) model.getValueAt(selectedRow, 1);
            String currentAuthor = (String) model.getValueAt(selectedRow, 2);
            String currentGenre = (String) model.getValueAt(selectedRow, 3);
            int currentYear = (int) model.getValueAt(selectedRow, 4);
            int currentCopies = (int) model.getValueAt(selectedRow, 5);

            JTextField titleField = new JTextField(currentTitle);
            JTextField authorField = new JTextField(currentAuthor);
            JTextField genreField = new JTextField(currentGenre);
            JTextField yearField = new JTextField(String.valueOf(currentYear));
            JTextField copiesField = new JTextField(String.valueOf(currentCopies));

            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            panel.add(new JLabel("Title:")); panel.add(titleField);
            panel.add(new JLabel("Author:")); panel.add(authorField);
            panel.add(new JLabel("Genre:")); panel.add(genreField);
            panel.add(new JLabel("Year:")); panel.add(yearField);
            panel.add(new JLabel("Copies:")); panel.add(copiesField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Book Info", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String newTitle = titleField.getText().trim();
                    String newAuthor = authorField.getText().trim();
                    String newGenre = genreField.getText().trim();
                    int newYear = Integer.parseInt(yearField.getText().trim());
                    int newCopies = Integer.parseInt(copiesField.getText().trim());

                    boolean success = Book.updateBookInDB(bookId, newTitle, newAuthor, newGenre, newYear, newCopies);
                    if (success) {
                        updateTableRows(Book.getAllBooksFromDB());
                        JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update book.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No book selected.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
