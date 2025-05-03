package com.library.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.library.utils.DatabaseConnection;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.sql.Timestamp;

import javax.swing.table.*;

import java.awt.event.*;
import java.sql.*;


public class AdminDashboard extends BaseDashboard {
    private JButton manageLibrariansButton;
    private JButton viewReportsButton;
    private JButton fineManagementButton;
    private JButton userManagementButton;

    private static final String[] NAV_ITEMS = { "Manage Librarians", "Reports", "Fine Mgmt", "User Mgmt", "Profile" };

    public AdminDashboard(String currentUser) {
        super("Admin Dashboard - Library Management System", currentUser, "ADMIN");
        showHomePanel();
    }

    @Override
    protected JPanel createSidebar() {
        return new JPanel(); // No sidebar
    }

    private void showHomePanel() {
        com.library.views.panels.HomePanel homePanel = new com.library.views.panels.HomePanel("ADMIN");
        homePanel.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(homePanel);
    }

    private void handleNav(String nav) {
        switch (nav) {
            case "Manage Librarians":
                showManageLibrariansPanel();
                break;
            case "Reports":
                showReportsPanel();
                break;
            case "Fine Mgmt":
                showFineManagementPanel();
                break;
            case "User Mgmt":
                showUserManagementPanel();
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

    private void showManageLibrariansPanel() {
        JPanel panel = new ManageLibrariansPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "Manage Librarians", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private void showReportsPanel() {
        JPanel panel = createReportsPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "Reports", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
    
        JLabel heading = new JLabel("System Reports");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
    
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        panel.add(headingPanel, BorderLayout.NORTH);
    
        String[] labels = {
            "Total Books", "Total Users", "Total Transactions", "Total Fines Collected", "Total Categories"
        };
        String[] values = new String[labels.length];
    
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Total books
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM books");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) values[0] = String.valueOf(rs.getInt(1));
            }
    
            // Total users
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) values[1] = String.valueOf(rs.getInt(1));
            }
    
            // Total transactions
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM book_transactions");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) values[2] = String.valueOf(rs.getInt(1));
            }
    
