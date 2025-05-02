package com.library.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class BaseDashboard extends JFrame {
    protected JPanel mainPanel;
    protected JPanel contentPanel;
    protected JPanel sidebarPanel;
    protected JButton logoutButton;
    protected String currentUser;
    protected String userRole;

    public BaseDashboard(String title, String currentUser, String userRole) {
        this.currentUser = currentUser;
        this.userRole = userRole;
        initializeUI(title);
    }

    private void initializeUI(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());

        // Create sidebar panel
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    protected abstract JPanel createSidebar();

    protected void showContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    protected void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            cleanup();
            new LoginView().setVisible(true);
            dispose();
        }
    }

    protected void cleanup() {
        // Override in subclasses if needed
    }

    protected JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + currentUser);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(userLabel, BorderLayout.EAST);

        return headerPanel;
    }

    protected JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        return button;
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    protected int showConfirm(String message) {
        return JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION
        );
    }
} 