package bookNest.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import bookNest.database.DatabaseUtil;

// Represents a user in the library system
// Includes user details and borrowed books
public class User {
    private int id;
    private String name;
    private String email;
    private List<Book> borrowedBooks;

    public User(int id, String name, String email, List<Book> borrowedBooks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.borrowedBooks = borrowedBooks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
    
    // Methods for managing borrowed books
    public void addBorrowedBook(Book book) {
        borrowedBooks.add(book);
    }
    public void removeBorrowedBook(Book book) {
        borrowedBooks.remove(book);
    }

    public static List<User> getAllUsersFromDB() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                List<Book> borrowedBooks = getBorrowedBooksForUser(userId, conn);
                users.add(new User(userId, name, email, borrowedBooks));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static List<Book> getBorrowedBooksForUser(int userId, Connection conn) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM books b JOIN borrowed_books bb ON b.id = bb.book_id WHERE bb.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("year"),
                        rs.getInt("copiesAvailable")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}

