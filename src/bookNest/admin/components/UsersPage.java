package bookNest.admin.components;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import bookNest.models.User;

// Represents the admin page for managing users
public class UsersPage extends JPanel {

    /* ---------------- data ---------------- */
    private final List<User> users = User.getAllUsersFromDB();

    /* ---------------- ui parts ------------- */
    private final DefaultTableModel model;
    private final JTable table;

    // info labels
    private final JLabel idLbl      = makeInfoLabel();
    private final JLabel nameLbl    = makeInfoLabel();
    private final JLabel emailLbl   = makeInfoLabel();
    private final JLabel countLbl   = makeInfoLabel();

    private final JTextArea booksArea = new JTextArea();

    public UsersPage() {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);

        // Initialize UI components and layout
        // Table setup with modern black/white theme
        String[] cols = {"ID", "Name", "Email", "Borrowed"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        updateTableRows(users);

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

        // Search and Add User Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(220, 220, 220)); // Light gray background
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30)); // Match height with button
        topPanel.add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterUsers(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterUsers(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterUsers(searchField.getText()); }
        });

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setPreferredSize(new Dimension(350, 0)); // Set fixed width
        infoPanel.add(new JLabel("ID:")); infoPanel.add(idLbl);
        infoPanel.add(new JLabel("Name:")); infoPanel.add(nameLbl);
        infoPanel.add(new JLabel("Email:")); infoPanel.add(emailLbl);
        infoPanel.add(new JLabel("Borrowed:")); infoPanel.add(countLbl);

        // Borrowed Books Area
        booksArea.setEditable(false);
        booksArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        booksArea.setBackground(new Color(245, 245, 245));
        booksArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        booksArea.setLineWrap(true);
        booksArea.setWrapStyleWord(true);
        infoPanel.add(new JLabel("Borrowed Books:"));
        infoPanel.add(new JScrollPane(booksArea));

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);

        // Table selection logic to update info panel (fix: use filtered list)
        table.getSelectionModel().addListSelectionListener(e -> rowSelected(e));
    }

    /* -------- helper ui builder -------- */

    private JLabel makeInfoLabel() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return l;
    }

    /* -------- handle row click -------- */
    private List<User> filteredUsers = users;
    private void rowSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = table.getSelectedRow();
            if (row >= 0 && row < filteredUsers.size()) {
                User u = filteredUsers.get(row);
                idLbl.setText(String.valueOf(u.getId()));
                nameLbl.setText(u.getName());
                emailLbl.setText(u.getEmail());
                countLbl.setText(String.valueOf(u.getBorrowedBooks().size()));

                String list = u.getBorrowedBooks().isEmpty()
                        ? "\u2014 None \u2014"
                        : u.getBorrowedBooks().stream()
                              .map(b -> "\u2022 " + b.getTitle() + " by " + b.getAuthor())
                              .collect(Collectors.joining("\n"));
                booksArea.setText(list);
            }
        }
    }

    private void updateTableRows(List<User> list) {
        filteredUsers = list;
        model.setRowCount(0);
        list.forEach(u -> model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getBorrowedBooks().size()}));
    }

    private void filterUsers(String query) {
        String trimmedQuery = query.trim().toLowerCase();
        if (trimmedQuery.isEmpty()) {
            updateTableRows(users);
        } else {
            // Filter users whose name contains the search query (case-insensitive)
            List<User> filtered = users.stream()
                .filter(user -> user.getName().toLowerCase().contains(trimmedQuery))
                .collect(Collectors.toList());
            updateTableRows(filtered);
        }
    }
}
