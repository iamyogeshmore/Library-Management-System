package org.LibraryManagementSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private List<Borrower> borrowers;
    private List<Integer> borrowedBooks;
    private Connection connection;

    public Library() {
        books = new ArrayList<>();
        borrowers = new ArrayList<>();
        borrowedBooks = new ArrayList<>();
        connection = null;
    }

    //--------------------------------- Establish JDBC Connection ---------------------------------
    public void establishConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/library_db";
            String username = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to MySQL database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //--------------------------------- Close JDBC Connection ---------------------------------
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //--------------------------------- Add Books ---------------------------------
    public void addBook(Book book) {
        try {
            // Check if the book already exists in the database
            if (isBookExists(book)) {
                System.out.println("Book already exists in the database.");
                return;
            }
            saveBookToDatabase(book);// Save the book to the database
            books.add(book);
        } catch (SQLException e) {
            System.out.println("Error occurred while adding the book: " + e.getMessage());
        }
    }

    // ----------------------- Prepare the SQL query to check if the book exists -----------------------
    private boolean isBookExists(Book book) throws SQLException {

        String query = "SELECT COUNT(*) FROM books WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, book.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {  // Check if any rows are returned
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    //--------------------------------- insert the book into the database ---------------------------------
    private void saveBookToDatabase(Book book) throws SQLException {
        // Prepare the SQL query to insert the book into the database
        String query = "INSERT INTO books (id, title, author, available) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, book.getId());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setBoolean(4, book.isAvailable());

            statement.executeUpdate();
        }
    }

    //--------------------------------- insert borrower to the database ---------------------------------
    public void addBorrower(Borrower borrower) {
        try {
            // Check if the borrower already exists in the database
            if (isBorrowerExists(borrower)) {
                System.out.println("Borrower already exists in the database.");
                return;
            }
            saveBorrowerToDatabase(borrower); // Save the borrower to the database
            borrowers.add(borrower);  // Add the borrower to the local borrowers list

            System.out.println("Borrower added successfully!");
        } catch (SQLException e) {
            System.out.println("Error occurred while adding the borrower: " + e.getMessage());
        }
    }

    // ------------------ Prepare the SQL query to check if the borrower exists ------------------
    private boolean isBorrowerExists(Borrower borrower) throws SQLException {
        String query = "SELECT COUNT(*) FROM borrowers WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, borrower.getId());
            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()) {  // Check if any rows are returned
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    //--------------------------------- Save Borrower To Database ---------------------------------
    public void saveBorrowerToDatabase(Borrower borrower) throws SQLException {
        String query = "INSERT INTO borrowers (id, name) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, borrower.getId());
            statement.setString(2, borrower.getName());

            statement.executeUpdate();
        }
    }

    //--------------------------------- Find Book By Id ---------------------------------
    public Book findBookById(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    //--------------------------------- Find Borrower By Id ---------------------------------
    public Borrower findBorrowerById(int id) {
        for (Borrower borrower : borrowers) {
            if (borrower.getId() == id) {
                return borrower;
            }
        }
        return null;
    }

    //--------------------------------- Borrow Book ---------------------------------
    public void borrowBook(int bookId, int borrowerId) throws SQLException {
        Book book = findBookById(bookId);
        Borrower borrower = findBorrowerById(borrowerId);

        if (book != null && borrower != null && book.isAvailable()) {
            String updateBookQuery = "UPDATE books SET available = ? WHERE id = ?";
            String insertBorrowingQuery = "INSERT INTO borrowings (book_id, borrower_id) VALUES (?, ?)";

            try (PreparedStatement updateStatement = connection.prepareStatement(updateBookQuery);
                 PreparedStatement insertStatement = connection.prepareStatement(insertBorrowingQuery)) {
                // Update book availability
                updateStatement.setBoolean(1, false);
                updateStatement.setInt(2, book.getId());
                updateStatement.executeUpdate();

                // Insert borrowing details
                insertStatement.setInt(1, book.getId());
                insertStatement.setInt(2, borrower.getId());
                insertStatement.executeUpdate();

                book.setAvailable(false); // Update book availability in memory

                System.out.println("Book borrowed successfully!");
            }
        } else {
            System.out.println("Invalid book Id or borrower Id, or the book is not available.");
        }
    }

    //--------------------------------- Save Borrowing Details into Database ---------------------------------
    public void saveBorrowingDetails(int bookId, int borrowerId) throws SQLException {
        String query = "INSERT INTO borrowings (book_id, borrower_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookId);
            statement.setInt(2, borrowerId);

            statement.executeUpdate();
        }
    }

    //--------------------------------- Return Book ---------------------------------
    public void returnBook(int bookId, int borrowerId) throws SQLException {
        Book book = findBookById(bookId);
        Borrower borrower = findBorrowerById(borrowerId);

        if (book != null && borrower != null && !book.isAvailable()) {
            // Check if the borrower ID matches the borrower who borrowed the book
            String selectQuery = "SELECT * FROM borrowings WHERE book_id = ? AND borrower_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, bookId);
                selectStatement.setInt(2, borrowerId);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    // Borrower ID matches, proceed with returning the book
                    String updateBookQuery = "UPDATE books SET available = ? WHERE id = ?";
                    String updateReturnStatusQuery = "UPDATE borrowings SET returned = ? WHERE book_id = ? AND borrower_id = ?";
                    String insertReturnQuery = "INSERT INTO returns (book_id, borrower_id) VALUES (?, ?)";

                    try (PreparedStatement updateStatement = connection.prepareStatement(updateBookQuery);
                         PreparedStatement updateReturnStatusStatement = connection.prepareStatement(updateReturnStatusQuery);
                         PreparedStatement insertStatement = connection.prepareStatement(insertReturnQuery)) {
                        // Update book availability
                        updateStatement.setBoolean(1, true);
                        updateStatement.setInt(2, bookId);
                        updateStatement.executeUpdate();

                        // Update return status in borrowings table
                        updateReturnStatusStatement.setBoolean(1, true);
                        updateReturnStatusStatement.setInt(2, book.getId());
                        updateReturnStatusStatement.setInt(3, borrower.getId());
                        updateReturnStatusStatement.executeUpdate();


                        // Insert return details
                        insertStatement.setInt(1, bookId);
                        insertStatement.setInt(2, borrowerId);
                        insertStatement.executeUpdate();

                        book.setAvailable(true); // Update book availability in memory

                        System.out.println("Book returned successfully!");
                        System.out.println("Returned Book Details:");
                        System.out.println("Book ID: " + bookId);
                        System.out.println("Book Title: " + book.getTitle());
                        System.out.println("Book Author: " + book.getAuthor());
                        System.out.println("Borrower Details:");
                        System.out.println("Borrower ID: " + borrowerId);
                        System.out.println("Borrower Name: " + borrower.getName());
                    }
                } else {
                    System.out.println("The book was not borrowed by the specified borrower.");
                }
            }
        } else {
            System.out.println("Invalid book ID, borrower ID, or the book is already available.");
        }
    }

    //--------------------------------- Save Returning Details into Database ---------------------------------
    public void saveReturningDetails(int bookId, int borrowerId) throws SQLException {
        String query = "INSERT INTO returns (book_id, borrower_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookId);
            statement.setInt(2, borrowerId);

            statement.executeUpdate();
        }
    }

    //--------------------------------- Show All Books ---------------------------------
    public void showAllBooks() {
        System.out.println("---- All Books ----");
        for (Book book : books) {
            System.out.println("Book ID: " + book.getId());
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Availability: " + (book.isAvailable() ? "Available" : "Not Available"));
            System.out.println("-------------------");
        }
    }

    //--------------------------------- Generate Reports ---------------------------------
    public void generateReports() {
        System.out.println("---- Library Reports ----");
        System.out.println("Total Books: " + books.size());
        System.out.println("Total Borrowers: " + borrowers.size());
        System.out.println("Borrowed Books: " + borrowedBooks.size());

        System.out.println("---- Borrowed Books ----");
        for (int bookId : borrowedBooks) {
            Book book = findBookById(bookId);
            Borrower borrower = findBorrowerById(bookId);

            if (book != null && borrower != null) {
                System.out.println("Borrower ID: " + borrower.getId());
                System.out.println("Borrower Name: " + borrower.getName());
                System.out.println("Book ID: " + book.getId());
                System.out.println("Book Title: " + book.getTitle());
                System.out.println("-------------------------");
            }
        }
    }

    //--------------------------------- Create Tables ---------------------------------
    public void createTables() throws SQLException {
        String createBookTableQuery = "CREATE TABLE IF NOT EXISTS books (" +
                "id INT PRIMARY KEY," +
                "title VARCHAR(100) NOT NULL," +
                "author VARCHAR(100) NOT NULL," +
                "available BOOLEAN DEFAULT TRUE" +
                ")";
        String createBorrowerTableQuery = "CREATE TABLE IF NOT EXISTS borrowers (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL" +
                ")";
        String createBorrowingsTableQuery = "CREATE TABLE IF NOT EXISTS borrowings (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "book_id INT NOT NULL," +
                "borrower_id INT NOT NULL," +
                "returned BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (book_id) REFERENCES books(id)," +
                "FOREIGN KEY (borrower_id) REFERENCES borrowers(id)" +
                ")";
        String createReturnsTableQuery = "CREATE TABLE IF NOT EXISTS returns (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "book_id INT NOT NULL," +
                "borrower_id INT NOT NULL," +
                "FOREIGN KEY (book_id) REFERENCES books(id)," +
                "FOREIGN KEY (borrower_id) REFERENCES borrowers(id)" +
                ")";

        try (Statement statement = connection.createStatement()) {
            // Drop existing tables if they exist
            statement.executeUpdate("DROP TABLE IF EXISTS borrowings");
            statement.executeUpdate("DROP TABLE IF EXISTS returns");
            statement.executeUpdate("DROP TABLE IF EXISTS books");
            statement.executeUpdate("DROP TABLE IF EXISTS borrowers");

            // Create new tables
            statement.executeUpdate(createBookTableQuery);
            statement.executeUpdate(createBorrowerTableQuery);
            statement.executeUpdate(createBorrowingsTableQuery);
            statement.executeUpdate(createReturnsTableQuery);

            System.out.println("Database tables created successfully.");
        }
    }
}
