package com.library.views;

import javax.swing.*;
import java.awt.*;
import com.library.views.panels.ManageBooksPanel;
import com.library.views.panels.StudentRecordsPanel;

public class LibrarianDashboard extends BaseDashboard {
    private String username;
    private static final String[] NAV_ITEMS = { "Manage Books", "Issued Books", "Student Records", "Overdue Notices",
            "Profile" };

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
        // If there are action/search buttons, wrap them in ModernSearchPanel here
        JLabel label = new JLabel("Overdue Notices - Coming Soon", SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);
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
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Edit Profile");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);
        JLabel label = new JLabel("Profile editing coming soon.", SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);
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