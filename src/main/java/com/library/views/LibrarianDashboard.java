package com.library.views;

import javax.swing.*;
import java.awt.*;
import com.library.views.panels.ManageBooksPanel;
import com.library.views.panels.StudentRecordsPanel;

public class LibrarianDashboard extends BaseDashboard {
    private String username;

    public LibrarianDashboard(String username) {
        super("Librarian Dashboard", username, "LIBRARIAN");
        this.username = username;
    }

    @Override
    protected JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setPreferredSize(new Dimension(200, 0));

        // Create buttons with consistent styling
        JButton manageBooksBtn = createSidebarButton("Manage Books");
        JButton viewIssuedBtn = createSidebarButton("View Issued Books");
        JButton studentRecordsBtn = createSidebarButton("Student Records");
        JButton overdueNoticesBtn = createSidebarButton("Overdue Notices");
        JButton logoutBtn = createSidebarButton("Logout");

        // Add action listeners
        manageBooksBtn.addActionListener(e -> showManageBooks());
        viewIssuedBtn.addActionListener(e -> showIssuedBooks());
        studentRecordsBtn.addActionListener(e -> showStudentRecords());
        overdueNoticesBtn.addActionListener(e -> showOverdueNotices());
        logoutBtn.addActionListener(e -> handleLogout());

        // Add buttons to sidebar
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(manageBooksBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(viewIssuedBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(studentRecordsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(overdueNoticesBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 51, 51));
            }
        });

        return button;
    }

    private void showManageBooks() {
        contentPanel.removeAll();
        contentPanel.add(new ManageBooksPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showIssuedBooks() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Issued Books", SwingConstants.CENTER), BorderLayout.NORTH);

        String[] columns = {"Book Title", "Borrower", "Borrow Date", "Due Date", "Status"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable issuedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(issuedTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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
            JOptionPane.showMessageDialog(panel, "Error loading issued books: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showStudentRecords() {
        contentPanel.removeAll();
        contentPanel.add(new StudentRecordsPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOverdueNotices() {
        contentPanel.removeAll();
        contentPanel.add(new JLabel("Overdue Notices - Coming Soon"));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    protected void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

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