package com.library.views.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.library.utils.DatabaseConnection;

public class StudentRecordsPanel extends JPanel {
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JTable borrowedBooksTable;
    private DefaultTableModel borrowedBooksModel;
    private JLabel studentInfoLabel;
    private JLabel overdueStatusLabel;
    private JLabel bookLimitLabel;

    public StudentRecordsPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadStudents();
    }

    private void initializeComponents() {
        // Create top panel for search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        studentInfoLabel = new JLabel("Search by student ID or name");
        overdueStatusLabel = new JLabel("");
        bookLimitLabel = new JLabel("");
        
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(studentInfoLabel);
        topPanel.add(overdueStatusLabel);
        topPanel.add(bookLimitLabel);

        // Create students table
        String[] columns = {"ID", "Name", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int studentId = (int) tableModel.getValueAt(selectedRow, 0);
                    showStudentDetails(studentId);
                }
            }
        });

        // Create borrowed books table
        String[] borrowedColumns = {"Book Title", "Borrow Date", "Due Date", "Status"};
        borrowedBooksModel = new DefaultTableModel(borrowedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        borrowedBooksTable = new JTable(borrowedBooksModel);

        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(studentsTable),
            new JScrollPane(borrowedBooksTable));
        splitPane.setResizeWeight(0.5);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchStudents());
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT id, full_name, email, is_active FROM users WHERE role = 'STUDENT'")) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getBoolean("is_active") ? "Active" : "Inactive"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT id, full_name, email, is_active FROM users " +
                 "WHERE role = 'STUDENT' AND (id LIKE ? OR full_name LIKE ? OR email LIKE ?)")) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getBoolean("is_active") ? "Active" : "Inactive"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error searching students: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStudentDetails(int studentId) {
        borrowedBooksModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Get student info
            try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT full_name, email FROM users WHERE id = ?")) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String studentName = rs.getString("full_name");
                        String studentEmail = rs.getString("email");
                        studentInfoLabel.setText(String.format("Student: %s (%s)", studentName, studentEmail));
                    }
                }
            }

            // Get borrowed books
            try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT b.title, bt.borrow_date, bt.due_date, bt.status " +
                "FROM book_transactions bt " +
                "JOIN books b ON bt.book_id = b.id " +
                "WHERE bt.user_id = ? AND bt.status IN ('BORROWED', 'OVERDUE')")) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                            rs.getString("title"),
                            rs.getDate("borrow_date"),
                            rs.getDate("due_date"),
                            rs.getString("status")
                        };
                        borrowedBooksModel.addRow(row);
                    }
                }
            }

            // Check overdue status
            try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM book_transactions " +
                "WHERE user_id = ? AND status = 'OVERDUE'")) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int overdueCount = rs.getInt(1);
                        overdueStatusLabel.setText(String.format("Overdue Books: %d", overdueCount));
                        overdueStatusLabel.setForeground(overdueCount > 0 ? Color.RED : Color.GREEN);
                    }
                }
            }

            // Check book limit
            try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM book_transactions " +
                "WHERE user_id = ? AND status = 'BORROWED'")) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int borrowedCount = rs.getInt(1);
                        bookLimitLabel.setText(String.format("Books Borrowed: %d/3", borrowedCount));
                        bookLimitLabel.setForeground(borrowedCount >= 3 ? Color.RED : Color.GREEN);
                    }
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading student details: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 