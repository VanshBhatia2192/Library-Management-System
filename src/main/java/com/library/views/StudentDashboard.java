package com.library.views;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class StudentDashboard extends BaseDashboard {
    private JButton borrowBooksButton;
    private JButton returnBooksButton;
    private JButton viewStatusButton;
    private JButton requestNewBooksButton;
    private JButton reissueBooksButton;
    private JButton notificationsButton;

    private static final String[] NAV_ITEMS = { "Borrow", "Return", "Request", "Status", "Notifications", "Profile" };

    public StudentDashboard(String currentUser) {
        super("Student Dashboard - Library Management System", currentUser, "STUDENT");
        showHomePanel();
    }

    @Override
    protected JPanel createSidebar() {
        return new JPanel(); // No sidebar
    }

    private void handleButtonClick(JButton button) {
        // No sidebar buttons
    }

    private void showHomePanel() {
        com.library.views.panels.HomePanel homePanel = new com.library.views.panels.HomePanel("STUDENT");
        homePanel.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(homePanel);
    }

    private void handleNav(String nav) {
        switch (nav) {
            case "Borrow":
                showBorrowBooksPanel();
                break;
            case "Return":
                showReturnBooksPanel();
                break;
            case "Request":
                showRequestNewBooksPanel();
                break;
            case "Status":
                showViewStatusPanel();
                break;
            case "Notifications":
                showNotificationsPanel();
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

    private void showProfilePanel() {
        JPanel panel = createProfilePanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Profile", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Edit Profile"), BorderLayout.NORTH);
        JLabel label = new JLabel("Profile editing coming soon.", SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return new com.library.views.panels.ModernCardPanel(panel);
    }

    private void showBorrowBooksPanel() {
        JPanel panel = createBorrowBooksPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Borrow", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createBorrowBooksPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Borrow Selected Book");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // Modern search/filter bar
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JComboBox<String> categoryFilter = new JComboBox<>(
                new String[] { "All Categories", "Fiction", "Non-Fiction", "Science", "History", "Biography" });
        JButton borrowButton = new JButton("Borrow Selected Book");
        borrowButton.setFont(new Font("Arial", Font.BOLD, 16));
        borrowButton.setBackground(new Color(34, 87, 126));
        borrowButton.setForeground(new Color(30, 30, 30));
        borrowButton.setFocusPainted(false);
        borrowButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        JPanel searchPanel = new com.library.views.panels.ModernSearchPanel(
                new JLabel("Search:"), searchField, searchButton,
                new JLabel("Category:"), categoryFilter, borrowButton);
        inner.add(searchPanel, BorderLayout.NORTH);

        // Table for books
        String[] columns = { "ID", "Title", "Author", "ISBN", "Category", "Available Copies", "Total Copies" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        // Load books method
        Runnable loadBooks = () -> {
            tableModel.setRowCount(0);
            String searchTerm = searchField.getText().trim();
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
                if (!searchTerm.isEmpty()) {
                    sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
                }
                if (!selectedCategory.equals("All Categories")) {
                    sql.append(" AND category = ?");
                }
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    if (!searchTerm.isEmpty()) {
                        String searchPattern = "%" + searchTerm + "%";
                        pstmt.setString(paramIndex++, searchPattern);
                        pstmt.setString(paramIndex++, searchPattern);
                        pstmt.setString(paramIndex++, searchPattern);
                    }
                    if (!selectedCategory.equals("All Categories")) {
                        pstmt.setString(paramIndex, selectedCategory);
                    }
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getString("author"),
                                    rs.getString("isbn"),
                                    rs.getString("category"),
                                    rs.getInt("available_quantity"),
                                    rs.getInt("quantity")
                            };
                            tableModel.addRow(row);
                        }
                    }
                }
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner,
                        "Error loading books: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };

        // Borrow logic
        borrowButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(inner, "Please select a book to borrow.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            int available = (int) tableModel.getValueAt(selectedRow, 5);
            if (available <= 0) {
                JOptionPane.showMessageDialog(inner, "No available copies for this book.", "Unavailable",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                // Get user id from username
                int userId = -1;
                try (java.sql.PreparedStatement userStmt = conn
                        .prepareStatement("SELECT id FROM users WHERE username = ?")) {
                    userStmt.setString(1, currentUser);
                    try (java.sql.ResultSet rs = userStmt.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getInt("id");
                        }
                    }
                }
                if (userId == -1) {
                    JOptionPane.showMessageDialog(inner, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Check if student has reached max borrowed books (3)
                try (java.sql.PreparedStatement countStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM book_transactions WHERE user_id = ? AND status = 'BORROWED'")) {
                    countStmt.setInt(1, userId);
                    try (java.sql.ResultSet rs = countStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) >= 3) {
                            JOptionPane.showMessageDialog(inner,
                                    "You have reached the maximum number of borrowed books (3).", "Limit Reached",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
                // Insert into book_transactions
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                java.sql.Timestamp due = new java.sql.Timestamp(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000); // 14
                                                                                                                         // days
                try (java.sql.PreparedStatement borrowStmt = conn.prepareStatement(
                        "INSERT INTO book_transactions (book_id, user_id, borrow_date, due_date, status, transaction_type) VALUES (?, ?, ?, ?, 'BORROWED', 'BORROW')")) {
                    borrowStmt.setInt(1, bookId);
                    borrowStmt.setInt(2, userId);
                    borrowStmt.setTimestamp(3, now);
                    borrowStmt.setTimestamp(4, due);
                    borrowStmt.executeUpdate();
                }
                // Decrement available_quantity
                try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE books SET available_quantity = available_quantity - 1 WHERE id = ?")) {
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(inner, "You have successfully borrowed '" + title + "'.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBooks.run();
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error borrowing book: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add listeners
        searchButton.addActionListener(e -> loadBooks.run());
        categoryFilter.addActionListener(e -> loadBooks.run());
        // Initial load
        loadBooks.run();

        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showReturnBooksPanel() {
        JPanel panel = createReturnBooksPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Return", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createReturnBooksPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Return Selected Book");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // Prominent return button
        JButton returnButton = new JButton("Return Selected Book");
        returnButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnButton.setBackground(new Color(34, 87, 126));
        returnButton.setForeground(new Color(30, 30, 30));
        returnButton.setFocusPainted(false);
        returnButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(returnButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        inner.add(buttonPanel, BorderLayout.NORTH);

        // Modern action bar
        JPanel actionPanel = new com.library.views.panels.ModernSearchPanel(returnButton);
        inner.add(actionPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = { "Transaction ID", "Title", "Borrow Date", "Due Date", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable borrowedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        Runnable loadBorrowed = () -> {
            tableModel.setRowCount(0);
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                int userId = -1;
                try (java.sql.PreparedStatement userStmt = conn
                        .prepareStatement("SELECT id FROM users WHERE username = ?")) {
                    userStmt.setString(1, currentUser);
                    try (java.sql.ResultSet rs = userStmt.executeQuery()) {
                        if (rs.next())
                            userId = rs.getInt("id");
                    }
                }
                if (userId == -1)
                    return;
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT bt.id, b.title, bt.borrow_date, bt.due_date, bt.status, bt.book_id FROM book_transactions bt JOIN books b ON bt.book_id = b.id WHERE bt.user_id = ? AND bt.status IN ('BORROWED', 'OVERDUE')")) {
                    pstmt.setInt(1, userId);
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getDate("borrow_date"),
                                    rs.getDate("due_date"),
                                    rs.getString("status")
                            };
                            tableModel.addRow(row);
                        }
                    }
                }
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error loading borrowed books: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadBorrowed.run();

        returnButton.addActionListener(e -> {
            int selectedRow = borrowedTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(inner, "Please select a book to return.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                // Set transaction to RETURNED and set return_date
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE book_transactions SET status = 'RETURNED', return_date = ? WHERE id = ?")) {
                    pstmt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                    pstmt.setInt(2, transactionId);
                    pstmt.executeUpdate();
                }
                // Increment available_quantity
                try (java.sql.PreparedStatement getBookIdStmt = conn
                        .prepareStatement("SELECT book_id FROM book_transactions WHERE id = ?")) {
                    getBookIdStmt.setInt(1, transactionId);
                    try (java.sql.ResultSet rs = getBookIdStmt.executeQuery()) {
                        if (rs.next()) {
                            int bookId = rs.getInt("book_id");
                            try (java.sql.PreparedStatement updateBookStmt = conn.prepareStatement(
                                    "UPDATE books SET available_quantity = available_quantity + 1 WHERE id = ?")) {
                                updateBookStmt.setInt(1, bookId);
                                updateBookStmt.executeUpdate();
                            }
                        }
                    }
                }
                JOptionPane.showMessageDialog(inner, "Book returned successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBorrowed.run();
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error returning book: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showViewStatusPanel() {
        JPanel panel = createViewStatusPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Status", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createViewStatusPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("View Status");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // Table for borrowed books
        String[] columns = { "Transaction ID", "Title", "Borrow Date", "Due Date", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable borrowedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        Runnable loadBorrowed = () -> {
            tableModel.setRowCount(0);
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                int userId = -1;
                try (java.sql.PreparedStatement userStmt = conn
                        .prepareStatement("SELECT id FROM users WHERE username = ?")) {
                    userStmt.setString(1, currentUser);
                    try (java.sql.ResultSet rs = userStmt.executeQuery()) {
                        if (rs.next())
                            userId = rs.getInt("id");
                    }
                }
                if (userId == -1)
                    return;
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT bt.id, b.title, bt.borrow_date, bt.due_date, bt.status FROM book_transactions bt JOIN books b ON bt.book_id = b.id WHERE bt.user_id = ? AND bt.status IN ('BORROWED', 'OVERDUE')")) {
                    pstmt.setInt(1, userId);
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getDate("borrow_date"),
                                    rs.getDate("due_date"),
                                    rs.getString("status")
                            };
                            tableModel.addRow(row);
                        }
                    }
                }
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error loading borrowed books: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadBorrowed.run();
        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showRequestNewBooksPanel() {
        JPanel panel = createRequestNewBooksPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Request", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createRequestNewBooksPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Request New Books");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // TODO: Implement new book request functionality
        JLabel label = new JLabel("New Book Request Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);

        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showReissueBooksPanel() {
        JPanel panel = createReissueBooksPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Reissue", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createReissueBooksPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Reissue Selected Book");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // Prominent reissue button
        JButton reissueButton = new JButton("Reissue Selected Book");
        reissueButton.setFont(new Font("Arial", Font.BOLD, 16));
        reissueButton.setBackground(new Color(34, 87, 126));
        reissueButton.setForeground(new Color(30, 30, 30));
        reissueButton.setFocusPainted(false);
        reissueButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(reissueButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        inner.add(buttonPanel, BorderLayout.NORTH);

        // Modern action bar
        JPanel actionPanel = new com.library.views.panels.ModernSearchPanel(reissueButton);
        inner.add(actionPanel, BorderLayout.BEFORE_FIRST_LINE);

        String[] columns = { "Transaction ID", "Title", "Borrow Date", "Due Date", "Status", "Reissued" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable borrowedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedTable);
        inner.add(scrollPane, BorderLayout.CENTER);

        Runnable loadBorrowed = () -> {
            tableModel.setRowCount(0);
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                int userId = -1;
                try (java.sql.PreparedStatement userStmt = conn
                        .prepareStatement("SELECT id FROM users WHERE username = ?")) {
                    userStmt.setString(1, currentUser);
                    try (java.sql.ResultSet rs = userStmt.executeQuery()) {
                        if (rs.next())
                            userId = rs.getInt("id");
                    }
                }
                if (userId == -1)
                    return;
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT bt.id, b.title, bt.borrow_date, bt.due_date, bt.status, bt.transaction_type FROM book_transactions bt JOIN books b ON bt.book_id = b.id WHERE bt.user_id = ? AND bt.status = 'BORROWED'")) {
                    pstmt.setInt(1, userId);
                    try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            boolean reissued = "REISSUE".equals(rs.getString("transaction_type"));
                            Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("title"),
                                    rs.getDate("borrow_date"),
                                    rs.getDate("due_date"),
                                    rs.getString("status"),
                                    reissued ? "Yes" : "No"
                            };
                            tableModel.addRow(row);
                        }
                    }
                }
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error loading borrowed books: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadBorrowed.run();

        reissueButton.addActionListener(e -> {
            int selectedRow = borrowedTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(inner, "Please select a book to reissue.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
            String reissued = (String) tableModel.getValueAt(selectedRow, 5);
            if ("Yes".equals(reissued)) {
                JOptionPane.showMessageDialog(inner, "This book has already been reissued once.", "Limit Reached",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (java.sql.Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection()) {
                // Extend due date by 14 days and set transaction_type to REISSUE
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE book_transactions SET due_date = DATE_ADD(due_date, INTERVAL 14 DAY), transaction_type = 'REISSUE' WHERE id = ?")) {
                    pstmt.setInt(1, transactionId);
                    pstmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(inner, "Book reissued successfully! Due date extended by 14 days.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBorrowed.run();
            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(inner, "Error reissuing book: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showNotificationsPanel() {
        JPanel panel = createNotificationsPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "STUDENT", NAV_ITEMS, "Notifications", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createNotificationsPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Notifications");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);

        // TODO: Implement notifications functionality
        JLabel label = new JLabel("Notifications Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);

        return new com.library.views.panels.ModernCardPanel(inner);
    }

    @Override
    protected void cleanup() {
        // TODO: Implement cleanup logic
    }
}