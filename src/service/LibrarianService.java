package service;

import model.Book;
import model.BookIssue;
import model.Student;
import java.util.*;
import java.util.stream.Collectors;

public class LibrarianService {
    private List<Book> books;
    private List<BookIssue> bookIssues;
    private List<Student> students;
    private static final int MAX_BOOKS_PER_STUDENT = 3;
    private static final int LOAN_PERIOD_DAYS = 14;

    public LibrarianService() {
        this.books = new ArrayList<>();
        this.bookIssues = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    // Book Management
    public void addBook(Book book) {
        books.add(book);
    }

    public void deleteBook(int bookId) {
        books.removeIf(book -> book.getBookId() == bookId);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public Book getBookById(int bookId) {
        return books.stream()
                .filter(book -> book.getBookId() == bookId)
                .findFirst()
                .orElse(null);
    }

    // Book Issue Management
    public boolean issueBook(int bookId, int studentId) {
        Book book = getBookById(bookId);
        Student student = getStudentById(studentId);

        if (book == null || student == null || !book.isAvailable()) {
            return false;
        }

        // Check if student has reached maximum book limit
        long activeIssues = bookIssues.stream()
                .filter(issue -> issue.getStudentId() == studentId && !issue.isReturned())
                .count();

        if (activeIssues >= MAX_BOOKS_PER_STUDENT) {
            return false;
        }

        // Create new book issue
        Calendar calendar = Calendar.getInstance();
        Date issueDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, LOAN_PERIOD_DAYS);
        Date dueDate = calendar.getTime();

        BookIssue issue = new BookIssue(
                bookIssues.size() + 1,
                bookId,
                studentId,
                issueDate,
                dueDate
        );

        book.setAvailable(false);
        bookIssues.add(issue);
        return true;
    }

    public boolean returnBook(int issueId) {
        BookIssue issue = bookIssues.stream()
                .filter(i -> i.getIssueId() == issueId && !i.isReturned())
                .findFirst()
                .orElse(null);

        if (issue == null) {
            return false;
        }

        issue.setReturned(true);
        issue.setReturnDate(new Date());
        issue.calculateFine();

        Book book = getBookById(issue.getBookId());
        if (book != null) {
            book.setAvailable(true);
        }

        return true;
    }

    public List<BookIssue> getIssuedBooks() {
        return bookIssues.stream()
                .filter(issue -> !issue.isReturned())
                .collect(Collectors.toList());
    }

    public List<BookIssue> getOverdueBooks() {
        Date currentDate = new Date();
        return bookIssues.stream()
                .filter(issue -> !issue.isReturned() && currentDate.after(issue.getDueDate()))
                .collect(Collectors.toList());
    }

    // Student Management
    public void addStudent(Student student) {
        students.add(student);
    }

    public Student getStudentById(int studentId) {
        return students.stream()
                .filter(student -> student.getId() == studentId)
                .findFirst()
                .orElse(null);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // Fine Management
    public double calculateFine(int issueId) {
        BookIssue issue = bookIssues.stream()
                .filter(i -> i.getIssueId() == issueId)
                .findFirst()
                .orElse(null);

        if (issue == null) {
            return 0.0;
        }

        issue.calculateFine();
        return issue.getFine();
    }

    // Notification System
    public List<String> generateOverdueNotifications() {
        List<String> notifications = new ArrayList<>();
        Date currentDate = new Date();

        for (BookIssue issue : bookIssues) {
            if (!issue.isReturned() && currentDate.after(issue.getDueDate())) {
                Student student = getStudentById(issue.getStudentId());
                Book book = getBookById(issue.getBookId());
                
                if (student != null && book != null) {
                    String notification = String.format(
                        "Overdue Notice: Student %s has not returned book '%s' (ID: %d). Due date was %s",
                        student.getName(),
                        book.getTitle(),
                        book.getBookId(),
                        issue.getDueDate()
                    );
                    notifications.add(notification);
                }
            }
        }
        return notifications;
    }
} 