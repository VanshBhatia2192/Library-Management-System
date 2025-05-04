package com.library.views;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import com.library.views.panels.ManageBooksPanel;
import com.library.views.panels.StudentRecordsPanel;

public class LibrarianDashboard extends BaseDashboard {
    private String username;
    private static final String[] NAV_ITEMS = { "Manage Books", "Issued Books", "Student Records", "Overdue Notices", "View Requests", "View Fines", "Profile" };

    public LibrarianDashboard(String username) {
        super("Librarian Dashboard", username, "LIBRARIAN");
        this.username = username;
        showHomePanel();
    }

    @Override
    protected JPanel createSidebar() {
        return new JPanel(); // No sidebar
    }

    private void showHomePanel() {
        com.library.views.panels.HomePanel homePanel = new com.library.views.panels.HomePanel("LIBRARIAN");
        homePanel.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(homePanel);
    }

    private void handleNav(String nav) {
        switch (nav) {
            case "Manage Books":
                showManageBooks();
                break;
            case "Issued Books":
                showIssuedBooks();
                break;
            case "Student Records":
                showStudentRecords();
                break;
            case "Overdue Notices":
                showOverdueNotices();
                break;
            case "View Requests":
                showViewRequestsPanel();
                break;
            case "View Fines":
                showViewFinesPanel();
                break;
            case "Profile":
                showProfilePanel();
                break;
            case "GoBack":
                showHomePanel();
                break;
            case "Logout":
                handleLogout();
                break;
        }
    }

    private void showManageBooks() {
        JPanel panel = new ManageBooksPanel();
        JPanel wrapped = new com.library.views.panels.ModernCardPanel(panel);
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "Manage Books", true, wrapped);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private void showIssuedBooks() {
        JPanel panel = createIssuedBooksPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "Issued Books", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createIssuedBooksPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Issued Books");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);
        // If there are action/search buttons, wrap them in ModernSearchPanel here
        String[] columns = { "Book Title", "Borrower", "Borrow Date", "Due Date", "Status" };
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable issuedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        // Load issued books
        try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT b.title, u.username, bt.borrow_date, bt.due_date, bt.status " +
                                "FROM book_transactions bt " +
                                "JOIN books b ON bt.book_id = b.id " +
                                "JOIN users u ON bt.user_id = u.id " +
                                "WHERE bt.status IN ('BORROWED', 'OVERDUE')")) {
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("title"),
                            rs.getString("username"),
                            rs.getDate("borrow_date"),
                            rs.getDate("due_date"),
                            rs.getString("status")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(inner, "Error loading issued books: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showStudentRecords() {
        JPanel panel = new StudentRecordsPanel();
        JPanel wrapped = new com.library.views.panels.ModernCardPanel(panel);
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "Student Records", true, wrapped);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private void showOverdueNotices() {
        JPanel panel = createOverdueNoticesPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "Overdue Notices", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createOverdueNoticesPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Overdue Notices");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        String[] columns = { "Student", "Book Title", "Author", "Borrow Date", "Due Date", "Days Overdue" };
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable overdueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(overdueTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        // Load overdue books from the database
        try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT u.username, b.title, b.author, bt.borrow_date, bt.due_date, DATEDIFF(CURRENT_DATE, bt.due_date) AS days_overdue "
                                +
                                "FROM book_transactions bt " +
                                "JOIN books b ON bt.book_id = b.id " +
                                "JOIN users u ON bt.user_id = u.id " +
                                "WHERE bt.status = 'OVERDUE' ORDER BY bt.due_date ASC")) {
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("username"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getDate("borrow_date"),
                            rs.getDate("due_date"),
                            rs.getInt("days_overdue")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(inner, "Error loading overdue notices: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showProfilePanel() {
        JPanel panel = createProfilePanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "Profile", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createProfilePanel() {
        return new com.library.views.panels.ModernCardPanel(
                new com.library.views.panels.ProfilePanel(currentUser, userRole));
    }

    private void showViewRequestsPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Book Requests");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        String[] columns = { "Student", "Book Title", "Author", "Request Date" };
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable requestsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        // Load book requests from the database
        try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT u.username, br.title, br.author, br.request_date " +
                                "FROM book_requests br JOIN users u ON br.user_id = u.id ORDER BY br.request_date DESC")) {
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("username"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getTimestamp("request_date")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(inner, "Error loading book requests: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JPanel wrapped = new com.library.views.panels.ModernCardPanel(inner);
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "View Requests", true, wrapped);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private void showViewFinesPanel() {
        JPanel panel = createViewFinesPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "LIBRARIAN", NAV_ITEMS, "View Fines", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createViewFinesPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        
        // Header section
        JLabel heading = new JLabel("View Student Fines");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // Table for fines
        String[] columns = { "Student Name", "Book Title", "Due Date", "Days Overdue", "Fine Amount (₹)" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable finesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(finesTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        // Modern action bar
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total fines label
        JLabel totalFinesLabel = new JLabel("Total Outstanding Fines: ₹0.00");
        totalFinesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalFinesLabel.setForeground(new Color(60, 60, 60));
        actionPanel.add(totalFinesLabel, BorderLayout.WEST);

        inner.add(actionPanel, BorderLayout.SOUTH);

        // Load fines data
        Runnable loadFines = () -> {
            tableModel.setRowCount(0);
            double totalFines = 0.0;
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                String sql = "SELECT u.username, b.title, bt.due_date, " +
                           "GREATEST(0, DATEDIFF(CURRENT_DATE, bt.due_date)) as days_overdue " +
                           "FROM book_transactions bt " +
                           "JOIN books b ON bt.book_id = b.id " +
                           "JOIN users u ON bt.user_id = u.id " +
                           "WHERE bt.status IN ('BORROWED', 'OVERDUE') " +
                           "AND CURRENT_DATE > bt.due_date " +
                           "ORDER BY u.username, bt.due_date";
                
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            int daysOverdue = rs.getInt("days_overdue");
                            double fineAmount = daysOverdue * 20.0; // ₹20 per day
                            totalFines += fineAmount;

                            Object[] row = {
                                rs.getString("username"),
                                rs.getString("title"),
                                rs.getDate("due_date"),
                                daysOverdue,
                                String.format("%.2f", fineAmount)
                            };
                            tableModel.addRow(row);
                        }
                    }
                }
                // Update total fines label
                totalFinesLabel.setText(String.format("Total Outstanding Fines: ₹%.2f", totalFines));
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error loading fines: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // Initial load
        loadFines.run();

        return new com.library.views.panels.ModernCardPanel(inner);
    }

    @Override
    protected void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginView().setVisible(true);
        }
    }

    @Override
    protected void cleanup() {
        // Add any cleanup code here
    }
}