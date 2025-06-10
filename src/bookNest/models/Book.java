package bookNest.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import bookNest.database.DatabaseUtil;

// Represents a book in the library system
// Includes book details and availability
public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private int year;
    private int copiesAvailable;

    public Book(int id, String title, String author, String genre, int year, int copiesAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.copiesAvailable = copiesAvailable;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }
    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    // Methods for borrowing and returning books
    public static List<Book> getAllBooksFromDB() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static boolean borrowBook(User user, Book book) {
        try (java.sql.Connection conn = bookNest.database.DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Decrement copiesAvailable
                try (java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE books SET copiesAvailable = copiesAvailable - 1 WHERE id = ? AND copiesAvailable > 0")) {
                    ps.setInt(1, book.getId());
                    int updated = ps.executeUpdate();
                    if (updated == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                // Add to borrowed_books
                try (java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO borrowed_books (user_id, book_id) VALUES (?, ?);")) {
                    ps.setInt(1, user.getId());
                    ps.setInt(2, book.getId());
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean returnBook(User user, Book book) {
        try (java.sql.Connection conn = bookNest.database.DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Remove from borrowed_books
                try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM borrowed_books WHERE user_id = ? AND book_id = ?")) {
                    ps.setInt(1, user.getId());
                    ps.setInt(2, book.getId());
                    int deleted = ps.executeUpdate();
                    if (deleted == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                // Increment copiesAvailable
                try (java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE books SET copiesAvailable = copiesAvailable + 1 WHERE id = ?")) {
                    ps.setInt(1, book.getId());
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addBookToDB(String title, String author, String genre, int year, int copiesAvailable) {
        String sql = "INSERT INTO books (title, author, genre, year, copiesAvailable) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = bookNest.database.DatabaseUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, genre);
            ps.setInt(4, year);
            ps.setInt(5, copiesAvailable);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}