package com.library.views;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends BaseDashboard {
    private JButton manageLibrariansButton;
    private JButton viewReportsButton;
    private JButton fineManagementButton;
    private JButton userManagementButton;

    public AdminDashboard(String currentUser) {
        super("Admin Dashboard - Library Management System", currentUser, "ADMIN");
    }

    @Override
    protected JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Create buttons
        manageLibrariansButton = createMenuButton("Manage Librarians");
        viewReportsButton = createMenuButton("View Reports");
        fineManagementButton = createMenuButton("Fine Management");
        userManagementButton = createMenuButton("User Management");
        logoutButton = createMenuButton("Logout");

        // Style buttons
        Color buttonColor = new Color(70, 70, 70);
        Color textColor = Color.WHITE;
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);

        for (JButton button : new JButton[]{manageLibrariansButton, viewReportsButton, 
                fineManagementButton, userManagementButton, logoutButton}) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFont(buttonFont);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> handleButtonClick(button));
            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10));
        }

        // Add some space at the bottom
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void handleButtonClick(JButton button) {
        if (button == manageLibrariansButton) {
            showManageLibrariansPanel();
        } else if (button == viewReportsButton) {
            showReportsPanel();
        } else if (button == fineManagementButton) {
            showFineManagementPanel();
        } else if (button == userManagementButton) {
            showUserManagementPanel();
        } else if (button == logoutButton) {
            handleLogout();
        }
    }

    private void showManageLibrariansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Manage Librarians"), BorderLayout.NORTH);
        
        // TODO: Implement librarian management functionality
        JLabel label = new JLabel("Librarian Management Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        showContent(panel);
    }

    private void showReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("View Reports"), BorderLayout.NORTH);
        
        // TODO: Implement reports functionality
        JLabel label = new JLabel("Reports Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        showContent(panel);
    }

    private void showFineManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("Fine Management"), BorderLayout.NORTH);
        
        // TODO: Implement fine management functionality
        JLabel label = new JLabel("Fine Management Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        showContent(panel);
    }

    private void showUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeaderPanel("User Management"), BorderLayout.NORTH);
        
        // TODO: Implement user management functionality
        JLabel label = new JLabel("User Management Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        showContent(panel);
    }

    @Override
    protected void cleanup() {
        // TODO: Implement cleanup logic
    }
} 