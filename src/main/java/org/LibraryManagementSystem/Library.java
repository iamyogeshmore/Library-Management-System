package org.LibraryManagementSystem;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private List<Borrower> borrowers;
    private List<Integer> borrowedBooks;

    public Library() {
        books = new ArrayList<>();
        borrowers = new ArrayList<>();
        borrowedBooks = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void addBorrower(Borrower borrower) {
        borrowers.add(borrower);
    }

    public Book findBookById(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    public Borrower findBorrowerById(int id) {
        for (Borrower borrower : borrowers) {
            if (borrower.getId() == id) {
                return borrower;
            }
        }
        return null;
    }

    public void borrowBook(int bookId, int borrowerId) {
        Book book = findBookById(bookId);
        Borrower borrower = findBorrowerById(borrowerId);

        if (book != null && borrower != null && book.isAvailable()) {
            book.setAvailable(false);
            borrowedBooks.add(book.getId());
            System.out.println("Book borrowed successfully!");
        } else {
            System.out.println("Invalid book ID or borrower ID, or the book is not available.");
        }
    }

    public void returnBook(int bookId, int borrowerId) {
        Book book = findBookById(bookId);
        Borrower borrower = findBorrowerById(borrowerId);

        if (book != null && borrower != null && !book.isAvailable()) {
            book.setAvailable(true);
            borrowedBooks.remove(Integer.valueOf(book.getId()));
            System.out.println("Book returned successfully!");
            System.out.println("Returned Book Details:");
            System.out.println("Book ID: " + book.getId());
            System.out.println("Book Title: " + book.getTitle());
            System.out.println("Book Author: " + book.getAuthor());
            System.out.println("Borrower Details:");
            System.out.println("Borrower ID: " + borrower.getId());
            System.out.println("Borrower Name: " + borrower.getName());
        } else {
            System.out.println("Invalid book ID, borrower ID, or the book is already available.");
        }
    }

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
}