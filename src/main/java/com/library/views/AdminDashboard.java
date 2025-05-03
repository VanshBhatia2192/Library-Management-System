package com.library.views;

import javax.swing.*;
import java.awt.*;

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
        JPanel panel = createManageLibrariansPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "Manage Librarians", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createManageLibrariansPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Manage Librarians");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);
        JLabel label = new JLabel("Librarian Management Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);
        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showReportsPanel() {
        JPanel panel = createReportsPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "Reports", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createReportsPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("View Reports");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);
        JLabel label = new JLabel("Reports Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);
        return new com.library.views.panels.ModernCardPanel(inner);
    }

    private void showFineManagementPanel() {
        JPanel panel = createFineManagementPanel();
        com.library.views.panels.ThemedPanel themed = new com.library.views.panels.ThemedPanel(
                "ADMIN", NAV_ITEMS, "Fine Mgmt", true, panel);
        themed.addPropertyChangeListener("navigate", evt -> handleNav((String) evt.getNewValue()));
        showContent(themed);
    }

    private JPanel createFineManagementPanel() {
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        JLabel heading = new JLabel("Fine Management");
        heading.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        heading.setForeground(new Color(60, 60, 60));
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        inner.add(headingPanel, BorderLayout.NORTH);
        JLabel label = new JLabel("Fine Management Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);
        return new com.library.views.panels.ModernCardPanel(inner);
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
        JLabel label = new JLabel("User Management Panel - Coming Soon");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(label, BorderLayout.CENTER);
        return new com.library.views.panels.ModernCardPanel(inner);
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

    @Override
    protected void cleanup() {
        // TODO: Implement cleanup logic
    }
}