package model;

import java.util.Date;

public class BookIssue {
    private int issueId;
    private int bookId;
    private int studentId;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private double fine;
    private boolean isReturned;

    public BookIssue(int issueId, int bookId, int studentId, Date issueDate, Date dueDate) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isReturned = false;
        this.fine = 0.0;
    }

    // Getters and Setters
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    public void calculateFine() {
        if (returnDate != null && returnDate.after(dueDate)) {
            long diffInMillies = returnDate.getTime() - dueDate.getTime();
            long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
            this.fine = diffInDays * 1.0; // $1 per day fine
        }
    }
} 