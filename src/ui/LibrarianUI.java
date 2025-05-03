package ui;

import model.Book;
import model.BookIssue;
import model.Student;
import service.LibrarianService;
import config.DatabaseConfig;
import java.util.List;
import java.util.Scanner;

public class LibrarianUI {
    private LibrarianService librarianService;
    private Scanner scanner;

    public LibrarianUI() {
        this.librarianService = new LibrarianService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            running = processChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Library Management System - Librarian Portal ===");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Delete Book");
        System.out.println("4. Issue Book");
        System.out.println("5. View Issued Books");
        System.out.println("6. Return Book");
        System.out.println("7. Add Student");
        System.out.println("8. View All Students");
        System.out.println("9. View Overdue Books");
        System.out.println("10. Generate Overdue Notifications");
        System.out.println("11. Change Database Password");
        System.out.println("0. Logout");
    }

    private boolean processChoice(int choice) {
        switch (choice) {
            case 1:
                addBook();
                break;
            case 2:
                viewAllBooks();
                break;
            case 3:
                deleteBook();
                break;
            case 4:
                issueBook();
                break;
            case 5:
                viewIssuedBooks();
                break;
            case 6:
                returnBook();
                break;
            case 7:
                addStudent();
                break;
            case 8:
                viewAllStudents();
                break;
            case 9:
                viewOverdueBooks();
                break;
            case 10:
                generateOverdueNotifications();
                break;
            case 11:
                changeDatabasePassword();
                break;
            case 0:
                System.out.println("Logging out...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    private void addBook() {
        System.out.println("\n=== Add New Book ===");
        int bookId = getIntInput("Enter Book ID: ");
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();

        Book book = new Book(bookId, title, author, isbn, category);
        librarianService.addBook(book);
        System.out.println("Book added successfully!");
    }

    private void viewAllBooks() {
        System.out.println("\n=== All Books ===");
        List<Book> books = librarianService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            books.forEach(System.out::println);
        }
    }

    private void deleteBook() {
        System.out.println("\n=== Delete Book ===");
        int bookId = getIntInput("Enter Book ID to delete: ");
        librarianService.deleteBook(bookId);
        System.out.println("Book deleted successfully!");
    }

    private void issueBook() {
        System.out.println("\n=== Issue Book ===");
        int bookId = getIntInput("Enter Book ID: ");
        int studentId = getIntInput("Enter Student ID: ");

        if (librarianService.issueBook(bookId, studentId)) {
            System.out.println("Book issued successfully!");
        } else {
            System.out.println("Failed to issue book. Please check if the book is available and student is eligible.");
        }
    }

    private void viewIssuedBooks() {
        System.out.println("\n=== Issued Books ===");
        List<BookIssue> issues = librarianService.getIssuedBooks();
        if (issues.isEmpty()) {
            System.out.println("No books are currently issued.");
        } else {
            issues.forEach(System.out::println);
        }
    }

    private void returnBook() {
        System.out.println("\n=== Return Book ===");
        int issueId = getIntInput("Enter Issue ID: ");
        
        if (librarianService.returnBook(issueId)) {
            double fine = librarianService.calculateFine(issueId);
            if (fine > 0) {
                System.out.printf("Book returned successfully! Fine amount: $%.2f%n", fine);
            } else {
                System.out.println("Book returned successfully! No fine applicable.");
            }
        } else {
            System.out.println("Failed to return book. Please check the Issue ID.");
        }
    }

    private void addStudent() {
        System.out.println("\n=== Add New Student ===");
        int id = getIntInput("Enter Student ID: ");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();

        Student student = new Student(id, name, email, phone, department);
        librarianService.addStudent(student);
        System.out.println("Student added successfully!");
    }

    private void viewAllStudents() {
        System.out.println("\n=== All Students ===");
        List<Student> students = librarianService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students registered.");
        } else {
            students.forEach(System.out::println);
        }
    }

    private void viewOverdueBooks() {
        System.out.println("\n=== Overdue Books ===");
        List<BookIssue> overdueBooks = librarianService.getOverdueBooks();
        if (overdueBooks.isEmpty()) {
            System.out.println("No overdue books.");
        } else {
            overdueBooks.forEach(System.out::println);
        }
    }

    private void generateOverdueNotifications() {
        System.out.println("\n=== Overdue Notifications ===");
        List<String> notifications = librarianService.generateOverdueNotifications();
        if (notifications.isEmpty()) {
            System.out.println("No overdue notifications to generate.");
        } else {
            notifications.forEach(System.out::println);
        }
    }

    private void changeDatabasePassword() {
        System.out.println("\n=== Change Database Password ===");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        if (!currentPassword.equals(DatabaseConfig.getPassword())) {
            System.out.println("Current password is incorrect!");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match!");
            return;
        }

        DatabaseConfig.changePassword(newPassword);
        System.out.println("Database password changed successfully!");
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
} 