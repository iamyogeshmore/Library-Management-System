package org.LibraryManagementSystem;

import java.util.Scanner;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        // Add sample books
        library.addBook(new Book(1, "Java Programming", "John Smith"));
        library.addBook(new Book(2, "Clean Code", "Robert C. Martin"));
        library.addBook(new Book(3, "Python Basics", "Jane Doe"));
        library.addBook(new Book(4, "Let Us C", "Yashavant Kanetkar"));
        library.addBook(new Book(5, "Eloquent JavaScript", "Marijn Haverbeke"));
        library.addBook(new Book(6, "Eloquent Ruby ", "Russ Olsen"));
        library.addBook(new Book(7, "Programming Pearls", "Joe Bentley"));

        // Add borrowers from console
        System.out.print("Enter the number of borrowers: ");
        int numBorrowers = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numBorrowers; i++) {
            System.out.println("Enter details for Borrower " + (i + 1) + ":");
            System.out.print("Enter borrower Id: ");
            int borrowerId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter borrower name: ");
            String borrowerName = scanner.nextLine();

            library.addBorrower(new Borrower(borrowerId, borrowerName));
        }

        while (true) {
            System.out.println("\n----- Library Management System -----");
            System.out.println("1. Show all available books");
            System.out.println("2. Search for a book");
            System.out.println("3. Borrow a book");
            System.out.println("4. Return a book");
            System.out.println("5. Generate Reports");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    library.showAllBooks();
                    break;
                case 2:
                    System.out.print("Enter the book Id: ");
                    int bookId = scanner.nextInt();
                    scanner.nextLine();

                    Book book = library.findBookById(bookId);
                    if (book != null) {
                        System.out.println("Book found!");
                        System.out.println("Title: " + book.getTitle());
                        System.out.println("Author: " + book.getAuthor());
                        System.out.println("Availability: " + (book.isAvailable() ? "Available" : "Not Available"));
                    } else {
                        System.out.println("Book not found!");
                    }
                    break;

                case 3:
                    System.out.print("Enter the book Id: ");
                    int borrowBookId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter the borrower Id: ");
                    int borrowerId = scanner.nextInt();
                    scanner.nextLine();

                    library.borrowBook(borrowBookId, borrowerId);
                    break;

                case 4:
                    System.out.print("Enter the book Id: ");
                    int returnBookId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter the borrower Id: ");
                    int returnBorrowerId = scanner.nextInt();
                    scanner.nextLine();

                    library.returnBook(returnBookId, returnBorrowerId);
                    break;

                case 5:
                    library.generateReports();
                    break;

                case 0:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}