            // Total fines collected
            try (PreparedStatement stmt = conn.prepareStatement("SELECT SUM(fine) FROM book_transactions");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) values[3] = rs.getBigDecimal(1) == null ? "₹0.00" : "₹" + rs.getBigDecimal(1).toString();
            }
    
            // Total unique categories
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(DISTINCT category) FROM books");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) values[4] = String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch reports: " + e.getMessage());
            Arrays.fill(values, "N/A");
        }
    
        JPanel statsPanel = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        statsPanel.setOpaque(false);
    
        // Set font to Comic Sans MS and bold for all labels and values
        Font reportFont = new Font("Comic Sans MS", Font.BOLD, 18);
    
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ": ");
            label.setFont(reportFont);
            label.setForeground(new Color(60, 60, 60));
    
            JLabel value = new JLabel(values[i]);
            value.setFont(reportFont);
            value.setForeground(new Color(60, 60, 60));
            statsPanel.add(label);
            statsPanel.add(value);
        }
    
        panel.add(statsPanel, BorderLayout.CENTER);
    
        return new com.library.views.panels.ModernCardPanel(panel);
    }
    

    private void showFineManagementPanel() {
        JPanel panel = createFineManagementPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "Fine Mgmt", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createFineManagementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // Heading Panel
        JLabel heading = new JLabel("Fine Management");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        mainPanel.add(headingPanel, BorderLayout.NORTH);
        
        // Tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: User-wise fines
        JPanel userFinesPanel = createUserFinesPanel();
        tabbedPane.addTab("User Fines", userFinesPanel);
        
        // Tab 2: Monthly report
        JPanel monthlyReportPanel = createMonthlyReportPanel();
        tabbedPane.addTab("Monthly Report", monthlyReportPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        return new com.library.views.panels.ModernCardPanel(mainPanel);
    }
    
    private JPanel createUserFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search User:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        
        // Table for displaying fines
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Username", "Full Name", "Book Title", "Fine Amount", "Status", "Due Date", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only the "Actions" column is editable
            }
        };
        
        JTable finesTable = new JTable(model);
        finesTable.setRowHeight(30);
        finesTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        finesTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), finesTable));
        
        JScrollPane scrollPane = new JScrollPane(finesTable);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setOpaque(false);
        
        JLabel totalLabel = new JLabel("Total Outstanding Fines: ");
        JLabel totalAmount = new JLabel("$0.00");
        totalAmount.setFont(totalAmount.getFont().deriveFont(Font.BOLD));
        
        summaryPanel.add(totalLabel);
        summaryPanel.add(totalAmount);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Load all fines initially
        loadAllFines(model, totalAmount);
        
        // Search button action
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                searchFines(model, searchTerm, totalAmount);
            }
        });
        
        // Show all button action
        showAllButton.addActionListener(e -> loadAllFines(model, totalAmount));
        
        return panel;
    }
    
    private JPanel createMonthlyReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Year and month selection
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);
        
        // Current year and previous 5 years
        JComboBox<Integer> yearCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 5; i--) {
            yearCombo.addItem(i);
        }
        
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "All Months", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        
        JButton generateButton = new JButton("Generate Report");
        
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearCombo);
        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(monthCombo);
        filterPanel.add(generateButton);
        
        // Table for monthly report
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Period", "Total Fines", "Paid Fines", "Outstanding Fines"}, 0);
        
        JTable reportTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Generate report for current year initially
        generateMonthlyReport(model, currentYear, 0);
        
        // Generate button action
        generateButton.addActionListener(e -> {
            int selectedYear = (int) yearCombo.getSelectedItem();
            int selectedMonth = monthCombo.getSelectedIndex(); // 0 = All Months
            generateMonthlyReport(model, selectedYear, selectedMonth);
        });
        
        return panel;
    }
    
    private void loadAllFines(DefaultTableModel model, JLabel totalAmountLabel) {
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String query = "SELECT bt.id, u.username, u.full_name, b.title, bt.fine, bt.status, bt.due_date " +
                            "FROM book_transactions bt " +
                            "JOIN users u ON bt.user_id = u.id " +
                            "JOIN books b ON bt.book_id = b.id " +
                            "WHERE bt.fine > 0 " +
                            "ORDER BY bt.due_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            double totalFines = 0;
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String bookTitle = rs.getString("title");
                double fine = rs.getDouble("fine");
                String status = rs.getString("status");
                java.sql.Timestamp dueDate = rs.getTimestamp("due_date");
                
                // Only count outstanding fines
                if ("OVERDUE".equals(status)) {
                    totalFines += fine;
                }
                
                model.addRow(new Object[]{
                    id, username, fullName, bookTitle, 
                    String.format("$%.2f", fine), 
                    status, 
                    new SimpleDateFormat("yyyy-MM-dd").format(dueDate),
                    "Waive"
                });
            }
            
            totalAmountLabel.setText(String.format("$%.2f", totalFines));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading fines: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchFines(DefaultTableModel model, String searchTerm, JLabel totalAmountLabel) {
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String query = "SELECT bt.id, u.username, u.full_name, b.title, bt.fine, bt.status, bt.due_date " +
                            "FROM book_transactions bt " +
                            "JOIN users u ON bt.user_id = u.id " +
                            "JOIN books b ON bt.book_id = b.id " +
                            "WHERE bt.fine > 0 AND (u.username LIKE ? OR u.full_name LIKE ?) " +
                            "ORDER BY bt.due_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            
            double totalFines = 0;
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String bookTitle = rs.getString("title");
                double fine = rs.getDouble("fine");
                String status = rs.getString("status");
                java.sql.Timestamp dueDate = rs.getTimestamp("due_date");
                
                if ("OVERDUE".equals(status)) {
                    totalFines += fine;
                }
                
                model.addRow(new Object[]{
                    id, username, fullName, bookTitle, 
                    String.format("$%.2f", fine), 
                    status, 
                    new SimpleDateFormat("yyyy-MM-dd").format(dueDate),
                    "Waive"
                });
            }
            
            totalAmountLabel.setText(String.format("$%.2f", totalFines));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error searching fines: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateMonthlyReport(DefaultTableModel model, int year, int month) {
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String query;
            PreparedStatement stmt;
            
            if (month == 0) {
                // All months of the year
                query = "SELECT MONTH(return_date) as month, " +
                        "SUM(fine) as total_fines, " +
                        "SUM(CASE WHEN status = 'RETURNED' THEN fine ELSE 0 END) as paid_fines, " +
                        "SUM(CASE WHEN status = 'OVERDUE' THEN fine ELSE 0 END) as outstanding_fines " +
                        "FROM book_transactions " +
                        "WHERE fine > 0 AND YEAR(return_date) = ? " +
                        "GROUP BY MONTH(return_date) " +
                        "ORDER BY MONTH(return_date)";
                
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, year);
            } else {
                // Specific month
                query = "SELECT DAY(return_date) as day, " +
                        "SUM(fine) as total_fines, " +
                        "SUM(CASE WHEN status = 'RETURNED' THEN fine ELSE 0 END) as paid_fines, " +
                        "SUM(CASE WHEN status = 'OVERDUE' THEN fine ELSE 0 END) as outstanding_fines " +
                        "FROM book_transactions " +
                        "WHERE fine > 0 AND YEAR(return_date) = ? AND MONTH(return_date) = ? " +
                        "GROUP BY DAY(return_date) " +
                        "ORDER BY DAY(return_date)";
                
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, year);
                stmt.setInt(2, month);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                if (month == 0) {
                    int reportMonth = rs.getInt("month");
                    double total = rs.getDouble("total_fines");
                    double paid = rs.getDouble("paid_fines");
                    double outstanding = rs.getDouble("outstanding_fines");
                    
                    // Create a Calendar instance to properly format the month name
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, reportMonth - 1);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    
                    String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
                    model.addRow(new Object[]{
                        monthName,
                        String.format("$%.2f", total),
                        String.format("$%.2f", paid),
                        String.format("$%.2f", outstanding)
                    });
                } else {
                    int day = rs.getInt("day");
                    double total = rs.getDouble("total_fines");
                    double paid = rs.getDouble("paid_fines");
                    double outstanding = rs.getDouble("outstanding_fines");
                    
                    model.addRow(new Object[]{
                        day + "/" + month + "/" + year,
                        String.format("$%.2f", total),
                        String.format("$%.2f", paid),
                        String.format("$%.2f", outstanding)
                    });
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void waiveFine(int transactionId) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to waive this fine?", "Confirm Waive", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String query = "UPDATE book_transactions SET fine = 0, status = 'RETURNED' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, transactionId);
            
            int updated = stmt.executeUpdate();
            
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "Fine waived successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to waive fine.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error waiving fine: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Button renderer and editor for the "Waive" button in the table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        private String label;
        private JTable table;
        
        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            checkBox.setOpaque(true);
            editorComponent = checkBox;
            setClickCountToStart(1);
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            ((JButton) editorComponent).setText(label);
            return editorComponent;
        }
        
        public Object getCellEditorValue() {
            if (label.equals("Waive")) {
                int transactionId = (int) table.getValueAt(table.getEditingRow(), 0);
                waiveFine(transactionId);
            }
            return label;
        }
    }

    private void showUserManagementPanel() {
        JPanel panel = createUserManagementPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "User Mgmt", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createUserManagementPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
    
        JLabel heading = new JLabel("User Management");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);
    
        String[] columnNames = {"ID", "Username", "Full Name", "Role", "Active", "Toggle"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Toggle button editable
            }
        };
    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, username, full_name, role, is_active FROM users WHERE role != 'ADMIN'");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");
                model.addRow(new Object[]{id, username, fullName, role, isActive ? "Active" : "Inactive", "Toggle"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
        }
    
        JTable table = new JTable(model);
        table.setRowHeight(30);
    
        table.getColumn("Toggle").setCellRenderer(new ButtonRenderer());
        table.getColumn("Toggle").setCellEditor(new UserButtonEditor(new JCheckBox(), model, table));
    
        JScrollPane scrollPane = new JScrollPane(table);
        inner.add(scrollPane, BorderLayout.CENTER);
    
        return new com.library.views.panels.ModernCardPanel(inner);
    }

    class UserButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private DefaultTableModel model;
    
        public UserButtonEditor(JCheckBox checkBox, DefaultTableModel model, JTable table) {
            super(checkBox);
            this.model = model;
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
    
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Toggle" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
    
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                int userId = (int) model.getValueAt(selectedRow, 0);
                boolean currentlyActive = "Active".equals(model.getValueAt(selectedRow, 4));
    
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement stmt = conn.prepareStatement("UPDATE users SET is_active = ? WHERE id = ?")) {
                    stmt.setBoolean(1, !currentlyActive);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
    
                    // Update the table model
                    model.setValueAt(!currentlyActive ? "Active" : "Inactive", selectedRow, 4);
                    JOptionPane.showMessageDialog(null, "User " + (currentlyActive ? "deactivated" : "activated") + " successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to update user status: " + ex.getMessage());
                }
            }
            isPushed = false;
            return label;
        }
    
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    
        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    

        private void showProfilePanel() {
            JPanel panel = createProfilePanel();
            com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                    "ADMIN", NAV_ITEMS, "Profile", true, panel);
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

    public class ManageLibrariansPanel extends JPanel {

        private JTable table;
        private DefaultTableModel tableModel;
        
        private JTextField usernameField;
        private JTextField emailField;
        private JTextField fullNameField;
        private JPasswordField passwordField;

        public ManageLibrariansPanel() {
            setLayout(new BorderLayout());

            JLabel title = new JLabel("Manage Librarians");
            title.setFont(new Font("Arial", Font.BOLD, 18));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            add(title, BorderLayout.NORTH);

            // Setup table
            tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Email", "Full Name", "Active", "Created At"}, 0);
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
            JPanel formPanel = new JPanel(new GridLayout(2, 5, 10, 5));

            usernameField = new JTextField();
            emailField = new JTextField();
            fullNameField = new JTextField();
            passwordField = new JPasswordField();
            JButton addButton = new JButton("Add Librarian");
            JButton deleteButton = new JButton("Delete Selected Librarian");
            formPanel.add(deleteButton);

            deleteButton.addActionListener(e -> deleteSelectedLibrarian());
            formPanel.add(new JLabel("Username"));
            formPanel.add(new JLabel("Email"));
            formPanel.add(new JLabel("Full Name"));
            formPanel.add(new JLabel("Password"));
            formPanel.add(new JLabel()); // empty cell

            formPanel.add(usernameField);
            formPanel.add(emailField);
            formPanel.add(fullNameField);
            formPanel.add(passwordField);
            formPanel.add(addButton);

            add(formPanel, BorderLayout.SOUTH);
            addButton.addActionListener(e -> addLibrarian());
            loadLibrarians();
        }

        private void addLibrarian() {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String password = new String(passwordField.getPassword());
        
            if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
        
            try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
                String query = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, 'LIBRARIAN')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password); // You should hash this eventually
                stmt.setString(3, email);
                stmt.setString(4, fullName);
        
                int inserted = stmt.executeUpdate();
        
                if (inserted > 0) {
                    JOptionPane.showMessageDialog(this, "Librarian added successfully.");
                    tableModel.setRowCount(0); // Clear table
                    loadLibrarians();          // Reload table
                    usernameField.setText("");
                    emailField.setText("");
                    fullNameField.setText("");
                    passwordField.setText("");
                }
        
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        private void deleteSelectedLibrarian() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a librarian to delete.");
                return;
            }
        
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this librarian?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        
            int userId = (int) tableModel.getValueAt(selectedRow, 0); // assuming ID is in column 0
        
            try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
                String query = "DELETE FROM users WHERE id = ? AND role = 'LIBRARIAN'";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
        
                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Librarian deleted successfully.");
                    tableModel.setRowCount(0); // clear table
                    loadLibrarians();          // reload table
                }
        
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        private void loadLibrarians() {
            try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
                String query = "SELECT id, username, email, full_name, is_active, created_at FROM users WHERE role = 'LIBRARIAN'";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at")
                    };
                    tableModel.addRow(row);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load librarians: " + e.getMessage());
            }
    }
}

    @Override
    protected void cleanup() {
        // TODO: Implement cleanup logic
    }
}