package com.library.models;

import java.util.Date;

public class BookTransaction {
    private int id;
    private int bookId;
    private int userId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private double fine;
    private String status; // BORROWED, RETURNED, OVERDUE, RESERVED
    private String transactionType; // BORROW, RETURN, RESERVE

    public BookTransaction() {}

    public BookTransaction(int bookId, int userId, Date borrowDate, Date dueDate, String transactionType) {
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "BORROWED";
        this.transactionType = transactionType;
        this.fine = 0.0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void calculateFine() {
        if (returnDate != null && returnDate.after(dueDate)) {
            long diffInMillies = returnDate.getTime() - dueDate.getTime();
            long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
            this.fine = diffInDays * 1.0; // $1 per day fine
        }
    }

    @Override
    public String toString() {
        return "BookTransaction{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", fine=" + fine +
                ", status='" + status + '\'' +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
} 